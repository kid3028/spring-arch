package com.qull.springarch.stereotype;

import java.lang.annotation.*;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 13:43
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {

    boolean required() default true;

}
