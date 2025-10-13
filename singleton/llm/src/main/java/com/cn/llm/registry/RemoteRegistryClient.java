package com.cn.llm.registry;

import com.cn.llm.config.OpenRouterConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 远程注册客户端
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RemoteRegistryClient {

    private final WebClient webClient;
    private final OpenRouterConfig openRouterConfig;
    private final ObjectMapper objectMapper;

    public String fetch() {
        final OpenRouterConfig.RemoteRegistry cfg = openRouterConfig.getRemoteRegistry();
        final int timeoutSec = cfg.getReadTimeoutSeconds() == null ? 10 : cfg.getReadTimeoutSeconds();
        try {
            log.info("请求远程注册数据，url={}, timeout={}s", cfg.getUrl(), timeoutSec);
            String raw = webClient
                    .get()
                    .uri(cfg.getUrl())
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(timeoutSec))
                    .onErrorResume(e -> {
                        log.error("拉取远程注册数据失败: {}", e.getMessage(), e);
                        return Mono.empty();
                    })
                    .block();

            if (raw == null || raw.isBlank()) {
                return null;
            }

            try {
                JsonNode root = objectMapper.readTree(raw);
                JsonNode dataNode = root.path("data");

                ArrayNode outData = objectMapper.createArrayNode();

                if (dataNode.isArray()) {
                    for (JsonNode item : dataNode) {
                        JsonNode endpoint = item.get("endpoint");
                        if (endpoint == null || endpoint.isNull()) {
                            continue;
                        }

                        // 按配置过滤 FREE/PAID/ALL
                        if (!acceptByFilter(endpoint)) {
                            continue;
                        }

                        // 必要字段映射
                        JsonNode idNode = endpoint.get("id");
                        JsonNode modelNode = endpoint.get("model");
                        if (idNode == null || idNode.isNull() || modelNode == null || modelNode.isNull()) {
                            continue;
                        }

                        ObjectNode outItem = objectMapper.createObjectNode();
                        outItem.put("id", idNode.asText());
                        outItem.put("name", modelNode.path("name").asText(""));
                        outItem.put("model", modelNode.path("slug").asText(""));
                        // 新增：icon 来源 endpoint.provider_info.icon.url
                        try {
                            String iconUrl = endpoint.path("provider_info").path("icon").path("url").asText("");
                            if (iconUrl != null && !iconUrl.isBlank()) {
                                outItem.put("icon", iconUrl);
                            }
                        } catch (Exception ignore) {}

                        JsonNode ctxLen = modelNode.get("context_length");
                        int maxTokens = 0;
                        if (ctxLen != null && !ctxLen.isNull()) {
                            try {
                                maxTokens = ctxLen.isNumber() ? ctxLen.asInt() : Integer.parseInt(ctxLen.asText("0"));
                            } catch (Exception ignored) {
                            }
                        }
                        // 过滤 context_length 为 0 的模型
                        if (maxTokens <= 0) {
                            continue;
                        }
                        outItem.put("maxTokens", maxTokens);

                        // outputType 来源：output_modalities 数组
                        ArrayNode outputTypeArr = objectMapper.createArrayNode();
                        JsonNode outMods = modelNode.get("output_modalities");
                        if (outMods != null && outMods.isArray()) {
                            for (JsonNode mod : outMods) {
                                outputTypeArr.add(mod.asText());
                            }
                        }
                        outItem.set("outputType", outputTypeArr);

                        // inputType 来源：input_modalities 数组
                        ArrayNode inputTypeArr = objectMapper.createArrayNode();
                        JsonNode inMods = modelNode.get("input_modalities");
                        if (inMods != null && inMods.isArray()) {
                            for (JsonNode mod : inMods) {
                                inputTypeArr.add(mod.asText());
                            }
                        }
                        outItem.set("inputType", inputTypeArr);

   
                        // supportReasoning 来源：endpoint 的 supports_reasoning 字段
                        boolean supportReasoning = endpoint.path("supports_reasoning").asBoolean(false);
                        outItem.put("supportReasoning", supportReasoning);

                        // 添加付费模式字段
                        JsonNode pricing = endpoint.get("pricing");
                        String paymentMode = "FREE";
                        if (pricing != null && pricing.isObject()) {
                            double prompt = parsePrice(pricing.get("prompt"));
                            double completion = parsePrice(pricing.get("completion"));
                            if (prompt > 0.0 || completion > 0.0) {
                                paymentMode = "PAID";
                            }
                        }
                        outItem.put("paymentMode", paymentMode);

                        outData.add(outItem);
                    }
                } else {
                    log.warn("远程注册返回结构无 data 数组，使用空数组");
                    return "[]";
                }

                return objectMapper.writeValueAsString(outData);
            } catch (Exception parseOrFilterEx) {
                log.error("远程注册数据解析/过滤失败，使用空数组: {}", parseOrFilterEx.getMessage(), parseOrFilterEx);
                return "[]";
            }
        } catch (Exception e) {
            log.error("请求远程注册数据异常: {}", e.getMessage(), e);
            return null;
        }
    }

    private boolean acceptByFilter(JsonNode endpoint) {
        OpenRouterConfig.FilterMode mode = openRouterConfig.getRemoteRegistry().getFilter();
        if (mode == null || mode == OpenRouterConfig.FilterMode.ALL) {
            return true;
        }
        JsonNode pricing = endpoint.get("pricing");
        if (pricing == null || !pricing.isObject()) {
            return mode == OpenRouterConfig.FilterMode.FREE;
        }
        double prompt = parsePrice(pricing.get("prompt"));
        double completion = parsePrice(pricing.get("completion"));
        boolean isFree = prompt == 0.0 && completion == 0.0;
        if (mode == OpenRouterConfig.FilterMode.FREE) return isFree;
        if (mode == OpenRouterConfig.FilterMode.PAID) return !isFree;
        return true;
    }

    private double parsePrice(JsonNode node) {
        if (node == null || node.isNull()) return 0.0;
        try {
            if (node.isNumber()) return node.asDouble();
            String s = node.asText();
            if (s == null || s.isBlank()) return 0.0;
            return Double.parseDouble(s);
        } catch (Exception ignore) {
            return 0.0;
        }
    }
} 