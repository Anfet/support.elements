package net.anfet.simple.support.library.exceptions;

/**
 * Выбрасывается когда не указан какой либо Id
 */

public class NoIdException extends RuntimeException {

	public NoIdException() {

	}

	public NoIdException(String s) {
		super(s);
	}
}
