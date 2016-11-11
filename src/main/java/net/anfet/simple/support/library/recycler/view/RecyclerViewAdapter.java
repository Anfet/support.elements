package net.anfet.simple.support.library.recycler.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import junit.framework.Assert;

import net.anfet.simple.support.library.recycler.view.support.IPresenter;
import net.anfet.simple.support.library.recycler.view.support.RecycleViewHolder;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Враппер для адаптера
 */

public class RecyclerViewAdapter<T> extends RecyclerView.Adapter<RecycleViewHolder<T>> {


	private final Context context;
	private final LinkedList<T> items;
	private final IPresenter presenter;

	public RecyclerViewAdapter(@NonNull Context context, @Nullable Collection<T> items, @NonNull IPresenter presenter) {
		Assert.assertNotNull(context);
		this.context = context;

		this.items = new LinkedList<>();
		if (items != null) {
			this.items.addAll(items);
		}

		Assert.assertNotNull(presenter);
		this.presenter = presenter;
	}


	@Override
	public RecycleViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
		return presenter.getNewViewHolder(context, parent);
	}

	@Override
	public void onBindViewHolder(RecycleViewHolder<T> holder, int position) {
		presenter.populateView(context, holder, getItem(position), position);
	}

	public T getItem(int position) throws IndexOutOfBoundsException {
		synchronized (items) {
			return items.get(position);
		}
	}

	@Override
	public int getItemCount() {
		synchronized (items) {
			return items.size();
		}
	}

	@NonNull
	public Collection<T> getItems() {
		synchronized (items) {
			return new LinkedList<>(items);
		}
	}

	public void setItems(@Nullable Collection<T> items) {
		synchronized (this.items) {
			this.items.clear();
			if (items != null) {
				this.items.addAll(items);
			}
		}

		notifyDataSetChanged();
	}
}
