-- 为 workflow_result 表添加元数据字段
-- 用于保存生成作品时使用的表单参数（包含 tips、type、options 等元数据）

ALTER TABLE workflow_result 
ADD COLUMN workflow_id BIGINT COMMENT '工作流ID',
ADD COLUMN form_params JSON COMMENT '生成时使用的表单参数（包含tips、type、options等元数据）',
ADD INDEX idx_workflow_id (workflow_id);

-- 添加注释说明
ALTER TABLE workflow_result MODIFY COLUMN form_params JSON COMMENT 'JSON格式存储，包含workflowId和taskNodeContainer数组，taskNodeContainer中包含nodeKey、inputs、nodeValue、isUpload、tips、type、options等字段';

