package com.cn.system.controller;

import com.cn.common.msg.Result;
import com.cn.common.vo.PageVo;
import com.cn.system.dto.*;
import com.cn.system.service.SystemWorkflowService;
import com.cn.system.vo.SystemWorkflowPageItemVo;
import com.cn.system.vo.SystemWorkflowCategoryVo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * 系统工作流管理控制器
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@RestController
@RequestMapping("/system/workflow")
@Validated
@RequiredArgsConstructor
public class SystemWorkflowController {

    private final SystemWorkflowService systemWorkflowService;

    /**
     * 解析工作流 JSON 文件
     */
    @PostMapping(value = "/parsing", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result parsingWorkflowJson(@RequestParam("file") MultipartFile file) {
        return Result.data(systemWorkflowService.parsingWorkflowJson(file));
    }

    /**
     * 保存工作流配置
     */
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result saveWorkflowConfig(@Valid @RequestBody SaveWorkflowConfigDto dto) {
        systemWorkflowService.saveWorkflowConfig(dto);
        return Result.ok("工作流配置保存成功");
    }

    /**
     * 获取工作流分页列表（包含类别信息）
     */
    @GetMapping(value = "/page", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getWorkflowPage(@RequestParam(defaultValue = "1") @Min(1) Integer page,
                                  @RequestParam(defaultValue = "10") @Min(1) Integer size,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) Long categoryId) {
        PageVo<SystemWorkflowPageItemVo> data = systemWorkflowService.page(page, size, keyword, categoryId);
        return Result.data(data);
    }

    /**
     * 修改工作流（仅允许修改类别与名称）
     */
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result updateWorkflow(@RequestBody @Valid UpdateWorkflowDto dto) {
        systemWorkflowService.updateWorkflow(dto);
        return Result.ok();
    }

    /**
     * 删除工作流
     */
    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result deleteWorkflow(@RequestBody @Valid DeleteWorkflowDto dto) {
        systemWorkflowService.deleteWorkflow(dto);
        return Result.ok();
    }

    /**
     * 新建类别
     */
    @PostMapping(value = "/category/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result createCategory(@RequestBody @Valid CreateWorkflowCategoryDto dto) {
        Long id = systemWorkflowService.createCategory(dto);
        return Result.data(id);
    }

    /**
     * 修改类别名称
     */
    @PostMapping(value = "/category/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result updateCategory(@RequestBody @Valid UpdateWorkflowCategoryDto dto) {
        systemWorkflowService.updateCategory(dto);
        return Result.ok();
    }

    /**
     * 删除类别
     */
    @PostMapping(value = "/category/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result deleteCategory(@RequestBody @Valid DeleteWorkflowCategoryDto dto) {
        systemWorkflowService.deleteCategory(dto);
        return Result.ok();
    }

    /**
     * 获取所有类别列表
     */
    @GetMapping(value = "/category/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getCategoryList() {
        java.util.List<SystemWorkflowCategoryVo> data = systemWorkflowService.getCategoryList();
        return Result.data(data);
    }
}

