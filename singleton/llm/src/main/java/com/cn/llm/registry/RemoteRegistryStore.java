package com.cn.llm.registry;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.cn.common.base.BasePage;
import com.cn.llm.dto.GetAvailableModelPageDto;
import com.cn.llm.dto.GetAvailableModelListDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 远程注册存储
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Slf4j
@Component
public class RemoteRegistryStore {

    private final AtomicReference<String> dataRef = new AtomicReference<>();

    private volatile Instant lastUpdatedAt;

    
    private final ObjectMapper objectMapper = new ObjectMapper();
    // FastJSON 预解析缓存
    private final AtomicReference<JSONArray> fastjsonArrayRef = new AtomicReference<>();

    // 新增：首个 FREE/PAID 文本模型 id 缓存
    private final AtomicReference<String> firstFreeTextModelIdRef = new AtomicReference<>();
    private final AtomicReference<String> firstPaidTextModelIdRef = new AtomicReference<>();

    // 新增：读取 open-router.plugins.file-parser.enabled 配置
    private final com.cn.llm.config.OpenRouterConfig openRouterConfig;

    public RemoteRegistryStore(com.cn.llm.config.OpenRouterConfig openRouterConfig) {
        this.openRouterConfig = openRouterConfig;
    }

    public void update(String data) {
        dataRef.set(data);
        lastUpdatedAt = Instant.now();
        try {
            JSONArray arr = (data == null || data.isBlank())
                    ? null
                    : JSON.parseArray(data);
            fastjsonArrayRef.set(arr);
            
            // 调试：输出前几个模型的结构
            if (arr != null && !arr.isEmpty()) {
                log.info("远程注册数据结构调试 - 总数: {}", arr.size());
                int debugCount = Math.min(3, arr.size());
                for (int i = 0; i < debugCount; i++) {
                    Object item = arr.get(i);
                    if (item instanceof JSONObject obj) {
                        log.info("模型 {} 结构: id={}, paymentMode={}, outputType={}", 
                                i + 1, obj.getString("id"), obj.getString("paymentMode"), obj.get("outputType"));
                    }
                }
            }
            
            // 计算首个 FREE/PAID 文本模型 id
            recomputeFirstTextModelIds(arr);
        } catch (Exception e) {
            log.error("解析远程注册数据失败", e);
            fastjsonArrayRef.set(null);
            firstFreeTextModelIdRef.set(null);
            firstPaidTextModelIdRef.set(null);
        }
    }


    public Object get(String inputTypeCsv, String outputTypeCsv, String nameQuery) {
        // 兼容旧签名，委托到新的 DTO 版本
       GetAvailableModelPageDto query = new GetAvailableModelPageDto();
        query.setInputType(inputTypeCsv);
        query.setOutputType(outputTypeCsv);
        query.setName(nameQuery);
        return get(query);
    }

    // 新增：基于 DTO 的查询，支持多模态与推理筛选和分页
    public Object get(GetAvailableModelPageDto query) {
        final JSONArray current = fastjsonArrayRef.get();
        if (current == null || current.isEmpty()) {
            // 返回空的分页结果
            return new BasePage<JSONObject>().setItems(new java.util.ArrayList<>()).setTotal(0L);
        }
        
        final Set<String> inputTypeFilter = normalizeCsv(query == null ? null : query.getInputType());
        final Set<String> outputTypeFilter = normalizeCsv(query == null ? null : query.getOutputType());
        final String q = (query == null || query.getName() == null) ? null : query.getName().trim().toLowerCase();
        final Boolean requireReasoning = query == null ? null : query.getSupportReasoning();
        final String requirePaymentMode = query == null ? null : query.getPaymentMode();

        // 筛选数据
        List<JSONObject> filteredData = filterData(current, inputTypeFilter, outputTypeFilter, q, requireReasoning, requirePaymentMode);
        // 为每个模型项添加是否支持 PDF 的回显字段
        injectPdfSupport(filteredData);
        // 为每个模型项添加是否支持联网搜索的回显字段
        injectWebSupport(filteredData);
        
        // 强制分页处理
        return createPagedResult(filteredData, query);
    }
    
