package net.anfet.simple.support.library.recycler.view.support;

import android.support.annotation.Nullable;

/**
 * Базовый презентер в котором есть отсылка к шрифту
 */

public interface IFontedPresenter<T, X extends RecycleViewHolder<T>> extends IPresenter<T, X> {
	/**
	 * @return базовый шрифт всех вьюшек
	 */
	@Nullable
	String getFont();
}
