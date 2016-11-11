package net.anfet.simple.support.library.anotations;

import android.support.annotation.LayoutRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Нотация, указывает {@link net.anfet.simple.support.library.SupportFragment} что следует строить диалог, а не чистый фрагмент
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Alert {
	@LayoutRes int layoutId();
}
