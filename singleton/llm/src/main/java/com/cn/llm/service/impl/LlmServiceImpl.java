       package com.cn.llm.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.common.enums.FilePathEnum;
import com.cn.common.utils.UploadUtil;
import com.cn.common.utils.RedisUtils;
import com.cn.llm.config.OpenRouterConfig;
import com.cn.llm.dto.SubmitMessageDto;
import com.cn.llm.excepitons.LlmException;
import com.cn.llm.registry.RemoteRegistryStore;
import com.cn.llm.service.LlmService;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.alibaba.fastjson.JSON.parseObject;

/**
 * LLM 服务实现类
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Slf4j
@Service
public class LlmServiceImpl implements LlmService {

    private final OpenRouterConfig openRouterConfig;
    private final WebClient webClient;
    private final WebClient httpClient;
    private final ThreadLocal<StringBuilder> bufferThreadLocal = ThreadLocal.withInitial(StringBuilder::new);
	private final ThreadLocal<StringBuilder> contentBufferThreadLocal = ThreadLocal.withInitial(StringBuilder::new);
	private final ThreadLocal<StringBuilder> reasoningBufferThreadLocal = ThreadLocal.withInitial(StringBuilder::new);
    private final RedissonClient redissonClient;
    private final RemoteRegistryStore remoteRegistryStore;
    private final UploadUtil uploadUtil;
    private final RedisUtils redisUtils;

    private final ThreadLocal<java.util.List<java.util.Map<String, Object>>> imagesBufferThreadLocal = ThreadLocal.withInitial(java.util.ArrayList::new);
    private final ThreadLocal<java.util.List<java.util.Map<String, Object>>> citationsBufferThreadLocal = ThreadLocal.withInitial(java.util.ArrayList::new);
    private final ThreadLocal<Integer> imagePlaceholderCountThreadLocal = ThreadLocal.withInitial(() -> 0);

    public LlmServiceImpl(OpenRouterConfig openRouterConfig,
                          RedissonClient redissonClient,
                          RemoteRegistryStore remoteRegistryStore,
                          UploadUtil uploadUtil,
                          RedisUtils redisUtils) {
        this.openRouterConfig = openRouterConfig;
        this.redissonClient = redissonClient;
        this.remoteRegistryStore = remoteRegistryStore;
        this.uploadUtil = uploadUtil;
        this.redisUtils = redisUtils;

        Integer connectTimeout = openRouterConfig.getConnectTimeout();
        Integer readTimeout = openRouterConfig.getReadTimeout();

        HttpClient httpClientForSse = HttpClient.create();
        if (connectTimeout != null && connectTimeout > 0) {
            httpClientForSse = httpClientForSse.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);
        }
        if (readTimeout != null && readTimeout > 0) {
            httpClientForSse = httpClientForSse.doOnConnected(conn ->
                    conn.addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS)));
        }

        this.webClient = WebClient.builder()
                .baseUrl(openRouterConfig.getBaseUrl())
                .defaultHeader("Authorization", "Bearer " + openRouterConfig.getApiKey())
                .defaultHeader("Content-Type", "application/json")
                .clientConnector(new ReactorClientHttpConnector(httpClientForSse))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1))
                .build();

        this.httpClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1))
                .build();
    }

    @Override
    public void submitMessage(SubmitMessageDto dto) {
        boolean noText = (dto.getText() == null || dto.getText().isBlank());
        boolean noImages = (dto.getImageUrls() == null || dto.getImageUrls().isEmpty());
        boolean noPdf = (dto.getPdfFiles() == null || dto.getPdfFiles().isEmpty());
        boolean noAudio = (dto.getAudioFiles() == null || dto.getAudioFiles().isEmpty());
        if (noText && noImages && noPdf && noAudio) {
            throw new LlmException("请求参数错误");
        }

        // 校验附件总数限制（imageUrls + pdfFiles + audioFiles）
        int imageCount = dto.getImageUrls() == null ? 0 : dto.getImageUrls().size();
        int pdfCount = dto.getPdfFiles() == null ? 0 : dto.getPdfFiles().size();
        int audioCount = dto.getAudioFiles() == null ? 0 : dto.getAudioFiles().size();
        int totalAttachments = imageCount + pdfCount + audioCount;
        Integer maxCount = Optional.ofNullable(openRouterConfig.getUpload()).map(OpenRouterConfig.Upload::getMaxAttachmentCount).orElse(10);
        if (totalAttachments > maxCount) {
            throw new LlmException("附件数量超出限制，最多允许 " + maxCount + " 个");
        }

        // 构造 content_json 为多模态数组
        List<Map<String, Object>> parts = new ArrayList<>();
        if (dto.getText() != null && !dto.getText().isBlank()) {
            Map<String, Object> text = new HashMap<>();
            text.put("type", "text");
            text.put("text", dto.getText());
            parts.add(text);
        }
        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            for (String url : dto.getImageUrls()) {
                if (url == null || url.isBlank()) continue;
                Map<String, Object> img = new HashMap<>();
                img.put("type", "image_url");
                Map<String, Object> imageUrl = new HashMap<>();
                imageUrl.put("url", url);
                img.put("image_url", imageUrl);
                parts.add(img);
            }
        }
        // 多个 PDF 文件
        if (dto.getPdfFiles() != null && !dto.getPdfFiles().isEmpty()) {
            for (com.cn.llm.dto.PdfFileDto pdf : dto.getPdfFiles()) {
                if (pdf == null || pdf.getUrl() == null || pdf.getUrl().isBlank()) continue;
                String url = pdf.getUrl();
                Map<String, Object> filePart = new HashMap<>();
                filePart.put("type", "file");
                Map<String, Object> fileObj = new HashMap<>();
                String safeName = requirePdfStaticFilename();
                fileObj.put("filename", safeName);
                fileObj.put("file_data", url);
                filePart.put("file", fileObj);
                parts.add(filePart);
            }
        }
        // 多个音频：后端拉取 URL，检查大小，转 base64 并推断 format
        if (dto.getAudioFiles() != null && !dto.getAudioFiles().isEmpty()) {
            for (com.cn.llm.dto.AudioFileDto audio : dto.getAudioFiles()) {
                if (audio == null || audio.getUrl() == null || audio.getUrl().isBlank()) continue;
                String audioUrl = audio.getUrl();
                try {
                    long maxBytes = Optional.ofNullable(openRouterConfig.getAudio()).map(OpenRouterConfig.Audio::getMaxSizeBytes).orElse(20L * 1024 * 1024);
                    Map<String, Object> headInfo = httpClient.head().uri(audioUrl)
                            .exchangeToMono(res -> {
                                var headers = res.headers().asHttpHeaders();
                                Long len = headers.getContentLength() > 0 ? headers.getContentLength() : null;
                                String ct = headers.getContentType() != null ? headers.getContentType().toString() : null;
                                Map<String, Object> map = new HashMap<>();
                                map.put("len", len);
                                map.put("ct", ct);
                                return Mono.just(map);
                            })
                            .onErrorResume(e -> Mono.just(Collections.emptyMap()))
                            .block(Duration.ofSeconds(10));
                    Long contentLength = headInfo == null ? null : (Long) headInfo.get("len");
                    String contentType = headInfo == null ? null : (String) headInfo.get("ct");
                    if (contentLength != null && contentLength > maxBytes) {
                        throw new LlmException("音频文件过大，超过限制");
                    }
                    byte[] bytes = httpClient.get().uri(audioUrl)
                            .retrieve()
                            .bodyToMono(byte[].class)
                            .map(data -> {
                                if (contentLength == null && data != null && data.length > maxBytes) {
                                    throw new LlmException("音频文件过大，超过限制");
                                }
                                return data;
                            })
                            .block(Duration.ofSeconds(30));
                    if (bytes == null || bytes.length == 0) {
                        throw new LlmException("拉取音频失败");
                    }
                    if (bytes.length > maxBytes) {
                        throw new LlmException("音频文件过大，超过限制");
                    }
                    String format = inferAudioFormat(contentType, audioUrl);
                    String base64 = java.util.Base64.getEncoder().encodeToString(bytes);
                    Map<String, Object> audioPart = new HashMap<>();
                    audioPart.put("type", "input_audio");
                    Map<String, Object> audioObj = new HashMap<>();
                    audioObj.put("data", base64);
                    audioObj.put("format", format);
                    audioPart.put("input_audio", audioObj);
                    parts.add(audioPart);
                } catch (Exception e) {
                    throw new LlmException("音频处理失败: " + e.getMessage());
                }
            }
        }
		String contentJson = JSON.toJSONString(parts);
        
        // Redis key: chat:session123
        String sessionKey = "chat:" + dto.getSessionId();
        
        // 查询该会话的最后一条消息（Redis List中的第一个元素）
        List<Object> lastMessages = redisUtils.listGet(sessionKey, 0, 0);
        
        Map<String, Object> newMessage = new HashMap<>();
        newMessage.put("role", "user");
        newMessage.put("content", JSON.parseArray(contentJson));
        newMessage.put("timestamp", System.currentTimeMillis());
        
        String newMessageJson = JSON.toJSONString(newMessage);
        
        // 如果最后一条消息是用户发的，则覆盖该消息；否则插入新消息
        if (!lastMessages.isEmpty()) {
            String lastMessageStr = (String) lastMessages.getFirst();
            JSONObject lastMessageObj = JSON.parseObject(lastMessageStr);
            if ("user".equals(lastMessageObj.getString("role"))) {
                // 覆盖最后一条用户消息（使用Redis List的set操作）
                redisUtils.getRedisTemplate().opsForList().set(sessionKey, 0, newMessageJson);
            } else {
                // 插入新的用户消息到List头部
                redisUtils.listPush(sessionKey, newMessageJson);
            }
        } else {
            // 会话为空，插入第一条消息
            redisUtils.listPush(sessionKey, newMessageJson);
        }
        
        // 设置会话过期时间（从配置读取）
        redisUtils.expire(sessionKey, openRouterConfig.getChat().getSessionTtlSeconds());
        
        // 建立会话与用户的关联关系（如果是新会话的话）
        String sessionOwnerKey = "chat:session:owner:" + dto.getSessionId();
        if (!redisUtils.hasKey(sessionOwnerKey)) {
            Long currentUserId = com.cn.common.utils.UserUtils.getCurrentLoginId();
            redisUtils.setValueTimeout(sessionOwnerKey, String.valueOf(currentUserId), openRouterConfig.getChat().getSessionTtlSeconds());
        }
    }

    private String deriveFilenameFromUrl(String url) {
        int q = url.indexOf('?');
        String base = q >= 0 ? url.substring(0, q) : url;
        int slash = base.lastIndexOf('/');
        if (slash >= 0 && slash < base.length() - 1) {
            return base.substring(slash + 1);
        }
        return requirePdfStaticFilename();
    }

    private String sanitizeFilename(String candidate) {
        if (candidate == null || candidate.isBlank()) return requirePdfStaticFilename();
        // 移除路径片段与危险字符，仅保留字母数字、点、下划线、连字符
        String name = candidate.replace("\\", "/");
        int slash = name.lastIndexOf('/');
        if (slash >= 0) name = name.substring(slash + 1);
        name = name.replaceAll("[^A-Za-z0-9._-]", "_");
        // 避免隐藏/空名称
        if (name.equals(".") || name.equals("..") || name.isBlank()) {
            name = requirePdfStaticFilename();
        }
        // 如果没有扩展名，默认 .pdf
        if (!name.contains(".")) {
            name = name + ".pdf";
        }
        // 限制最大长度
        if (name.length() > 120) {
            int dot = name.lastIndexOf('.');
            String base = dot > 0 ? name.substring(0, dot) : name;
            String ext = dot > 0 ? name.substring(dot) : "";
            base = base.substring(0, Math.min(100, base.length()));
            name = base + ext;
        }
        return name;
    }

    private String requirePdfStaticFilename() {
        return openRouterConfig.getPlugins().getFileParser().getPdf().getStaticFilename();
    }

    private String inferAudioFormat(String contentType, String url) {
        java.util.List<String> allowed = Optional.ofNullable(openRouterConfig.getAudio()).filter(a -> a.getAllowedFormats() != null && !a.getAllowedFormats().isEmpty()).map(OpenRouterConfig.Audio::getAllowedFormats).orElse(Arrays.asList("wav", "mp3"));
        String defaultFormat = java.util.Optional.ofNullable(openRouterConfig.getAudio())
                .map(a -> a.getDefaultFormat() == null || a.getDefaultFormat().isBlank() ? "wav" : a.getDefaultFormat())
                .orElse("wav");
        java.util.Set<String> allowedSet = new java.util.HashSet<>();
        for (String f : allowed) {
            if (f != null && !f.isBlank()) {
                allowedSet.add(f.toLowerCase());
            }
        }
        if (contentType != null && !contentType.isBlank()) {
            String ct = contentType.toLowerCase();
            for (String f : allowedSet) {
                if (ct.contains(f)) {
                    return f;
                }
            }
        }
        if (url != null && !url.isBlank()) {
            String lower = url.toLowerCase();
            for (String f : allowedSet) {
                if (lower.endsWith("." + f)) {
                    return f;
                }
            }
        }
        return defaultFormat;
    }

	@Override
 		public SseEmitter chatStream(String sessionId, String modelId,
							 String enableWebSearchParam,
							 String generateImagesParam,
							 String token) {
		// 通过 token 获取用户 ID，同时校验 token 有效性
		Long currentUserId = com.cn.common.utils.UserUtils.getLoginIdByToken(token);
		
		// 验证sessionId是否属于当前用户
		String sessionOwnerKey = "chat:session:owner:" + sessionId;
		String sessionOwner = (String) redisUtils.getValue(sessionOwnerKey);
		if (sessionOwner != null && !sessionOwner.equals(String.valueOf(currentUserId))) {
			SseEmitter emitter = new SseEmitter(Duration.ofMinutes(10).toMillis());
			try {
				emitter.send(SseEmitter.event().name("error").data("无权限访问该会话"));
				emitter.complete();
			} catch (IOException e) {
				log.error("发送无权限错误失败", e);
				emitter.completeWithError(e);
			}
			return emitter;
		}

		SseEmitter emitter = new SseEmitter(Duration.ofMinutes(10).toMillis());
		CompletableFuture.runAsync(() -> {
			String lockKey = "chat:lock:" + sessionId;
			RLock lock = redissonClient.getLock(lockKey);
			boolean locked = false;
			try {
				locked = lock.tryLock(0, 600, TimeUnit.SECONDS);
				if (!locked) {
					emitter.send(SseEmitter.event().name("done").data("当前会话正在处理中，请稍后再试"));
					emitter.complete();
					return;
				}

				String resolvedModelId = modelId;
				if (resolvedModelId == null || resolvedModelId.isBlank()) {
                    resolvedModelId = getDefaultModel();
				}
				final String slug = remoteRegistryStore == null ? null : remoteRegistryStore.resolveModelSlugById(resolvedModelId);
				if (slug == null || slug.isBlank()) {
					try {
						emitter.send(SseEmitter.event().name("error").data("模型不存在"));
						emitter.complete();
					} catch (IOException e) {
						log.error("发送模型不存在错误失败", e);
						emitter.completeWithError(e);
					}
					return;
				}
				// 判定是否有待处理的用户消息
				if (!needsReply(sessionId)) {
					emitter.send(SseEmitter.event().name("done").data("当前没有需要回复的消息"));
					emitter.complete();
					return;
				}
				// 读取最近窗口上下文（取40条）
				List<Map<String, Object>> messages = getChatMessages(sessionId, 40);

				int reserve = Optional.ofNullable(openRouterConfig.getTruncation().getResponseTokenReserve()).orElse(2000);
				Integer modelMax = remoteRegistryStore.getMaxTokensById(resolvedModelId);
				int maxContext;
				if (modelMax != null && modelMax > 0) {
					maxContext = modelMax;
				} else {
					maxContext = 8192; 
					log.warn("无法从远程注册表获取模型 {} 的最大上下文 Token，已回退到默认值 {}", resolvedModelId, maxContext);
				}
				int budget = Math.max(1000, maxContext - reserve);
				List<Map<String, Object>> sourceForTruncation = messages;
				if (Optional.ofNullable(openRouterConfig.getTruncation()).map(com.cn.llm.config.OpenRouterConfig.Truncation::getEnableCompression).orElse(Boolean.TRUE)) {
					sourceForTruncation = compressMessagesToBudget(messages, budget);
				}
				List<Map<String, Object>> truncated = truncateMessagesToBudget(sourceForTruncation, budget);

				// 解析 URL 参数
				Boolean qEnableWeb = parseBooleanQuery(enableWebSearchParam);
				Boolean qGenerateImages = parseBooleanQuery(generateImagesParam);

				boolean useImages = Boolean.TRUE.equals(qGenerateImages);
				boolean useWeb = Boolean.TRUE.equals(qEnableWeb) && Boolean.TRUE.equals(openRouterConfig.getPlugins().getWeb().getEnabled());
				boolean useReasoning = Optional.ofNullable(openRouterConfig.getReasoning()).map(OpenRouterConfig.Reasoning::getEnabled).orElse(false);

				// 在生成图片模式下，不携带历史上下文，仅保留最后一条用户消息
				if (useImages) {
					java.util.List<java.util.Map<String, Object>> onlyLastUser = new java.util.ArrayList<>(1);
					for (int i = messages.size() - 1; i >= 0; i--) {
						Object role = messages.get(i).get("role");
						if ("user".equals(role)) {
							java.util.Map<String, Object> msg = new java.util.HashMap<>();
							msg.put("role", "user");
							msg.put("content", messages.get(i).get("content"));
							onlyLastUser.add(msg);
							break;
						}
					}
					if (onlyLastUser.isEmpty() && !messages.isEmpty()) {
						java.util.Map<String, Object> last = new java.util.HashMap<>();
						java.util.Map<String, Object> tail = messages.getLast();
						last.put("role", tail.get("role"));
						last.put("content", tail.get("content"));
						onlyLastUser.add(last);
					}
					truncated = onlyLastUser;
				}

				Map<String, Object> requestBody = new HashMap<>();
				requestBody.put("model", slug);
				requestBody.put("messages", truncated);
				requestBody.put("stream", true);
//				requestBody.put("max_tokens", reserve);
				if (useImages) {
					requestBody.put("modalities", Arrays.asList("text", "image"));
				}

				if (useReasoning) {
					Integer maxTokens = Optional.ofNullable(openRouterConfig.getReasoning().getMaxTokens()).orElse(8000);
					Map<String, Object> reasoning = new HashMap<>();
					reasoning.put("max_tokens", maxTokens);
					requestBody.put("reasoning", reasoning);
				}

				// 根据内容注入插件（仅 file-parser 依赖消息内容；web 走 URL 开关）
				List<Map<String, Object>> plugins = new ArrayList<>();
				 if (containsFileContent(truncated) && Boolean.TRUE.equals(openRouterConfig.getPlugins().getFileParser().getEnabled())) {
					Map<String, Object> plugin = new HashMap<>();
					plugin.put("id", "file-parser");
					Map<String, Object> pdf = new HashMap<>();
					String engine = Optional.ofNullable(openRouterConfig.getPlugins().getFileParser().getPdf().getEngine()).orElse("mistral-ocr");
					pdf.put("engine", engine);
					plugin.put("pdf", pdf);
					plugins.add(plugin);
				}
				if (useWeb) {
					Map<String, Object> web = new HashMap<>();
					web.put("id", "web");
					// 添加搜索结果最大数量配置
					Integer maxResults = Optional.ofNullable(openRouterConfig.getPlugins().getWeb().getMaxResults()).orElse(5);
					web.put("max_results", maxResults);
					plugins.add(web);
				}
				if (!plugins.isEmpty()) {
					requestBody.put("plugins", plugins);
				}
				log.info("requestBody: {}", requestBody);
				System.out.println(requestBody);
				webClient.post()
						.uri("/chat/completions")
						.body(BodyInserters.fromValue(requestBody))
						.retrieve()
						.onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (clientResponse -> clientResponse.bodyToMono(String.class)
								.flatMap(errorBody -> {
									log.error("OpenRouter API 错误响应: {}", errorBody);
									try {
										final JSONObject errorJson = parseObject(errorBody);
										if (errorJson.containsKey("error")) {
											final Object errorObj = errorJson.get("error");
											String errorMessage;
											if (errorObj instanceof String) {
												errorMessage = (String) errorObj;
											} else if (errorObj instanceof JSONObject errorDetails) {
												errorMessage = errorDetails.getString("message");
												if (errorMessage == null) {
													errorMessage = errorDetails.toJSONString();
												}
											} else {
												errorMessage = errorBody;
											}
											return Mono.error(new LlmException("OpenRouter API 错误: " + errorMessage));
										}
									} catch (Exception e) {
										log.error("解析错误响应失败", e);
									}
									return Mono.error(new LlmException("OpenRouter API 调用失败: " + errorBody));
								})))
						.bodyToFlux(DataBuffer.class)
						.map(dataBuffer -> {
							byte[] bytes = new byte[dataBuffer.readableByteCount()];
							dataBuffer.read(bytes);
							DataBufferUtils.release(dataBuffer);
							return new String(bytes, StandardCharsets.UTF_8);
						})
//						.doOnNext(chunk -> {
//							log.info("收到响应块: {}", chunk);
//
//						})
						.subscribe(
								chunk -> {
									try {
										processChunk(chunk, emitter);
									} catch (IOException e) {
										log.error("处理响应块失败", e);
										emitter.completeWithError(e);
									}
								},
								error -> {
									log.error("OpenRouter API 调用失败", error);
									bufferThreadLocal.remove();
									contentBufferThreadLocal.remove();
									reasoningBufferThreadLocal.remove();
									citationsBufferThreadLocal.remove();
									try {
										emitter.send(SseEmitter.event().name("error").data("服务器繁忙 请稍后再试"));
										emitter.complete();
									} catch (IOException e) {
										log.error("发送错误消息失败", e);
										emitter.completeWithError(e);
									}
								},
								() -> {
									log.info("OpenRouter API 响应完成");
									// 清理缓冲区
									String assistantText = contentBufferThreadLocal.get().toString();
									String reasoningText = reasoningBufferThreadLocal.get().toString();
									bufferThreadLocal.remove();
									contentBufferThreadLocal.remove();
									reasoningBufferThreadLocal.remove();
									// 入库助手消息（文本+图片）
									java.util.List<java.util.Map<String, Object>> content = new java.util.ArrayList<>();
									if (!assistantText.isEmpty()) {
										java.util.Map<String, Object> text = new java.util.HashMap<>();
										text.put("type", "text");
										text.put("text", assistantText);
										content.add(text);
									}
									// 将 base64/非法图片作为占位文本写入 DB（不影响 SSE）
									int placeholderCount = java.util.Optional.ofNullable(imagePlaceholderCountThreadLocal.get()).orElse(0);
									for (int i = 0; i < placeholderCount; i++) {
										java.util.Map<String, Object> textPh = new java.util.HashMap<>();
										textPh.put("type", "text");
										textPh.put("text", "[image]");
										content.add(textPh);
									}
									// 合并生成的图片到 content_json
									java.util.List<java.util.Map<String, Object>> imagesFinal = imagesBufferThreadLocal.get();
									// 引用信息同样只用于 SSE，不入库
									java.util.List<java.util.Map<String, Object>> citationsFinal = citationsBufferThreadLocal.get();
									if (imagesFinal != null && !imagesFinal.isEmpty()) {
										content.addAll(imagesFinal);
									}
									// 保存助手回复到Redis
									addAssistantMessage(sessionId, com.alibaba.fastjson2.JSON.toJSONString(content));
									
									// 统计：API调用成功
									recordApiCall(slug);
									
									try {
										java.util.Map<String, Object> finalPayload = new java.util.HashMap<>();
										finalPayload.put("content", assistantText);
										finalPayload.put("reasoning_content", java.util.Optional.of(reasoningText).orElse(""));
										finalPayload.put("images", imagesFinal == null ? java.util.Collections.emptyList() : imagesFinal);
										finalPayload.put("citations", citationsFinal == null ? java.util.Collections.emptyList() : citationsFinal);
										log.info("最终输出载荷: {}", finalPayload);
										System.out.println("最终输出载荷(JSON): " + com.alibaba.fastjson2.JSON.toJSONString(finalPayload));
										emitter.send(org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event().name("done").data(finalPayload));
										emitter.complete();
									} catch (java.io.IOException e) {
										log.error("发送完成消息失败", e);
										emitter.completeWithError(e);
									} finally {
										imagesBufferThreadLocal.remove();
										citationsBufferThreadLocal.remove();
										imagePlaceholderCountThreadLocal.remove();
									}
                                }
					);

			} catch (Exception e) {
				log.error("处理会话流式聊天失败", e);
				try {
					emitter.send(SseEmitter.event().name("error").data("处理请求失败: " + e.getMessage()));
					emitter.complete();
				} catch (IOException ioException) {
					log.error("发送错误消息失败", ioException);
					emitter.completeWithError(ioException);
				}
			} finally {
				if (locked) {
					try {
						lock.unlock();
					} catch (Exception ignored) {}
				}
			}
		});
		return emitter;
	}

	private String getDefaultModel() {
        // 1) 优先使用显式指定的 auto.modelId（若存在且非空）
        String specifiedId = Optional.ofNullable(openRouterConfig.getRemoteRegistry())
                .map(OpenRouterConfig.RemoteRegistry::getAuto)
                .map(OpenRouterConfig.Auto::getModelId)
                .filter(id -> !id.isBlank())
                .orElse(null);
        log.info("配置中指定的模型ID: {}", specifiedId);
        if (specifiedId != null) {
            // 验证指定的模型ID是否存在于远程注册表中
            Object model = remoteRegistryStore == null ? null : remoteRegistryStore.getById(specifiedId);
            if (model != null) {
                log.info("配置中指定的模型ID有效，使用: {}", specifiedId);
                return specifiedId;
            } else {
                log.warn("配置中指定的模型ID不存在于远程注册表中，回退到prefer逻辑: {}", specifiedId);
            }
        }

        // 2) 其次使用 auto.prefer（FREE/PAID）；若未配置，默认 FREE
        OpenRouterConfig.FilterMode prefer = Optional.ofNullable(openRouterConfig.getRemoteRegistry())
                .map(OpenRouterConfig.RemoteRegistry::getAuto)
                .map(OpenRouterConfig.Auto::getPrefer)
                .orElse(OpenRouterConfig.FilterMode.FREE);
        log.info("偏好模式: {}", prefer);

        String candidate = getCandidate(prefer);
        log.info("获取到的候选模型ID: {}", candidate);
        return candidate;
	}

	private String getCandidate(OpenRouterConfig.FilterMode prefer) {
		String candidate;
		if (prefer == OpenRouterConfig.FilterMode.PAID) {
			candidate = remoteRegistryStore == null ? null : remoteRegistryStore.getFirstPaidTextModelId();
			log.info("获取到的首个付费模型ID: {}", candidate);
			if (candidate == null || candidate.isBlank()) {
				candidate = remoteRegistryStore == null ? null : remoteRegistryStore.getFirstFreeTextModelId();
				log.info("付费模型为空，获取到的首个免费模型ID: {}", candidate);
			}
		} else {
			candidate = remoteRegistryStore == null ? null : remoteRegistryStore.getFirstFreeTextModelId();
			log.info("获取到的首个免费模型ID: {}", candidate);
			if (candidate == null || candidate.isBlank()) {
				candidate = remoteRegistryStore == null ? null : remoteRegistryStore.getFirstPaidTextModelId();
				log.info("免费模型为空，获取到的首个付费模型ID: {}", candidate);
			}
		}
		return candidate;
	}

	@Override
	public String getDefaultModelId() {
		return getDefaultModel();
	}

	@Override
	public Object getDefaultModelObject() {
		String id = getDefaultModel();
		log.info("获取到的默认模型ID: {}", id);
		if (id == null || id.isBlank()) {
			log.error("默认模型ID为空");
			throw new LlmException("模型不存在");
		}
		Object model = remoteRegistryStore.getById(id);
		log.info("从远程注册表获取的模型对象: {}", model != null ? "存在" : "null");
		if (model == null) {
			log.error("远程注册表中未找到模型ID: {}", id);
			throw new LlmException("模型不存在");
		}
		return model;
	}


	/**
     * 处理响应块
     */
    private void processChunk(String chunk, SseEmitter emitter) throws IOException {
        if (chunk == null) {
            return;
        }
        StringBuilder buffer = bufferThreadLocal.get();
        buffer.append(chunk);
        while (true) {
            int lineEnd = buffer.indexOf("\n");
            if (lineEnd == -1) {
                break;
            }
            String line = buffer.substring(0, lineEnd).trim();
            buffer.delete(0, lineEnd + 1);
            if (line.isEmpty()) {
                continue;
            }
            if (line.startsWith("data: ")) {
                String data = line.substring(6).trim();
                if ("[DONE]".equals(data)) {
                    emitter.send(SseEmitter.event().name("done").data("[DONE]"));
                    return;
                }
                try {
                    JSONObject jsonData = parseObject(data);
                    if (jsonData.containsKey("choices")) {
                        String content = extractContent(jsonData);
                        if (content != null && !content.isEmpty()) {
                            contentBufferThreadLocal.get().append(content);
                            java.util.Map<String, Object> payload = new java.util.HashMap<>();
                            payload.put("content", content);
                            payload.put("reasoning_content", "");
                            emitter.send(SseEmitter.event().name("message").data(payload));
                        }
                        // 解析增量图像

                        Object choicesObj = jsonData.get("choices");
                        if (choicesObj instanceof JSONArray arr && !arr.isEmpty()) {
                            Object first = arr.getFirst();
                            if (first instanceof JSONObject choice) {
                                JSONObject delta = choice.getJSONObject("delta");
                                if (delta != null && delta.containsKey("images")) {
                                    JSONArray images = delta.getJSONArray("images");
                                    if (images != null && !images.isEmpty()) {
                                        java.util.List<java.util.Map<String, Object>> out = new java.util.ArrayList<>();
                                        for (int i = 0; i < images.size(); i++) {
                                            JSONObject imgObj = images.getJSONObject(i);
                                            if (imgObj == null) continue;
                                            String type = imgObj.getString("type");
                                           JSONObject imageUrl = imgObj.getJSONObject("image_url");
                                            if (imageUrl != null && imageUrl.containsKey("url")) {
                                                // 如果是 base64 或 data URI，尝试上传到 OSS，成功则替换为可访问 URL
                                                String finalUrl = imageUrl.getString("url");
                                                try {
                                                    if (finalUrl != null && !finalUrl.isBlank() && (finalUrl.startsWith("data:") || !(finalUrl.startsWith("http://") || finalUrl.startsWith("https://")))) {
                                                        finalUrl = uploadUtil.uploadBase64Image(finalUrl, FilePathEnum.TEMP.getDec());
                                                    }
                                                } catch (Exception ex) {
                                                    log.warn("上传 base64 图片到 OSS 失败，将使用文本占位符 [image]", ex);
                                                }
                                                boolean isHttp = finalUrl != null && (finalUrl.startsWith("http://") || finalUrl.startsWith("https://"));
                                                if (isHttp) {
                                                    java.util.Map<String, Object> one = new java.util.HashMap<>();
                                                    one.put("type", type != null ? type : "image_url");
                                                    java.util.Map<String, Object> u = new java.util.HashMap<>();
                                                    u.put("url", finalUrl);
                                                    one.put("image_url", u);
                                                    out.add(one);
                                                } else {
                                                    // 非 http 链接（仍为 base64 或非法），仅记录占位符计数用于数据库入库，不影响 SSE
                                                    Integer c = imagePlaceholderCountThreadLocal.get();
                                                    imagePlaceholderCountThreadLocal.set(c == null ? 1 : c + 1);
                                                }
                                            }
                                        }
                                        if (!out.isEmpty()) {
                                            imagesBufferThreadLocal.get().addAll(out);
                                            emitter.send(SseEmitter.event().name("images").data(out));
                                        }
                                    }
                                }
                                // 解析增量引用（citations）
                                java.util.List<java.util.Map<String, Object>> newCitations = extractCitations(jsonData);
                                if (!newCitations.isEmpty()) {
                                    citationsBufferThreadLocal.get().addAll(newCitations);
                                    emitter.send(SseEmitter.event().name("citations").data(newCitations));
                                }
                            }

                            // 仅在启用时推送 reasoning 增量
                            Boolean reasoningEnabled = Optional.ofNullable(openRouterConfig.getReasoning()).map(OpenRouterConfig.Reasoning::getEnabled).orElse(false);
                            if (reasoningEnabled) {
                                String reasoning = extractReasoning(jsonData);
                                if (reasoning != null && !reasoning.isEmpty()) {
                                    reasoningBufferThreadLocal.get().append(reasoning);
                                    java.util.Map<String, Object> reasoningPayload = new java.util.HashMap<>();
                                    reasoningPayload.put("content", "");
                                    reasoningPayload.put("reasoning_content", reasoning);
                                    emitter.send(SseEmitter.event().name("reasoning").data(reasoningPayload));
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    log.warn("解析响应数据失败: {}", data, e);
                }
            }
        }
    }
    
    /**
     * 从 JSON 响应中提取内容
     */
    private String extractContent(JSONObject jsonData) {
        try {
            if (jsonData.containsKey("choices") && !jsonData.getJSONArray("choices").isEmpty()) {
                JSONObject choice = jsonData.getJSONArray("choices").getJSONObject(0);
                if (choice.containsKey("delta")) {
                    JSONObject delta = choice.getJSONObject("delta");
                    if (delta.containsKey("content")) {
                        return delta.getString("content");
                    }
                }
            }
        } catch (Exception e) {
            log.warn("提取内容失败", e);
        }
        return null;
    }

	/**
	 * 从 JSON 响应中提取推理内容
	 */
	private String extractReasoning(JSONObject jsonData) {
		try {
			if (jsonData.containsKey("choices") && !jsonData.getJSONArray("choices").isEmpty()) {
				JSONObject choice = jsonData.getJSONArray("choices").getJSONObject(0);
				if (choice.containsKey("delta")) {
					JSONObject delta = choice.getJSONObject("delta");
					if (delta.containsKey("reasoning")) {
						return delta.getString("reasoning");
					}
				}
			}
		} catch (Exception e) {
			log.warn("提取推理失败", e);
		}
		return null;
	}

    /**
     * 提取增量/完整响应中的 URL 引用注释
     */
    private java.util.List<java.util.Map<String, Object>> extractCitations(JSONObject jsonData) {
        try {
            if (!jsonData.containsKey("choices") || jsonData.getJSONArray("choices").isEmpty()) return java.util.Collections.emptyList();
            JSONObject choice = jsonData.getJSONArray("choices").getJSONObject(0);
            JSONObject delta = choice.getJSONObject("delta");
            java.util.List<java.util.Map<String, Object>> out = new java.util.ArrayList<>();
            if (delta != null) {
                // 1) delta.annotations: [{ url_citation: {...} }]
                if (delta.containsKey("annotations")) {
                    JSONArray ann = delta.getJSONArray("annotations");
                    if (ann != null) {
                        for (int i = 0; i < ann.size(); i++) {
                            JSONObject a = ann.getJSONObject(i);
                            if (a == null) continue;
                            JSONObject uc = a.getJSONObject("url_citation");
                            if (uc != null) {
                                java.util.Map<String, Object> part = new java.util.HashMap<>();
                                part.put("type", "url_citation");
                                part.put("url_citation", new java.util.HashMap<String, Object>() {{
                                    put("url", uc.getString("url"));
                                    put("title", uc.getString("title"));
                                    put("content", uc.getString("content"));
                                    put("start_index", uc.getInteger("start_index"));
                                    put("end_index", uc.getInteger("end_index"));
                                }});
                                out.add(part);
                            }
                        }
                    }
                }
                // 2) delta.url_citation: { ... }
                if (delta.containsKey("url_citation")) {
                    JSONObject uc = delta.getJSONObject("url_citation");
                    if (uc != null) {
                        java.util.Map<String, Object> part = new java.util.HashMap<>();
                        part.put("type", "url_citation");
                        part.put("url_citation", new java.util.HashMap<String, Object>() {{
                            put("url", uc.getString("url"));
                            put("title", uc.getString("title"));
                            put("content", uc.getString("content"));
                            put("start_index", uc.getInteger("start_index"));
                            put("end_index", uc.getInteger("end_index"));
                        }});
                        out.add(part);
                    }
                }
            }
            return out;
        } catch (Exception e) {
            log.warn("提取引用失败", e);
            return java.util.Collections.emptyList();
        }
    }

    /**
     * 估算一条消息的 token 数（粗略）：
     * - 文本以 ~4 字符/token 的经验估算
     * - 图片按固定预算（如每张 ~1500 token）
     */
    private int estimateMessageTokens(Map<String, Object> message) {
        Object contentObj = message.get("content");
        int tokens = 0;
        int charsPerToken = Optional.ofNullable(openRouterConfig.getTruncation().getTextCharsPerToken()).orElse(4);
        int imageEstimate = Optional.ofNullable(openRouterConfig.getTruncation().getImageTokenEstimate()).orElse(1500);
        int fileEstimate = Optional.ofNullable(openRouterConfig.getTruncation().getFileTokenEstimate()).orElse(4000);
        int audioEstimate = Optional.ofNullable(openRouterConfig.getTruncation().getAudioTokenEstimate()).orElse(6000);
        if (contentObj instanceof List<?> parts) {
            for (Object p : parts) {
                if (!(p instanceof Map<?, ?> part)) continue;
                Object type = part.get("type");
                if ("text".equals(type)) {
                    Object text = part.get("text");
                    if (text != null) {
                        String s = String.valueOf(text);
                        tokens += Math.max(1, s.length() / Math.max(1, charsPerToken));
                    }
                } else if ("image_url".equals(type)) {
                    tokens += imageEstimate;
                } else if ("file".equals(type)) {
                    tokens += fileEstimate;
                } else if ("input_audio".equals(type)) {
                    tokens += audioEstimate;
                }
            }
        } else if (contentObj instanceof String s) {
            tokens += Math.max(1, s.length() / Math.max(1, charsPerToken));
        }
        // role 开销 + 结构开销
        return tokens + 10;
    }

    /**
     * 在不改变消息语义结构的前提下对文本片段做比例缩短，用于在截断前尽量保留更多条数。
     * 策略：
     * 1) 估算总 token，若未超预算直接返回
     * 2) 仅对 type=text 的片段做缩短，图片/文件/音频不变
     * 3) 采用全局比例 factor = budget / totalTokens，按比例截取文本（保留前后各一段，避免只留开头）
     */
    private List<Map<String, Object>> compressMessagesToBudget(List<Map<String, Object>> messages, int budget) {
        if (messages == null || messages.isEmpty()) return messages;
        int total = 0;
        for (Map<String, Object> m : messages) {
            total += estimateMessageTokens(m);
        }
        if (total <= budget) return messages;

        double factor = Math.max(0.1, Math.min(1.0, (double) budget / (double) total));
        int charsPerToken = Optional.ofNullable(openRouterConfig.getTruncation().getTextCharsPerToken()).orElse(4);

        List<Map<String, Object>> out = new ArrayList<>(messages.size());
        for (Map<String, Object> m : messages) {
            Object contentObj = m.get("content");
            if (!(contentObj instanceof List<?> parts)) {
                out.add(m);
                continue;
            }
            List<Object> newParts = new ArrayList<>();
            for (Object p : parts) {
                if (!(p instanceof Map<?, ?>)) { newParts.add(p); continue; }
                @SuppressWarnings("unchecked") Map<String, Object> part = (Map<String, Object>) p;
                Object type = part.get("type");
                if (!"text".equals(type)) {
                    newParts.add(part);
                    continue;
                }
                Object textObj = part.get("text");
                if (textObj == null) { newParts.add(part); continue; }
                String s = String.valueOf(textObj);
                if (s.isEmpty()) { newParts.add(part); continue; }

                int origTokens = Math.max(1, s.length() / Math.max(1, charsPerToken));
                int targetTokens = Math.max(1, (int) Math.floor(origTokens * factor));
                int targetChars = Math.max(1, targetTokens * Math.max(1, charsPerToken));
                if (targetChars >= s.length()) {
                    newParts.add(part);
                    continue;
                }
                // 头尾保留：各保留 40%，中间用省略号，避免语义丢失太多
                int head = Math.max(1, (int) Math.floor(targetChars * 0.6));
                int tail = Math.max(0, targetChars - head);
                StringBuilder sb = new StringBuilder();
                sb.append(s, 0, Math.min(head, s.length()));
                sb.append("\n...\n");
                if (tail > 0 && tail < s.length()) {
                    sb.append(s, Math.max(0, s.length() - tail), s.length());
                }
                Map<String, Object> newPart = new java.util.HashMap<>(part);
                newPart.put("text", sb.toString());
                newParts.add(newPart);
            }
            Map<String, Object> newMsg = new java.util.HashMap<>(m);
            newMsg.put("content", newParts);
            out.add(newMsg);
        }
        return out;
    }

    /**
     * 将 messages 基于 budget 进行从后往前保留（优先保留最近），并尽量保留系统消息：
     */
    private List<Map<String, Object>> truncateMessagesToBudget(List<Map<String, Object>> messages, int budget) {
        if (messages == null || messages.isEmpty()) return messages;
        // 首先分离系统消息
        List<Map<String, Object>> systemMessages = new ArrayList<>();
        List<Map<String, Object>> others = new ArrayList<>();
        for (Map<String, Object> m : messages) {
            Object role = m.get("role");
            if ("system".equals(role)) systemMessages.add(m);
            else others.add(m);
        }
        // 先尽量保留系统消息
        List<Map<String, Object>> kept = new ArrayList<>();
        int used = 0;
        for (Map<String, Object> m : systemMessages) {
            int t = estimateMessageTokens(m);
            if (used + t > budget) break;
            kept.add(m);
            used += t;
        }
        // 再从最近开始保留其他消息（倒序选取，但最终按时间正序返回）
        Deque<Map<String, Object>> deque = new ArrayDeque<>();
        ListIterator<Map<String, Object>> it = others.listIterator(others.size());
        while (it.hasPrevious()) {
            Map<String, Object> m = it.previous();
            int t = estimateMessageTokens(m);
            if (used + t > budget) break;
            deque.addFirst(m);
            used += t;
        }
        // 将系统消息（保持原顺序）放在最前，再拼接按时间正序的用户/助手消息
        List<Map<String, Object>> result = new ArrayList<>(kept);
        result.addAll(deque);
        return result;
    }



    /**
     * 检查消息列表中是否包含图片生成请求
     * 仿照 containsFileContent() 的实现模式
     */
    private boolean containsImageGenerationRequest(List<Map<String, Object>> messages) {
        for (Map<String, Object> message : messages) {
            Object content = message.get("content");
            if (content instanceof List<?> parts) {
                for (Object part : parts) {
                    if (part instanceof Map<?, ?> partMap) {
                        Object type = partMap.get("type");
                        if ("generate_images_request".equals(type)) {
                            Object enabled = partMap.get("enabled");
                            return Boolean.TRUE.equals(enabled);
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 检查 messages 列表中是否包含 file 类型的内容。
     */
    private boolean containsFileContent(List<Map<String, Object>> messages) {
        for (Map<String, Object> message : messages) {
            Object content = message.get("content");
            if (content instanceof List<?> parts) {
                for (Object part : parts) {
                    if (part instanceof Map<?, ?> partMap) {
                        Object type = partMap.get("type");
                        if ("file".equals(type)) {
                            return true;
                        }
                    }
                }
            } else if (content instanceof String s) {
                if (s.contains("file_data")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检查是否包含联网搜索请求
     */
    private boolean containsWebSearchRequest(List<Map<String, Object>> messages) {
        for (Map<String, Object> message : messages) {
            Object content = message.get("content");
            if (content instanceof List<?> parts) {
                for (Object part : parts) {
                    if (part instanceof Map<?, ?> partMap) {
                        Object type = partMap.get("type");
                        if ("web_search_request".equals(type)) {
                            Object enabled = partMap.get("enabled");
                            if (Boolean.TRUE.equals(enabled)) return true;
                        }
                    }
                }
            }
        }
        return false;
    }

	private Boolean parseBooleanQuery(String value) {
		if (value == null) return null;
		String v = value.trim().toLowerCase();
		if ("true".equals(v) || "1".equals(v)) return true;
		if ("false".equals(v) || "0".equals(v)) return false;
		return null;
	}

	private Integer parseIntegerQuery(String value) {
		try {
			if (value == null || value.trim().isEmpty()) return null;
			return Integer.parseInt(value.trim());
		} catch (Exception e) {
			return null;
		}
	}

	private Double parseDoubleQuery(String value) {
		try {
			if (value == null || value.trim().isEmpty()) return null;
			return Double.parseDouble(value.trim());
		} catch (Exception e) {
			return null;
		}
	}

    @Override
    public void deleteSession(String sessionId, boolean force) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new LlmException("会话ID不能为空");
        }
        String lockKey = "chat:lock:" + sessionId;
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            // 尝试快速获取锁，避免与流式处理冲突
            locked = lock.tryLock(0, 30, TimeUnit.SECONDS);
            if (!locked && !force) {
                throw new LlmException("会话正在处理中，请稍后再试");
            }
            // 执行删除Redis中的会话数据
            String sessionKey = "chat:" + sessionId;
            redisUtils.delKey(sessionKey);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LlmException("删除会话被中断");
        } finally {
            if (locked) {
                try { lock.unlock(); } catch (Exception ignored) {}
            }
        }
    }

    @Override
    public List<Map<String, Object>> getChatMessages(String sessionId, int limit) {
        String sessionKey = "chat:" + sessionId;
        // 从Redis List中获取最近的消息（倒序获取，然后反转为正序）
        List<Object> messageStrings = redisUtils.listGet(sessionKey, 0, Math.max(0, limit - 1));
        
        List<Map<String, Object>> messages = new ArrayList<>();
        if (messageStrings != null && !messageStrings.isEmpty()) {
            // Redis List是最新消息在前，需要反转为时间正序
            for (int i = messageStrings.size() - 1; i >= 0; i--) {
                String messageStr = (String) messageStrings.get(i);
                JSONObject messageObj = JSON.parseObject(messageStr);
                
                Map<String, Object> message = new HashMap<>();
                message.put("role", messageObj.getString("role"));
                message.put("content", messageObj.get("content"));
                messages.add(message);
            }
        }
        return messages;
    }

    @Override
    public void addAssistantMessage(String sessionId, String contentJson) {
        String sessionKey = "chat:" + sessionId;
        
        Map<String, Object> assistantMessage = new HashMap<>();
        assistantMessage.put("role", "assistant");
        assistantMessage.put("content", JSON.parseArray(contentJson));
        assistantMessage.put("timestamp", System.currentTimeMillis());
        
        String messageJson = JSON.toJSONString(assistantMessage);
        
        // 添加到Redis List头部
        redisUtils.listPush(sessionKey, messageJson);
        
        // 更新过期时间（从配置读取）
        redisUtils.expire(sessionKey, openRouterConfig.getChat().getSessionTtlSeconds());
    }

    @Override
    public boolean needsReply(String sessionId) {
        String sessionKey = "chat:" + sessionId;
        // 检查最新一条消息（List第一个元素）
        List<Object> lastMessages = redisUtils.listGet(sessionKey, 0, 0);
        
        if (lastMessages.isEmpty()) {
            return false; // 没有消息，不需要回复
        }
        
        String lastMessageStr = (String) lastMessages.getFirst();
        JSONObject lastMessageObj = JSON.parseObject(lastMessageStr);
        
        // 如果最后一条是用户消息，则需要回复
        return "user".equals(lastMessageObj.getString("role"));
    }

    /**
     * 记录 API 调用统计
     * 
     * @param modelSlug 模型标识（slug）
     */
    private void recordApiCall(String modelSlug) {
        try {
            String today = java.time.LocalDate.now().toString();
            
            // 1. 今日 API 调用次数 +1
            String apiCallKey = com.cn.common.constant.SystemStatsConstant.AI_API_CALLS_PREFIX + today;
            redisUtils.increment(apiCallKey, 1L);
            redisUtils.expire(apiCallKey, com.cn.common.constant.CacheExpireConstant.EXPIRE_48_HOURS);
            
            // 2. 模型调用统计 +1
            if (modelSlug != null && !modelSlug.isBlank()) {
                String modelKey = com.cn.common.constant.SystemStatsConstant.AI_MODELS_PREFIX + today;
                redisUtils.hashIncrement(modelKey, modelSlug, 1L);
                redisUtils.expire(modelKey, com.cn.common.constant.CacheExpireConstant.EXPIRE_48_HOURS);
            }
            
            log.debug("记录API调用统计: date={}, model={}", today, modelSlug);
            
        } catch (Exception e) {
            log.error("记录API调用统计失败", e);
        }
    }
} 
