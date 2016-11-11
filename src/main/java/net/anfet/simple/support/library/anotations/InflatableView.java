package net.anfet.simple.support.library.anotations;

import android.support.annotation.IdRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Oleg on 12.07.2016.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
/**
 * Нотация для инжектируемых вьюшек.
 */
public @interface InflatableView {
	@IdRes int value();
}
