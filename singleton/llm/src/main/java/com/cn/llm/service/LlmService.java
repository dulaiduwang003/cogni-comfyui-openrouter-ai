package com.cn.llm.service;


import com.cn.llm.dto.SubmitMessageDto;
import java.util.Map;
import java.util.List;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * LLM 服务接口
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
public interface LlmService {
    
    /**
	 * 提交一条用户消息并存储到Redis
	 */
	void submitMessage(SubmitMessageDto dto);

	/**
	 * 按会话发起一次流式回复
	 * @param token 用户token（用于身份验证）
     */
	SseEmitter chatStream(String sessionId, String modelId,
						 String enableWebSearch,
						 String generateImages,
						 String token);

	/**
	 * 删除一个会话（删除该会话下的所有消息）
	 * @param sessionId 会话ID
	 * @param force 是否在会话被占用时强制删除
	 */
	void deleteSession(String sessionId, boolean force);

	/** 获取默认模型ID（用于控制器解析完整模型对象） */
	String getDefaultModelId();

	/** 获取默认模型完整对象（包含 supportPdf 等扩展字段） */
	Object getDefaultModelObject();

	/**
	 * 从Redis获取会话的最新消息
	 * @param sessionId 会话ID
	 * @param limit 限制数量
	 * @return 消息列表（按时间正序）
	 */
	List<Map<String, Object>> getChatMessages(String sessionId, int limit);

	/**
	 * 向Redis中添加助手回复消息
	 * @param sessionId 会话ID
	 * @param contentJson 消息内容JSON
	 */
	void addAssistantMessage(String sessionId, String contentJson);

	/**
	 * 检查会话是否需要回复（最后一条是否为用户消息）
	 * @param sessionId 会话ID
	 * @return 是否需要回复
	 */
	boolean needsReply(String sessionId);

} 