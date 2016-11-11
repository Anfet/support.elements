package net.anfet.simple.support.library.anotations;

import android.support.annotation.AnimRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Указывает какую анимацию использовать для активити
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface ActivityTransitition {
	@AnimRes int in();

	@AnimRes int out();
}
