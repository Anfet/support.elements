package net.anfet.simple.support.library.anotations.layout;

import android.support.annotation.MenuRes;

import net.anfet.simple.support.library.activities.SupportActivity;
import net.anfet.simple.support.library.activities.SupportFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Аннотация меню для {@link SupportActivity} или {@link SupportFragment}
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface MenuId {
	@MenuRes int value();
}
