package com.cn.llm.controller;

import com.cn.common.annotations.RateLimit;
import com.cn.common.msg.Result;
import com.cn.llm.dto.DeleteSessionDto;
import com.cn.llm.dto.GetAvailableModelListDto;
import com.cn.llm.dto.GetAvailableModelPageDto;
import com.cn.llm.dto.SubmitMessageDto;
import com.cn.llm.excepitons.LlmException;
import com.cn.llm.registry.RemoteRegistryStore;
import com.cn.llm.service.LlmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * LLM 控制器
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/llm")
@RequiredArgsConstructor
public class LlmController {

    private final LlmService llmService;

    private final RemoteRegistryStore remoteRegistryStore;

    /**
     * 提交用户消息（文本、图片URL、PDF、音频）
     */
    @PostMapping(value = "/chat/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimit(permitsPerSecond = 1.0, limitType = RateLimit.LimitType.USER, message = "消息发送过于频繁，请稍后再试")
    public Result submit(@RequestBody @Validated SubmitMessageDto dto) {
        try {
            llmService.submitMessage(dto);
            return Result.ok();
        }catch (LlmException e){
            return Result.error(e.getMessage());
        }
    }

    /**
     * 按会话建立SSE连接并触发一次回复（若有待处理消息）
     */
    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @RateLimit(permitsPerSecond = 2.0, limitType = RateLimit.LimitType.USER, message = "连接请求过于频繁，请稍后再试")
	public SseEmitter chatStream(@RequestParam("sessionId") String sessionId,
							 @RequestParam(value = "modelId", required = false) String modelId,
							 @RequestParam(value = "enableWebSearch", required = false) String enableWebSearch,
							 @RequestParam(value = "generateImages", required = false) String generateImages,
							 @RequestParam("token") String token) {
        // SSE 无法通过请求头传递 token，需要通过 URL 参数传递到 Service 层进行校验
        return llmService.chatStream(sessionId, modelId, enableWebSearch, generateImages, token);
	}

    /**
     * 获取可用模型列表（分页）
     * 支持筛选：inputType、outputType、name、supportReasoning、paymentMode、page
     * 返回格式：{ total: 总数, items: 模型列表 }，每页20条
     */
    @PostMapping(value = "/get/available-model/page", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getAvailableModelPage(@RequestBody(required = false) GetAvailableModelPageDto query) {
        // remoteRegistryStore.get 方法已经完美处理 null 参数，并支持分页
        return Result.data(remoteRegistryStore.get(query));
    }

    /**
     * 获取可用模型完整列表（不分页），支持同样的筛选条件
     */
    @PostMapping(value = "/get/available-model/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getAvailableModelListList(@RequestBody(required = false) GetAvailableModelListDto filter) {
        return Result.data(remoteRegistryStore.list(filter));
    }

    /**
     * 删除会话（删除该会话下的所有消息）
     */
    @PostMapping(value = "/chat/session/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimit(permitsPerSecond = 0.5, limitType = RateLimit.LimitType.USER, message = "删除操作过于频繁，请稍后再试")
    public Result deleteSession(@RequestBody @Validated DeleteSessionDto body) {
        try {
            llmService.deleteSession(body.getSessionId(), false);
            return Result.ok();
        } catch (LlmException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取默认模型（完整对象）
     */
    @GetMapping(value = "/get/default-model", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getDefaultModel() {
        try {
            return Result.data(llmService.getDefaultModelObject());
        } catch (LlmException e) {
            return Result.error(e.getMessage());
        }
    }

} 