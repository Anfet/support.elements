package net.anfet.simple.support.library.recycler.view.support;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Для удобства
 */

public class WrappedClickListener<T> implements IClickListener<T> {
	@Override
	public boolean onItemClicked(@NonNull View view, @NonNull T t, int position) {
		return false;
	}

	@Override
	public boolean onLongItemClicked(@NonNull View view, @NonNull T t, int position) {
		return false;
	}
}
