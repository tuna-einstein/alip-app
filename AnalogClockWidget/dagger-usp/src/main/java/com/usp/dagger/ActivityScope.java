package com.usp.dagger;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by umasankar on 3/8/15.
 */
@Qualifier
@Target({FIELD, PARAMETER, METHOD})
@Documented
@Retention(RUNTIME)
public @interface ActivityScope {
}
