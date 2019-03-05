package com.p2p.validations;

import com.p2p.validations.checkers.NotEmptyStringCheck;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@net.sf.oval.configuration.annotation.Constraint(checkWith = NotEmptyStringCheck.class)
public @interface NotEmptyString {

    String message() default "must not be empty";
}
