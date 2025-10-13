package com.cn.system.service;

import com.cn.common.vo.PageVo;
import com.cn.system.dto.*;
import com.cn.system.vo.ParsingWorkflowVo;
import com.cn.system.vo.SystemWorkflowPageItemVo;
import com.cn.system.vo.SystemWorkflowCategoryVo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 系统工作流管理服务
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
public interface SystemWorkflowService {

    /**
     * 解析工作流 JSON 文件，自动识别节点类型
     *
     * @param file 上传的 JSON 文件
     * @return 解析结果，包含所有节点信息和可识别的输入节点
     */
    ParsingWorkflowVo parsingWorkflowJson(MultipartFile file);

    /**
     * 保存工作流配置
     *
     * @param dto 工作流配置数据
     */
    void saveWorkflowConfig(SaveWorkflowConfigDto dto);

    /**
     * 分页查询工作流
     */
    PageVo<SystemWorkflowPageItemVo> page(Integer page, Integer size, String keyword, Long categoryId);

    /**
     * 更新工作流（仅名称与类别）
     */
    void updateWorkflow(UpdateWorkflowDto dto);

    /**
     * 删除工作流
     */
    void deleteWorkflow(DeleteWorkflowDto dto);

    /**
     * 新建类别
     */
    Long createCategory(CreateWorkflowCategoryDto dto);

    /**
     * 修改类别名称
     */
    void updateCategory(UpdateWorkflowCategoryDto dto);

    /**
     * 删除类别
     */
    void deleteCategory(DeleteWorkflowCategoryDto dto);

    /**
     * 获取所有类别列表（按创建时间倒序）
     */
    java.util.List<SystemWorkflowCategoryVo> getCategoryList();
}

