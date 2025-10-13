package com.cn.comfyui.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 工作流接口VO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class WorkflowInterfaceVo implements Serializable {

    private Long workflowId;

    private String name;

    private List<WorkflowsFormContainer> formContainer;

    private Long creditsDeducted;


    @Data
    @Accessors(chain = true)
    public static class WorkflowsFormContainer {

        private String inputs;

        private String nodeKey;

        private String tips;

        private String type;

        private boolean required;

        private String options;

        private String template;

        private Long size;


    }

}
