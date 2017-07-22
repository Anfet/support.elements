package net.anfet.simple.support.library.recycler.view.support;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.anfet.simple.support.library.anotations.fonts.UseFont;
import net.anfet.simple.support.library.anotations.layout.LayoutId;
import net.anfet.simple.support.library.exceptions.NoLayoutException;
import net.anfet.simple.support.library.inflation.InflateHelper;
import net.anfet.simple.support.library.utils.Fonts;

/**
 * Презентер заполняющий своих наследников по рефлексии
 */

public abstract class InflatedPresenter<T> implements IFontedPresenter<T, RecycleViewHolder<T>>, IClickListener<T> {

	@Nullable
	private IClickListener<T> clickListener = null;

	@Nullable
	private IClickListener<T> longClickListener = null;


	protected InflatedPresenter() {

	}

	public InflatedPresenter<T> onClick(@Nullable IClickListener<T> longClickListener) {
		this.longClickListener = longClickListener;
		return this;
	}

	@Nullable
	@Override
	public String getFont() {
		UseFont font = getClass().getAnnotation(UseFont.class);
		if (font != null) {
			return font.value();
		}

		return null;
	}

	public InflatedPresenter<T> onLongClick(@Nullable IClickListener<T> clickListener) {
		this.longClickListener = clickListener;
		return this;
	}

	@Override
	public void initHolder(@NonNull RecycleViewHolder<T> holder) {
		UseFont font = getClass().getAnnotation(UseFont.class);
		if (font != null) {
			Fonts.setFont(holder.itemView, font.value());
		} else if (getFont() != null) {
			Fonts.setFont(holder.itemView, getFont());
		}

		InflateHelper.injectViewsAndFragments(this, holder.itemView, getClass());
	}

	@Override
	public int getLayoutId() {
		if (getClass().isAnnotationPresent(LayoutId.class)) {
			return getClass().getAnnotation(LayoutId.class).value();
		}

		throw new NoLayoutException("No layout for " + getClass().getSimpleName());
	}

	/**
	 * очищает форму
	 * @param context контекст
	 * @param holder держатель
	 * @param t элемент
	 * @param position позиция
	 */
	public void reset(@NonNull Context context, @NonNull RecycleViewHolder<T> holder, @Nullable final T t, final int position) {

	}

	@Override
	public void populateView(@NonNull Context context, @NonNull RecycleViewHolder<T> holder, @NonNull final T t, final int position) {
		InflateHelper.injectViewsAndFragments(this, holder.itemView, getClass());
		if (clickListener != null) holder.itemView.setOnClickListener(view -> clickListener.onItemClicked(view, t, position));
		if (longClickListener != null) holder.itemView.setOnLongClickListener(view -> clickListener.onItemClicked(view, t, position));
	}

	@Override
	public RecycleViewHolder<T> getNewViewHolder(@NonNull Context context, @NonNull ViewGroup parent) {
		RecycleViewHolder<T> holder = new RecycleViewHolder<T>(context, LayoutInflater.from(context).inflate(getLayoutId(), parent, false));
		initHolder(holder);
		return holder;
	}
}
