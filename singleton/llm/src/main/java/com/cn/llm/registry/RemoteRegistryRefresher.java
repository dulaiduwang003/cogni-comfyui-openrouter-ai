package com.cn.llm.registry;

import com.cn.llm.config.OpenRouterConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 远程注册刷新器
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "open-router.remote-registry", name = "enabled", havingValue = "true")
public class RemoteRegistryRefresher {

    private final RemoteRegistryClient client;
    private final RemoteRegistryStore store;
    private final OpenRouterConfig openRouterConfig;

    @PostConstruct
    public void init() {
        log.info("启动触发远程注册拉取");
        refresh();
    }

    @Scheduled(cron = "${open-router.remote-registry.cron:0 0/30 * * * ?}")
    public void scheduledRefresh() {
        log.info("定时触发远程注册拉取");
        refresh();
    }

    private void refresh() {
        log.info("开始拉取远程注册数据");
        try {
            final String content = client.fetch();
            if (content != null && !content.isBlank()) {
                store.update(content);
                log.info("远程注册数据已刷新，长度={} 字节", content.length());
            } else {
                log.warn("远程注册数据刷新失败或为空，保留旧值");
            }
        } catch (Exception e) {
            log.error("刷新远程注册数据时发生异常: {}", e.getMessage(), e);
        }
    }
} 