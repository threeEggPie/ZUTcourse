package xyz.kingsword.course.annocations;

import xyz.kingsword.course.enmu.RoleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Role {
    RoleEnum[] value() default {};
}
