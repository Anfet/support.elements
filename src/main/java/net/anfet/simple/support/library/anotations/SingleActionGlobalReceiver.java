package net.anfet.simple.support.library.anotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Нотация предназначается для методов, которые должны реализовывпть получение глобальных бродкастов
 * Функция обработки принимает следующие параметры {@link android.content.Context}, {@link android.content.Intent}, {@link android.content.BroadcastReceiver}
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SingleActionGlobalReceiver {
	String value();
}
