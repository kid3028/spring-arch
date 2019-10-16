package com.qull.springarch.stereotype;

import java.lang.annotation.*;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 13:42
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {
    String value() default "";
}
