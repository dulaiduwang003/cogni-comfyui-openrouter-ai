package com.cn.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cn.common.entity.Workflow;
import com.cn.common.entity.WorkflowCategory;
import com.cn.common.entity.WorkflowForm;
import com.cn.common.entity.WorkflowOutput;
import com.cn.common.enums.ComfyuiFormTypeEnum;
import com.cn.common.enums.ComfyuiInputFieldEnum;
import com.cn.common.exceptions.UniversalException;
import com.cn.common.enums.RequiredEnum;
import com.cn.common.mapper.WorkflowFormMapper;
import com.cn.common.mapper.WorkflowMapper;
import com.cn.common.mapper.WorkflowOutputMapper;
import com.cn.common.mapper.WorkflowCategoryMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cn.common.vo.PageVo;
import com.cn.system.dto.*;
import com.cn.system.service.SystemWorkflowService;
import com.cn.system.vo.ParsingWorkflowVo;
import com.cn.system.vo.SystemWorkflowPageItemVo;
import com.cn.system.vo.SystemWorkflowCategoryVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统工作流管理服务实现
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemWorkflowServiceImpl implements SystemWorkflowService {

    private final WorkflowMapper workflowMapper;
    private final WorkflowFormMapper workflowFormMapper;
    private final WorkflowOutputMapper workflowOutputMapper;
    private final WorkflowCategoryMapper workflowCategoryMapper;

    /**
     * 解析工作流 JSON 文件，自动识别可用作输入的节点
     */
    @Override
    public ParsingWorkflowVo parsingWorkflowJson(final MultipartFile file) {
        // 1. 验证文件扩展名
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".json")) {
            throw new UniversalException("请上传 .json 文件");
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            
            // 2. 读取文件内容
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String jsonString = stringBuilder.toString();

            // 3. 解析 JSON
            JSONObject jsonObject = JSON.parseObject(jsonString);
            if (jsonObject == null || jsonObject.isEmpty()) {
                throw new UniversalException("JSON 文件内容为空或格式不正确");
            }

            // 4. 提取所有节点的基础信息
            List<ParsingWorkflowVo.AllNode> allNodeList = extractAllNodes(jsonObject);

            // 5. 识别可用于表单输入的节点
            List<ParsingWorkflowVo.FormNode> formNodeList = extractFormNodes(jsonObject);

            // 6. 构建返回结果
            return new ParsingWorkflowVo()
                    .setJson(jsonObject.toJSONString())
                    .setAllNodeList(allNodeList)
                    .setFormNodeList(formNodeList);

        } catch (UniversalException e) {
            throw e;
        } catch (Exception e) {
            log.error("解析工作流 JSON 文件失败: {}", fileName, e);
            throw new UniversalException("无法正确解析该 JSON 文件内容：" + e.getMessage());
        }
    }

    /**
     * 提取所有节点的基础信息
     */
    private List<ParsingWorkflowVo.AllNode> extractAllNodes(JSONObject jsonObject) {
        List<ParsingWorkflowVo.AllNode> allNodeList = new ArrayList<>();

        for (String nodeKey : jsonObject.keySet()) {
            JSONObject node = jsonObject.getJSONObject(nodeKey);
            if (node == null) {
                continue;
            }

            // 提取节点标题
            String title = extractNodeTitle(node);

            // 构建基础节点信息
            ParsingWorkflowVo.AllNode allNode = new ParsingWorkflowVo.AllNode()
                    .setNodeKey(nodeKey)
                    .setTips(title);

            allNodeList.add(allNode);
        }

        return allNodeList;
    }

    /**
     * 识别并提取可用于表单输入的节点
     */
    private List<ParsingWorkflowVo.FormNode> extractFormNodes(JSONObject jsonObject) {
        List<ParsingWorkflowVo.FormNode> resultList = new ArrayList<>();

        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            String nodeKey = entry.getKey();
            JSONObject nodeObject = (JSONObject) entry.getValue();

            // 获取节点的 inputs 对象
            JSONObject inputs = nodeObject.getJSONObject("inputs");
            if (inputs == null || inputs.isEmpty()) {
                continue;
            }

            // 提取节点标题
            String title = extractNodeTitle(nodeObject);

            // 识别节点类型并添加到结果列表
            identifyAndAddNode(nodeKey, inputs, title, resultList);
        }

        return resultList;
    }

    /**
     * 从节点中提取标题
     * 
     * @param node 节点对象
     * @return 节点标题，如果不存在则返回 null
     */
    private String extractNodeTitle(JSONObject node) {
        JSONObject meta = node.getJSONObject("_meta");
        if (meta != null && meta.containsKey("title")) {
            return meta.getString("title");
        }
        return null;
    }

    /**
     * 识别节点类型并添加到结果列表
     * 
     * @param nodeKey 节点 Key
     * @param inputs 节点的 inputs 对象
     * @param title 节点标题
     * @param resultList 结果列表
     */
    private void identifyAndAddNode(String nodeKey, JSONObject inputs, String title,
                                     List<ParsingWorkflowVo.FormNode> resultList) {
        
        // 按优先级检查不同类型的输入字段
        // 注意：需要排除数组类型的字段，因为数组通常是节点连接引用
        
        // 1. 检查文本输入（需管理员配置具体类型）
        if ((inputs.containsKey(ComfyuiInputFieldEnum.TEXT.getFieldName()) && !isArrayValue(inputs.get(ComfyuiInputFieldEnum.TEXT.getFieldName())))
                ||(inputs.containsKey(ComfyuiInputFieldEnum.MULTI_LINE_PROMPT.getFieldName()) && !isArrayValue(inputs.get(ComfyuiInputFieldEnum.MULTI_LINE_PROMPT.getFieldName())))
                ||(inputs.containsKey(ComfyuiInputFieldEnum.RESOLUTION.getFieldName()) && !isArrayValue(inputs.get(ComfyuiInputFieldEnum.RESOLUTION.getFieldName())))) {
            resultList.add(createConfigurableTextNode(nodeKey, ComfyuiInputFieldEnum.TEXT.getFieldName(), title));
        }
        
        // 2. 检查图片上传
        if (inputs.containsKey(ComfyuiInputFieldEnum.IMAGE.getFieldName()) && !isArrayValue(inputs.get(ComfyuiInputFieldEnum.IMAGE.getFieldName()))) {
            resultList.add(createNode(nodeKey, ComfyuiFormTypeEnum.IMAGE_UPLOAD.getDec(), ComfyuiInputFieldEnum.IMAGE.getFieldName(), title, null));
        }
        
        // 3. 检查视频上传
        if (inputs.containsKey(ComfyuiInputFieldEnum.VIDEO.getFieldName()) && !isArrayValue(inputs.get(ComfyuiInputFieldEnum.VIDEO.getFieldName()))) {
            resultList.add(createNode(nodeKey, ComfyuiFormTypeEnum.VIDEO_UPLOAD.getDec(), ComfyuiInputFieldEnum.VIDEO.getFieldName(), title, null));
        }
        
        // 4. 检查音频上传
        if (inputs.containsKey(ComfyuiInputFieldEnum.AUDIO.getFieldName()) && !isArrayValue(inputs.get(ComfyuiInputFieldEnum.AUDIO.getFieldName()))) {
            resultList.add(createNode(nodeKey, ComfyuiFormTypeEnum.AUDIO_UPLOAD.getDec(), ComfyuiInputFieldEnum.AUDIO.getFieldName(), title, null));
        }
    }

    /**
     * 创建可配置的文本节点
     * 
     * @param nodeKey 节点 Key
     * @param nodeDigital 节点字段名
     * @param title 节点标题
     * @return 文本节点对象（带可选类型列表）
     */
    private ParsingWorkflowVo.FormNode createConfigurableTextNode(String nodeKey, String nodeDigital, String title) {
        List<String> availableTypes = Arrays.asList(
                ComfyuiFormTypeEnum.TEXT_PROMPT.getDec(),
                ComfyuiFormTypeEnum.RADIO_SELECTOR.getDec(),
                ComfyuiFormTypeEnum.CHECKBOX_SELECTOR.getDec()
        );
        return new ParsingWorkflowVo.FormNode()
                .setNodeKey(nodeKey)
                .setType(ComfyuiFormTypeEnum.TEXT_CONFIGURABLE.getDec())
                .setNodeDigital(nodeDigital)
                .setTips(title)
                .setAvailableTypes(availableTypes);
    }

    /**
     * 创建节点对象
     * 
     * @param nodeKey 节点 Key
     * @param type 节点类型
     * @param nodeDigital 节点字段名
     * @param title 节点标题
     * @param availableTypes 可选类型列表（null 表示不需要配置）
     * @return 节点对象
     */
    private ParsingWorkflowVo.FormNode createNode(String nodeKey, String type, 
                                               String nodeDigital, String title, List<String> availableTypes) {
        return new ParsingWorkflowVo.FormNode()
                .setNodeKey(nodeKey)
                .setType(type)
                .setNodeDigital(nodeDigital)
                .setTips(title)
                .setAvailableTypes(availableTypes);
    }

    /**
     * 判断值是否为数组类型
     * 
     * ComfyUI 中，数组类型的字段通常是节点间的连接引用，格式如：["节点ID", 输出索引]
     * 例如：["4", 0] 表示引用节点 4 的第 0 个输出
     * 
     * @param value 要检查的值
     * @return 如果是数组类型返回 true，否则返回 false
     */
    private boolean isArrayValue(Object value) {
        return value instanceof List;
    }

    /**
     * 保存工作流配置
     * 
     * 功能：
     * 1. 验证输入节点配置（选择器类型必须有 options）
     * 2. 保存 workflow 主表
     * 3. 保存输入节点配置到 workflow_form 表
     * 4. 保存输出节点配置到 workflow_output 表
     *
     * @param dto 工作流配置数据
     * @throws UniversalException 配置验证失败时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWorkflowConfig(SaveWorkflowConfigDto dto) {
        // 1. 验证表单节点配置
        validateFormNodeConfigs(dto.getFormNodeList());

        // 2. 保存工作流主表
        Workflow workflow = new Workflow()
                .setName(dto.getName())
                .setDescription(dto.getDescription())
                .setUrl(dto.getUrl())
                .setJson(dto.getJson())
                .setWorkflowCategoryId(dto.getWorkflowCategoryId())
                .setCreditsDeducted(dto.getCreditsDeducted());
        
        workflowMapper.insert(workflow);
        Long workflowId = workflow.getId();

        // 3. 批量保存表单节点配置
        List<WorkflowForm> formList = dto.getFormNodeList().stream()
                .map(input -> new WorkflowForm()
                        .setWorkflowId(workflowId)
                        .setNodeKey(input.getNodeKey())
                        .setType(input.getType())
                        .setInputs(input.getInputs())
                        .setTips(input.getTips())
                        // MySQL JSON 列不接受空字符串，空则置为 NULL
                        .setOptions(StringUtils.hasText(input.getOptions()) ? input.getOptions() : null)
                        .setTemplate(input.getTemplate())
                        .setRequired(Boolean.TRUE.equals(input.getRequired()) ? RequiredEnum.TRUE.getDec() : RequiredEnum.FALSE.getDec())
                        .setSize(input.getSize()))
                .collect(Collectors.toList());
        
        formList.forEach(workflowFormMapper::insert);

        // 4. 批量保存输出节点配置
        List<WorkflowOutput> outputList = dto.getOutputNodeList().stream()
                .map(output -> new WorkflowOutput()
                        .setWorkflowId(workflowId)
                        .setNodeKey(output.getNodeKey())
                        .setType(output.getType()))
                .toList();
        
        outputList.forEach(workflowOutputMapper::insert);

        log.info("成功保存工作流配置，workflowId: {}, 输入节点数: {}, 输出节点数: {}", 
                workflowId, formList.size(), outputList.size());
    }

    @Override
    public PageVo<SystemWorkflowPageItemVo> page(final Integer page, final Integer size, final String keyword, final Long categoryId) {
        QueryWrapper<Workflow> qw = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            qw.lambda().like(Workflow::getName, keyword);
        }
        if (categoryId != null) {
            qw.lambda().eq(Workflow::getWorkflowCategoryId, String.valueOf(categoryId));
        }
        qw.lambda().orderByDesc(Workflow::getCreateTime);

        // 简单分页（不引入分页插件）：查询全部转为简单分页
        List<Workflow> all = workflowMapper.selectList(qw);
        long total = all.size();
        int from = Math.max(0, (page - 1) * size);
        int to = Math.min(all.size(), from + size);
        List<Workflow> pageList = from >= to ? List.of() : all.subList(from, to);

        // 载入类别名称映射
        List<WorkflowCategory> cats = workflowCategoryMapper.selectList(new QueryWrapper<WorkflowCategory>().lambda());
        Map<String, String> catNameMap = cats.stream()
                .collect(Collectors.toMap(c -> String.valueOf(c.getId()), WorkflowCategory::getName));

        List<SystemWorkflowPageItemVo> items = pageList.stream()
                .map(w -> new SystemWorkflowPageItemVo()
                        .setWorkflowId(w.getId())
                        .setName(w.getName())
                        .setDescription(w.getDescription())
                        .setUrl(w.getUrl())
                        .setCategoryName(catNameMap.getOrDefault(w.getWorkflowCategoryId(), null))
                        .setCreditsDeducted(w.getCreditsDeducted()))
                .collect(Collectors.toList());

        return new PageVo<SystemWorkflowPageItemVo>().setTotal(total).setItems(items);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWorkflow(final UpdateWorkflowDto dto) {
        Workflow w = workflowMapper.selectById(dto.getWorkflowId());
        if (w == null) {
            throw new UniversalException("工作流不存在");
        }
        w.setName(dto.getName());
        w.setWorkflowCategoryId(String.valueOf(dto.getWorkflowCategoryId()));
        workflowMapper.updateById(w);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWorkflow(final DeleteWorkflowDto dto) {
        // 仅删除与工作流配置相关的数据：表单与输出。用户作品（workflow_result）不删除。
        Long workflowId = dto.getWorkflowId();

        // 删除 workflow_form 配置
        workflowFormMapper.delete(new QueryWrapper<WorkflowForm>().lambda()
                .eq(WorkflowForm::getWorkflowId, workflowId));

        // 删除 workflow_output 配置
        workflowOutputMapper.delete(new QueryWrapper<WorkflowOutput>().lambda()
                .eq(WorkflowOutput::getWorkflowId, workflowId));

        // 最后删除工作流主表
        workflowMapper.deleteById(workflowId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCategory(final CreateWorkflowCategoryDto dto) {
        WorkflowCategory entity = new WorkflowCategory()
                .setName(dto.getName());
        workflowCategoryMapper.insert(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(final UpdateWorkflowCategoryDto dto) {
        WorkflowCategory c = workflowCategoryMapper.selectById(dto.getCategoryId());
        if (c == null) {
            throw new UniversalException("类别不存在");
        }
        c.setName(dto.getName());
        workflowCategoryMapper.updateById(c);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(final DeleteWorkflowCategoryDto dto) {
        // 可根据业务校验是否存在关联工作流
        workflowCategoryMapper.deleteById(dto.getCategoryId());
    }

    @Override
    public List<SystemWorkflowCategoryVo> getCategoryList() {
        List<WorkflowCategory> filterList = workflowCategoryMapper.selectList(
                new QueryWrapper<WorkflowCategory>().lambda().orderByDesc(WorkflowCategory::getCreateTime)
        );
        return filterList.stream()
                .map(c -> new SystemWorkflowCategoryVo()
                        .setCategoryId(c.getId())
                        .setName(c.getName()))
                .collect(Collectors.toList());
    }

    /**
     * 验证输入节点配置
     * 
     * 规则：
     * - options 为可选项（包括 RADIO_SELECTOR/CHECKBOX_SELECTOR）
     * - 若提供 options，则必须为有效的 JSON 对象
     *
     * @param formNodeList 表单节点配置列表
     * @throws UniversalException 验证失败时抛出
     */
    private void validateFormNodeConfigs(List<SaveWorkflowConfigDto.FormNodeConfig> formNodeList) {
        for (SaveWorkflowConfigDto.FormNodeConfig config : formNodeList) {
            String optionsStr = config.getOptions();

            // options 为可选；若提供则校验为有效 JSON 对象
            if (StringUtils.hasText(optionsStr)) {
                try {
                    JSON.parseObject(optionsStr);
                } catch (Exception e) {
                    throw new UniversalException(
                            String.format("节点 [%s] 的 options 格式错误，必须为有效的 JSON 对象", 
                                    config.getNodeKey()));
                }
            }
        }
    }
}

