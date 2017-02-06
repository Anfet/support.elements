package net.anfet.simple.support.library.recycler.view.support;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Для удобства
 */

public class WrappedClickListener<T> implements IClickListener<T> {
	@Override
	public boolean onItemClicked(@NonNull View view, @Nullable T t, int position) {
		return false;
	}

	@Override
	public boolean onLongItemClicked(@NonNull View view, @Nullable T t, int position) {
		return false;
	}
}