    // 新增：返回未分页 List 结果（使用无分页筛选 DTO）
    public List<JSONObject> list(GetAvailableModelListDto filter) {
        final JSONArray current = fastjsonArrayRef.get();
        if (current == null || current.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        final Set<String> inputTypeFilter = normalizeCsv(filter == null ? null : filter.getInputType());
        final Set<String> outputTypeFilter = normalizeCsv(filter == null ? null : filter.getOutputType());
        final String q = (filter == null || filter.getName() == null) ? null : filter.getName().trim().toLowerCase();
        final Boolean requireReasoning = filter == null ? null : filter.getSupportReasoning();
        final String requirePaymentMode = filter == null ? null : filter.getPaymentMode();
        List<JSONObject> result = filterData(current, inputTypeFilter, outputTypeFilter, q, requireReasoning, requirePaymentMode);
        // 为每个模型项添加是否支持 PDF 的回显字段
        injectPdfSupport(result);
        // 为每个模型项添加是否支持联网搜索的回显字段
        injectWebSupport(result);
        return result;
    }
    

    
    
    /**
     * 筛选数据
     */
    private List<JSONObject> filterData(JSONArray current, Set<String> inputTypeFilter, Set<String> outputTypeFilter,
                                       String q, Boolean requireReasoning, String requirePaymentMode) {
        List<JSONObject> out = new java.util.ArrayList<>();
        for (Object o : current) {
            if (!(o instanceof JSONObject item)) continue;
            
            // inputType 过滤
            if (inputTypeFilter != null && !inputTypeFilter.isEmpty()) {
                JSONArray inputTypeNode = item.getJSONArray("inputType");
                if (inputTypeNode == null) continue;
                Set<String> itemTypes = new HashSet<>();
                for (Object t : inputTypeNode) {
                    itemTypes.add(safeLower(t == null ? null : String.valueOf(t)));
                }
                boolean ok = itemTypes.containsAll(inputTypeFilter);
                if (!ok) continue;
            }
            
            // outputType 过滤（任一匹配）
            if (outputTypeFilter != null && !outputTypeFilter.isEmpty()) {
                JSONArray outputTypeNode = item.getJSONArray("outputType");
                if (outputTypeNode == null) continue;
                Set<String> itemOutTypes = new HashSet<>();
                for (Object t : outputTypeNode) {
                    itemOutTypes.add(safeLower(t == null ? null : String.valueOf(t)));
                }
                boolean okOut = false;
                for (String wanted : outputTypeFilter) {
                    if (itemOutTypes.contains(wanted)) { okOut = true; break; }
                }
                if (!okOut) continue;
            }
            
            // 名称模糊匹配：仅匹配 name
            if (q != null && !q.isEmpty()) {
                String name = safeLower(item.getString("name"));
                if (!name.contains(q)) {
                    continue;
                }
            }
            
            // 检查推理支持
            if (requireReasoning != null) {
                Boolean modelIsReasoning = item.getBoolean("supportReasoning");
                if (modelIsReasoning == null || !modelIsReasoning.equals(requireReasoning)) {
                    continue;
                }
            }

            // 检查付费模式
            if (requirePaymentMode != null && !requirePaymentMode.trim().isEmpty()) {
                String modelPaymentMode = item.getString("paymentMode");
                if (modelPaymentMode == null || !requirePaymentMode.trim().equals(modelPaymentMode.trim())) {
                    continue;
                }
            }
            out.add(item);
        }
        return out;
    }
    
    /**
     * 为模型数据添加 PDF 支持回显字段
     */
    private void injectPdfSupport(List<JSONObject> data) {
        boolean pdfEnabled = isPdfEnabled();
        for (JSONObject item : data) {
            if (item == null) continue;
            item.put("supportPdf", pdfEnabled);
        }
    }

    /**
     * 为模型数据添加联网搜索支持回显字段
     */
    private void injectWebSupport(List<JSONObject> data) {
        boolean webEnabled = isWebEnabled();
        for (JSONObject item : data) {
            if (item == null) continue;
            item.put("supportWeb", webEnabled);
        }
    }

    private boolean isPdfEnabled() {
        return Boolean.TRUE.equals(openRouterConfig.getPlugins().getFileParser().getEnabled());
    }

    private boolean isWebEnabled() {
        return Boolean.TRUE.equals(openRouterConfig.getPlugins().getWeb().getEnabled());
    }
    
    /**
     * 创建分页结果
     */
    private BasePage<JSONObject> createPagedResult(List<JSONObject> data, GetAvailableModelPageDto query) {
        // 分页参数处理
        int page = Math.max(1, query.getPage() != null ? query.getPage() : 1);
        int size = 20; // 固定每页大小为20条
        
        long total = data.size();
        
        // 计算分页
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, data.size());
        
        List<JSONObject> pageData = (startIndex >= data.size()) ? 
            new java.util.ArrayList<>() : 
            data.subList(startIndex, endIndex);
        
        return new BasePage<JSONObject>()
            .setItems(pageData)
            .setTotal(total);
    }
    
