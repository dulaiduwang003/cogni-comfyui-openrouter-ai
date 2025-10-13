package com.cn.common.constant;

/**
 * 缓存过期时间常量
 * 统一管理 Redis 缓存的过期时间，单位：秒
 *
 * @author Conni-X-Pro
 */
public interface CacheExpireConstant {

    /**
     * 30秒
     */
    int EXPIRE_30_SECONDS = 30;

    /**
     * 1分钟
     */
    int EXPIRE_1_MINUTE = 60;

    /**
     * 5分钟
     */
    int EXPIRE_5_MINUTES = 5 * 60;

    /**
     * 10分钟
     */
    int EXPIRE_10_MINUTES = 10 * 60;

    /**
     * 30分钟
     */
    int EXPIRE_30_MINUTES = 30 * 60;

    /**
     * 1小时
     */
    int EXPIRE_1_HOUR = 60 * 60;

    /**
     * 6小时
     */
    int EXPIRE_6_HOURS = 6 * 60 * 60;

    /**
     * 12小时
     */
    int EXPIRE_12_HOURS = 12 * 60 * 60;

    /**
     * 24小时
     */
    int EXPIRE_24_HOURS = 24 * 60 * 60;

    /**
     * 48小时
     */
    int EXPIRE_48_HOURS = 48 * 60 * 60;

    /**
     * 7天
     */
    int EXPIRE_7_DAYS = 7 * 24 * 60 * 60;

    /**
     * 10天
     */
    int EXPIRE_10_DAYS = 10 * 24 * 60 * 60;

    /**
     * 30天
     */
    int EXPIRE_30_DAYS = 30 * 24 * 60 * 60;
}

