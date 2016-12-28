package net.anfet.simple.support.library.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import net.anfet.simple.support.library.anotations.Font;
import net.anfet.simple.support.library.anotations.Layout;
import net.anfet.simple.support.library.inflation.InflateHelper;
import net.anfet.simple.support.library.inflation.ViewRootWrapper;
import net.anfet.simple.support.library.utils.Fonts;


/**
 * Более удобная обертка над простым адаптером
 */

abstract class HolderAdapter<T> extends BaseAdapter {

	private final Context context;
	private final int layoutId;

	HolderAdapter(Context context, int layoutId) {
		this.context = context;
		this.layoutId = layoutId;
	}

	@Override
	public abstract T getItem(int i);

	@Override
	public final View getView(int i, View view, ViewGroup viewGroup) {
		View root = view != null ? view : LayoutInflater.from(getContext()).inflate(getLayoutId(), viewGroup, false);
		if (getTypeface() != null) {
			Fonts.setFont(root, getTypeface());
		}

		populateView(root, getItem(i), i);
		return root;
	}

	public void populateView(View root, T t, int position) {
		InflateHelper.injectViewsAndFragments(this, new ViewRootWrapper(root), null, getClass());
		InflateHelper.registerSimpleHandlers(this, root, getClass());
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	public Context getContext() {
		return context;
	}

	private int getLayoutId() {
		if (getClass().isAnnotationPresent(Layout.class)) {
			return getClass().getAnnotation(Layout.class).value();
		}

		return layoutId;
	}

	protected String getTypeface() {

		if (getClass().isAnnotationPresent(Font.class)) {
			return getClass().getAnnotation(Font.class).value();
		}

		return null;
	}

}
