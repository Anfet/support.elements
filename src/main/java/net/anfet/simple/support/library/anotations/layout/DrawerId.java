package net.anfet.simple.support.library.anotations.layout;

import android.support.annotation.IdRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Oleg on 13.09.2016.
 */

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface DrawerId {
	@IdRes int view();

	@IdRes int layout();

	boolean toggleEnabled() default true;
}
