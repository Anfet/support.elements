package net.anfet.simple.support.library.recycler.view.support;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import junit.framework.Assert;

import net.anfet.simple.support.library.anotations.Font;
import net.anfet.simple.support.library.anotations.Layout;
import net.anfet.simple.support.library.exceptions.NoLayoutException;
import net.anfet.simple.support.library.inflation.InflateHelper;
import net.anfet.simple.support.library.utils.Fonts;

/**
 * Презентер заполняющий своих наследников по рефлексии
 */

public abstract class InflatedPresenter<T> implements IFontedPresenter<T, RecycleViewHolder<T>>, IClickListener<T> {

	public static final int FLAG_ITEMS_NON_INTERACTIVE = 0;
	public static final int FLAG_ITEMS_CLICKABLE = 1;
	public static final int FLAG_ITEMS_LONG_CLICKABLE = 2;

	@Nullable
	private IClickListener<T> clickListener = null;

	protected InflatedPresenter() {

	}

	public InflatedPresenter(@Nullable IClickListener<T> clickListener) {
		this.clickListener = clickListener;
	}

	@Nullable
	@Override
	public String getFont() {
		Font font = getClass().getAnnotation(Font.class);
		if (font != null) {
			return font.value();
		}

		return null;
	}

	public void setClickListener(@Nullable IClickListener<T> clickListener) {
		this.clickListener = clickListener;
	}

	@Override
	public void initHolder(@NonNull RecycleViewHolder<T> holder) {
		Assert.assertNotNull(holder);

		Font font = getClass().getAnnotation(Font.class);
		if (font != null) {
			Fonts.setFont(holder.itemView, font.value());
		} else if (getFont() != null) {
			Fonts.setFont(holder.itemView, getFont());
		}

		InflateHelper.registerSimpleHandlers(this, holder.itemView, getClass());
		InflateHelper.injectViewsAndFragments(this, holder.itemView, null, getClass());
	}

	@Override
	public int getLayoutId() {
		if (getClass().isAnnotationPresent(Layout.class)) {
			return getClass().getAnnotation(Layout.class).value();
		}

		throw new NoLayoutException();
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
	public void populateView(@NonNull Context context, @NonNull RecycleViewHolder<T> holder, @Nullable final T t, final int position) {
		Assert.assertNotNull(holder);
		InflateHelper.injectViewsAndFragments(this, holder.itemView, null, getClass());

		if ((getClickableFlags() & FLAG_ITEMS_CLICKABLE) > 0) {
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					onItemClicked(view, t, position);
				}
			});
		}

		if ((getClickableFlags() & FLAG_ITEMS_LONG_CLICKABLE) > 0) {
			holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					return onLongItemClicked(view, t, position);
				}
			});
		}
	}

	@Override
	public boolean onItemClicked(@NonNull View view, @NonNull T t, int position) {
		return clickListener != null && clickListener.onItemClicked(view, t, position);
	}

	@Override
	public boolean onLongItemClicked(@NonNull View view, @NonNull T t, int position) {
		return clickListener != null && clickListener.onLongItemClicked(view, t, position);
	}

	/**
	 * @return сочетание флагов {@link InflatedPresenter#FLAG_ITEMS_CLICKABLE} и {@link InflatedPresenter#FLAG_ITEMS_LONG_CLICKABLE}
	 */
	protected int getClickableFlags() {
		return FLAG_ITEMS_NON_INTERACTIVE;
	}


	@Override
	public RecycleViewHolder<T> getNewViewHolder(@NonNull Context context, @NonNull ViewGroup parent) {
		RecycleViewHolder<T> holder = new RecycleViewHolder<T>(context, LayoutInflater.from(context).inflate(getLayoutId(), parent, false));
		initHolder(holder);
		return holder;
	}
}
