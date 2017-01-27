package net.anfet.simple.support.library.lists;

/**
 * Нет такого элемента
 */

public class NoElementException extends Exception {
	public NoElementException(String message) {
		super(message);
	}

	public NoElementException() {

	}

	public NoElementException(Class<?> type, Integer id) {
		super("No element " + type.getSimpleName() + ":" + String.valueOf(id));
	}
}
