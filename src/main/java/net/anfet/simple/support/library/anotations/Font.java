package net.anfet.simple.support.library.anotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Нотация указывает InflateHelperу какой шрифт установить на текущий объект
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Font {
	String value();
}
