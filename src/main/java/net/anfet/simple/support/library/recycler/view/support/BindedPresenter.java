package net.anfet.simple.support.library.recycler.view.support;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.anfet.simple.support.library.anotations.fonts.UseFont;
import net.anfet.simple.support.library.anotations.layout.LayoutId;
import net.anfet.simple.support.library.exceptions.NoLayoutException;
import net.anfet.simple.support.library.recycler.view.events.IClickListener;
import net.anfet.simple.support.library.utils.Fonts;

/**
 * Презентер заполняющий своих наследников по рефлексии
 */

public abstract class BindedPresenter<T, B extends ViewDataBinding> implements IFontedPresenter<T, BinderViewHolder<T, B>> {

	@Nullable
	protected IClickListener<T> mClickListener = null;

	@Nullable
	protected IClickListener<T> mLongClickListener = null;


	protected BindedPresenter() {

	}

	public BindedPresenter<T, B> onClick(@Nullable IClickListener<T> clickListener) {
		this.mClickListener = clickListener;
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

	public BindedPresenter<T, B> onLongClick(@Nullable IClickListener<T> clickListener) {
		this.mLongClickListener = clickListener;
		return this;
	}

	@Override
	public void initHolder(@NonNull BinderViewHolder<T, B> holder) {
		UseFont font = getClass().getAnnotation(UseFont.class);
		if (font != null) {
			Fonts.setFont(holder.itemView, font.value());
		} else if (getFont() != null) {
			Fonts.setFont(holder.itemView, getFont());
		}
	}

	@Override
	public int getLayoutId() {
		if (getClass().isAnnotationPresent(LayoutId.class)) {
			return getClass().getAnnotation(LayoutId.class).value();
		}

		throw new NoLayoutException("No layout for " + getClass().getSimpleName());
	}


	@Override
	public void populateView(@NonNull Context context, @NonNull BinderViewHolder<T, B> holder, @NonNull T t, int position) {
		if (mClickListener != null) holder.itemView.setOnClickListener(view -> mClickListener.onItemClicked(view, t, position));
		if (mLongClickListener != null) holder.itemView.setOnLongClickListener(view -> mLongClickListener.onItemClicked(view, t, position));
	}

	@Override
	public BinderViewHolder<T, B> getNewViewHolder(@NonNull Context context, @NonNull ViewGroup parent) {
		B mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), getLayoutId(), parent, false);
		BinderViewHolder<T, B> holder = new BinderViewHolder<T, B>(context, mBinding.getRoot());
		holder.setBinder(mBinding);
		initHolder(holder);
		return holder;
	}
}
