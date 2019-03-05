package com.p2p.validations;

import com.p2p.validations.checkers.NotEmptyListCheck;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Interface NotEmptyList.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@net.sf.oval.configuration.annotation.Constraint(checkWith = NotEmptyListCheck.class)
public @interface NotEmptyList {

    String message() default "list must not be empty";
}
