package net.anfet.simple.support.library.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Oleg on 12.07.2016.
 */
public class ListHolderAdapter<T extends Object> extends HolderAdapter<T> {

	protected final List<T> items;
	protected final AtomicBoolean requireUpdate;
	protected Comparator<T> comparator;

	public ListHolderAdapter(Context context, int layoutId, List<T> items) {
		super(context, layoutId);
		this.items = new ArrayList<>(items);
		this.comparator = null;
		this.requireUpdate = new AtomicBoolean(true);
	}

	public ListHolderAdapter(Context context, int layoutId, T[] items) {
		super(context, layoutId);
		this.items = Arrays.asList(items);
		this.comparator = null;
		this.requireUpdate = new AtomicBoolean(true);
	}

	public ListHolderAdapter<T> setComparator(Comparator<T> comparator) {
		this.comparator = comparator;
		requireUpdate.set(true);
		return this;
	}

	public List<T> getItems() {
		try {
			if (requireUpdate.get()) {
				if (comparator != null)
					Collections.sort(items, comparator);
			}
		} finally {
			requireUpdate.set(false);
		}

		return items;
	}

	public ListHolderAdapter<T> setItems(Collection<T> items) {
		synchronized (getItems()) {
			this.items.clear();
			if (items != null) {
				this.items.addAll(items);
			}
		}

		requireUpdate.set(true);
		return this;
	}

	public ListHolderAdapter<T> forceContentChanged() {
		requireUpdate.set(true);
		return this;
	}

	@Override
	public int getCount() {
		synchronized (getItems()) {
			return getItems().size();
		}
	}

	@Override
	public T getItem(int i) {
		synchronized (getItems()) {
			return getItems().get(i);
		}
	}


	public void addViews(@NonNull IListViewIterator listViewIterator, @Nullable ViewGroup group) {
		int i = 0;
		for (T item : getItems()) {
			View view = getView(i, null, group);
			if (listViewIterator.onView(view, item)) {
				if (group != null) {
					group.addView(view);
				}
			}

			i++;
		}
	}

	public interface IListViewIterator<T> {
		boolean onView(View view, T item);
	}
}
