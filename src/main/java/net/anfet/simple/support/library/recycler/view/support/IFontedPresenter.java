package net.anfet.simple.support.library.recycler.view.support;

import net.anfet.simple.support.library.utils.IFonted;

/**
 * Базовый презентер в котором есть отсылка к шрифту
 */

public interface IFontedPresenter<T, X extends RecycleViewHolder<T>> extends IPresenter<T, X>, IFonted {

}