    private void recomputeFirstTextModelIds(JSONArray arr) {
        String free = null;
        String paid = null;
        int totalCount = 0;
        int textOutputCount = 0;
        int freeCount = 0;
        int paidCount = 0;
        
        if (arr != null) {
            totalCount = arr.size();
            for (Object o : arr) {
                if (!(o instanceof JSONObject item)) continue;
                // 仅选择输出包含 text 的模型
                boolean outputsText = false;
                JSONArray outs = item.getJSONArray("outputType");
                if (outs != null) {
                    for (Object t : outs) {
                        if (t != null && "text".equalsIgnoreCase(String.valueOf(t))) {
                            outputsText = true;
                            break;
                        }
                    }
                }
                if (!outputsText) continue;
                textOutputCount++;
                
                String id = item.getString("id");
                String paymentMode = item.getString("paymentMode");
                if (free == null && "FREE".equalsIgnoreCase(paymentMode)) {
                    free = id;
                    freeCount++;
                }
                if (paid == null && "PAID".equalsIgnoreCase(paymentMode)) {
                    paid = id;
                    paidCount++;
                }
                if (free != null && paid != null) break;
            }
        }
        firstFreeTextModelIdRef.set(free);
        firstPaidTextModelIdRef.set(paid);
        
        log.info("重新计算首个文本模型ID - 总模型数: {}, 支持文本输出: {}, FREE模型数: {}, PAID模型数: {}, 首个免费模型: {}, 首个付费模型: {}", 
                 totalCount, textOutputCount, freeCount, paidCount, free, paid);
    }

    public String getFirstFreeTextModelId() {
        return firstFreeTextModelIdRef.get();
    }

    public String getFirstPaidTextModelId() {
        return firstPaidTextModelIdRef.get();
    }

    public String resolveModelSlugById(String id) {
		final String current = dataRef.get();
		if (current == null || current.isBlank() || id == null || id.isBlank()) {
			return null;
		}
		try {
			JsonNode root = objectMapper.readTree(current);
			if (!root.isArray()) {
				return null;
			}
			for (JsonNode item : root) {
				if (!item.isObject()) continue;
				String itemId = item.path("id").asText("");
				if (id.equals(itemId)) {
					String slug = item.path("model").asText("");
					return (slug == null || slug.isBlank()) ? null : slug;
				}
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

    /**
     * 根据模型 id 获取其最大上下文 token（若不存在返回 null）
     */
    public Integer getMaxTokensById(String id) {
        final JSONArray current = fastjsonArrayRef.get();
        if (current == null || current.isEmpty() || id == null || id.isBlank()) {
            return null;
        }
        for (Object o : current) {
            if (!(o instanceof JSONObject item)) continue;
            String itemId = item.getString("id");
            if (id.equals(itemId)) {
                Integer mt = item.getInteger("maxTokens");
                return (mt == null || mt <= 0) ? null : mt;
            }
        }
        return null;
    }

    /**
     * 根据模型 slug 获取其最大上下文 token（若不存在返回 null）
     */
    public Integer getMaxTokensBySlug(String slug) {
        final JSONArray current = fastjsonArrayRef.get();
        if (current == null || current.isEmpty() || slug == null || slug.isBlank()) {
            return null;
        }
        for (Object o : current) {
            if (!(o instanceof JSONObject item)) continue;
            String modelSlug = item.getString("model");
            if (slug.equals(modelSlug)) {
                Integer mt = item.getInteger("maxTokens");
                return (mt == null || mt <= 0) ? null : mt;
            }
        }
        return null;
    }

    /**
     * 根据模型 id 返回完整的模型对象（包含 supportPdf、supportWeb 字段）
     */
    public com.alibaba.fastjson2.JSONObject getById(String id) {
        final com.alibaba.fastjson2.JSONArray current = fastjsonArrayRef.get();
        if (current == null || current.isEmpty() || id == null || id.isBlank()) {
            return null;
        }
        for (Object o : current) {
            if (!(o instanceof com.alibaba.fastjson2.JSONObject item)) continue;
            String itemId = item.getString("id");
            if (id.equals(itemId)) {
                // 注入 supportPdf 回显字段
                boolean pdfEnabled = isPdfEnabled();
                item.put("supportPdf", pdfEnabled);
                // 注入 supportWeb 回显字段
                boolean webEnabled = isWebEnabled();
                item.put("supportWeb", webEnabled);
                return item;
            }
        }
        return null;
    }

    private Set<String> normalizeCsv(String csv) {
        if (csv == null || csv.isBlank()) return null;
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .map(this::safeLower)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(HashSet::new));
    }

    private String safeLower(String s) {
        return s == null ? "" : s.toLowerCase();
    }
} 