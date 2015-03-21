package com.usp.dagger;

/**
 * Created by umasankar on 3/8/15.
 */

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to be used for application scoped objects.
 */
@Qualifier
@Target({FIELD, PARAMETER, METHOD})
@Documented
@Retention(RUNTIME)
public @interface ApplicationScope {
}
