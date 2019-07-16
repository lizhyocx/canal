package com.alibaba.otter.canal.instance.manager.model;

import java.lang.annotation.*;

/**
 * NOTE:
 *
 * @author lizhiyang
 * @Date 2019-07-09 15:20
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CanalField {
    String value();
}
