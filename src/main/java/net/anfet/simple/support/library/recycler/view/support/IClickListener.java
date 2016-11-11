package net.anfet.simple.support.library.recycler.view.support;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Интерфейс простых откликов для клика
 */

public interface IClickListener<T> {
	boolean onItemClicked(@NonNull View view, @NonNull T t, int position);

	boolean onLongItemClicked(@NonNull View view, @NonNull T t, int position);
}
