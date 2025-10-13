package com.cn.comfyui.controller;

import com.cn.common.msg.Result;
import com.cn.comfyui.dto.WorkflowResultDto;
import com.cn.comfyui.excepitons.ComfyuiException;
import com.cn.comfyui.service.WorkflowResultService;
import com.cn.common.annotations.RateLimit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 作品控制器
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/comfyui/result")
@RequiredArgsConstructor
@Validated
public class WorkflowResultController {

    private final WorkflowResultService workflowResultService;

    /**
     * 获取作品分页列表
     *
     * @param page 页码
     * @return 作品分页列表
     */
    @GetMapping(value = "/get/workflow-result/page", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getWorkflowResult(@RequestParam(defaultValue = "1") @Min(value = 1, message = "页码最小为1") final Long page) {
        return Result.data(workflowResultService.getWorkflowResultPage(page));
    }

    /**
     * 获取作品详情
     *
     * @param workflowResultId 作品ID
     * @return 作品详情
     */
    @GetMapping(value = "/get/workflow-result/detail", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getWorkflowResultDetail(@RequestParam @NotNull(message = "作品ID不能为空") final Long workflowResultId) {
        return Result.data(workflowResultService.getWorkflowResultDetail(workflowResultId));
    }

    /**
     * 删除作品
     *
     * @param dto 删除作品请求
     * @return 操作结果
     */
    @PostMapping(value = "/delete/workflow-result", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimit(permitsPerSecond = 0.5, limitType = RateLimit.LimitType.USER, message = "删除操作过于频繁，请稍后再试")
    public Result deleteWorkflowResult(@RequestBody @Valid final WorkflowResultDto dto) {
        try {
            workflowResultService.deleteWorkflowResult(dto);
            return Result.ok("删除成功");
        } catch (ComfyuiException e) {
            return Result.error(e.getMessage());
        }
    }
}
