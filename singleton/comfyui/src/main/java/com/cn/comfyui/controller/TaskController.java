package com.cn.comfyui.controller;

import com.cn.comfyui.dto.CancelTaskDto;
import com.cn.comfyui.dto.RemakeTaskDto;
import com.cn.comfyui.dto.SubmitTaskDto;
import com.cn.comfyui.excepitons.ComfyuiException;
import com.cn.comfyui.service.WorkflowService;
import com.cn.common.msg.Result;
import com.cn.common.annotations.RateLimit;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 任务控制器
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/comfyui/task")
@RequiredArgsConstructor
public class TaskController {

    private final WorkflowService workflowService;


    /**
     * 提交任务
     *
     * @param dto the dto
     * @return the result
     */
    @PostMapping(value = "/submit/task", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimit(permitsPerSecond = 0.5, limitType = RateLimit.LimitType.USER, message = "任务提交过于频繁，请稍后再试")
    public Result submitTask(@RequestBody @Validated final SubmitTaskDto dto) {
        try {
            return Result.data(workflowService.submitTask(dto));
        } catch (ComfyuiException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
     * 取消等待中的任务
     * 
     * @return 操作结果
     */
    @PostMapping(value = "/cancel/task", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimit(permitsPerSecond = 1.0, limitType = RateLimit.LimitType.USER, message = "操作过于频繁，请稍后再试")
    public Result cancelTask(@RequestBody @Validated final CancelTaskDto dto) {
        try {
            workflowService.cancelTask(dto);
            return Result.ok();
        } catch (ComfyuiException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
     * 重新制作任务
     * 仅支持已取消、已成功或已失败的任务重新制作
     * 将保留原任务ID，但会删除原任务和相关作品记录，重新创建任务并加入队列
     *
     * @param dto 包含任务ID的DTO
     * @return 返回任务ID
     */
    @PostMapping(value = "/remake/task", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimit(permitsPerSecond = 0.333, limitType = RateLimit.LimitType.USER, message = "重制任务过于频繁，请稍后再试")
    public Result remakeTask(@RequestBody @Validated final RemakeTaskDto dto) {
        try {
            return Result.data(workflowService.remakeTask(dto));
        } catch (ComfyuiException ex) {
            return Result.error(ex.getMessage());
        }
    }


    /**
     * 分页获取当前用户任务进度
     *
     * @param page 页码，从1开始
     * @param status 任务状态筛选，可选值：WAIT、BUILD、SUCCEED、CANCELED、FAILED，为空则不筛选
     * @return 分页任务进度列表
     */
    @GetMapping(value = "/get/task/progress-page", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getTaskProgressPage(@RequestParam(defaultValue = "1") final Long page,
                                    @RequestParam(required = false) final String status) {
        try {
            return Result.data(workflowService.getTaskProgressPage(page, status));
        } catch (ComfyuiException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
     * 获取工作流接口定义
     *
     * @param workflowId 工作流ID
     * @return 工作流接口定义
     */
    @GetMapping(value = "/get/workflow/interface", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getWorkflowInterface(@RequestParam @NotNull(message = "工作流ID不能为空") final Long workflowId) {
        return Result.data(workflowService.getWorkflowInterface(workflowId));
    }

    /**
     * 获取工作流列表
     *
     * @return 工作流列表
     */
    @GetMapping(value = "/get/workflow/page", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getWorkflowPage(final String prompt, @RequestParam(defaultValue = "1") final Long page,final Long categoryId) {
        return Result.data(workflowService.getWorkflowsPage(prompt,categoryId, page));
    }


    /**
     * 获取工作流筛选列表
     *
     * @return 工作流列表
     */
    @GetMapping(value = "/get/workflow-category/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getWorkflowCategoryList() {
        return Result.data(workflowService.getWorkflowCategoryList());
    }

}
