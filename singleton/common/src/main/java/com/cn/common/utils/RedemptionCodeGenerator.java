package com.cn.common.utils;

import java.security.SecureRandom;
import java.util.Random;

/**
 * 兑换码生成工具类
 * 
 * @author CogniX
 */
public class RedemptionCodeGenerator {

    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random RANDOM = new SecureRandom();

    /**
     * 生成指定长度的兑换码
     * 
     * @param length 兑换码长度
     * @return 兑换码
     */
    public static String generateCode(int length) {
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(CHARSET.charAt(RANDOM.nextInt(CHARSET.length())));
        }
        return code.toString();
    }

    /**
     * 生成默认8位长度的兑换码
     * 
     * @return 兑换码
     */
    public static String generateCode() {
        return generateCode(8);
    }

    /**
     * 生成带前缀的兑换码
     * 
     * @param prefix 前缀
     * @param length 后缀长度
     * @return 兑换码
     */
    public static String generateCodeWithPrefix(String prefix, int length) {
        return prefix + generateCode(length);
    }
} 