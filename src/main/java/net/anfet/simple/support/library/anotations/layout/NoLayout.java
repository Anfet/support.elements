package net.anfet.simple.support.library.anotations.layout;

import net.anfet.simple.support.library.activities.SupportActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Нотация для {@link SupportActivity} указывающая что не нужно ей содержимое
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface NoLayout {
}
