package net.anfet.simple.support.library.lists;

/**
 * Суррогатный ключ
 */

public abstract class WrappedKey<T extends Object> implements Key {
	private final T value;


	public WrappedKey(T value) {
		this.value = value;
	}

	@Override
	public Long getId() {
		return null;
	}

	public T getValue() {
		return value;
	}
}
