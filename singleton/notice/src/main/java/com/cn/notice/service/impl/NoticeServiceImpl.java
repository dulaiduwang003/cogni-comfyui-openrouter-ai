package com.cn.notice.service.impl;

import com.cn.common.constant.NoticeConstant;
import com.cn.common.structure.NoticeStructure;
import com.cn.common.utils.RedisUtils;
import com.cn.notice.service.NoticeService;
import com.cn.notice.vo.NoticeVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import cn.dev33.satoken.secure.SaSecureUtil;
import java.time.LocalDateTime;

/**
 * 通知服务实现类
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {


    private final RedisUtils redisUtils;


    @Override
    public NoticeVo getNotice() {
        NoticeStructure value = (NoticeStructure)redisUtils.get(NoticeConstant.NOTICE);
       if (value != null) {
           String md5 = value.getMd5();
           if (md5 == null) {
               LocalDateTime ct = value.getCreateTime();
               md5 = SaSecureUtil.md5(value.getTitle() + "|" + value.getPublisher() + "|" + value.getContent() + "|" + (ct == null ? "" : ct.toString()));
               value.setMd5(md5);
               redisUtils.set(NoticeConstant.NOTICE, value);
           }
           return new NoticeVo()
                   .setCreateTime(value.getCreateTime())
                   .setContent(value.getContent())
                   .setPublisher(value.getPublisher())
                   .setTitle(value.getTitle())
                   .setMd5(md5);
       }else {
           return null;
       }
    }


}


