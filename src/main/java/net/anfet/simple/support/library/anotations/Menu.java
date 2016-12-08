package net.anfet.simple.support.library.anotations;

import android.support.annotation.MenuRes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Аннотация меню для {@link net.anfet.simple.support.library.SupportActivity} или {@link net.anfet.simple.support.library.SupportFragment}
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface Menu {
	@MenuRes int value();
}
