package net.anfet.simple.support.library.anotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Нотация для {@link net.anfet.simple.support.library.SupportActivity} указывающая что не нужно ей содержимое
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface NoLayout {
}
