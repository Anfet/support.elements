package net.anfet.simple.support.library.recycler.view.support;

import android.support.v7.widget.RecyclerView;

import net.anfet.simple.support.library.utils.IFonted;

/**
 * Базовый презентер в котором есть отсылка к шрифту
 */

public interface IFontedPresenter<T, X extends RecyclerView.ViewHolder> extends IPresenter<T, X>, IFonted {

}
