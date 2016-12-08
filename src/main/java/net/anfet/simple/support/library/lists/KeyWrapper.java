package net.anfet.simple.support.library.lists;

/**
 * Обертка для получения ключа
 */

public interface KeyWrapper<T> {
	Object getKey(T element);
}
