package com.cn.system.service.impl;


import com.cn.common.constant.NoticeConstant;
import com.cn.common.structure.NoticeStructure;
import com.cn.common.utils.RedisUtils;
import com.cn.system.dto.SystemNoticeSetDto;
import com.cn.system.service.SystemNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import cn.dev33.satoken.secure.SaSecureUtil;

/**
 * 系统公告服务实现类
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class SystemNoticeServiceImpl implements SystemNoticeService {

    private final RedisUtils redisUtils;

    @Override
    public void set(final SystemNoticeSetDto dto) {
        LocalDateTime finalCreateTime = dto.getCreateTime() != null ? dto.getCreateTime() : LocalDateTime.now();
        String md5 = SaSecureUtil.md5(dto.getTitle() + "|" + dto.getPublisher() + "|" + dto.getContent() + "|" + finalCreateTime.toString());
        NoticeStructure vo = new NoticeStructure()
                .setTitle(dto.getTitle())
                .setPublisher(dto.getPublisher())
                .setContent(dto.getContent())
                .setCreateTime(finalCreateTime)
                .setMd5(md5);
        redisUtils.set(NoticeConstant.NOTICE, vo);
    }

    @Override
    public void clear() {
        redisUtils.delKey(NoticeConstant.NOTICE);
    }






}


