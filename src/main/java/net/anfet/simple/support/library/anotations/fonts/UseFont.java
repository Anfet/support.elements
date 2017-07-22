package net.anfet.simple.support.library.anotations.fonts;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Нотация указывает InflateHelperу какой шрифт установить на текущий объект
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface UseFont {
	String value();
}
