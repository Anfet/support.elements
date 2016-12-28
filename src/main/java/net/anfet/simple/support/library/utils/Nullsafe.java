package net.anfet.simple.support.library.utils;

import android.support.annotation.Nullable;

/**
 * безопасное получение для нуллов
 */

public final class Nullsafe {

	public static <T> T get(@Nullable T value, @Nullable T fallback) {
		return value == null ? fallback : value;
	}
}
