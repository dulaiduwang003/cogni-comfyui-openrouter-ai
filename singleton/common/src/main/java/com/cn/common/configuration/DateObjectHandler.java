package com.cn.common.configuration;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * The type Date object handler.
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 */
@Component
public class DateObjectHandler implements MetaObjectHandler {
    /**
     * Insert fill.
     *
     * @param metaObject the meta object
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    /**
     * Update fill.
     *
     * @param metaObject the meta object
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }


}
