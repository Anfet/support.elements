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
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

/**
 * Враппер для адаптера
 */

public class RecyclerViewAdapter<T> extends RecyclerView.Adapter<RecycleViewHolder<T>> {


	private final Context context;
	private final LinkedList<T> items;
	private final IPresenter presenter;
	private final Queue<RecycleViewHolder<T>> pool;

	public RecyclerViewAdapter(@NonNull Context context, @Nullable Collection<T> items, @NonNull IPresenter presenter) {
		Assert.assertNotNull(context);
		this.context = context;

		this.items = new LinkedList<>();
		if (items != null) {
			this.items.addAll(items);
		}

		Assert.assertNotNull(presenter);
		this.presenter = presenter;

		this.pool = new SynchronousQueue<>();
	}


	@Override
	public RecycleViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
		RecycleViewHolder<T> holder = pool.poll();
		if (holder == null) {
			holder = presenter.getNewViewHolder(context, parent);
			presenter.initHolder(holder);
		}
		return holder;
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

	public void removeItem(T item) {
		synchronized (items) {
			int idx = items.indexOf(item);
			if (idx > -1) {
				items.remove(item);
			}

			notifyItemRemoved(idx);
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

	public void releaseViewHolder(RecycleViewHolder<T> holder) {
		if (!pool.contains(holder)) pool.offer(holder);
	}

	public void addItems(T... objects) {
		synchronized (this.items) {
			for (T object : objects) {
				addItem(object);
			}
		}
	}

	private void addItem(T object) {
		synchronized (this.items) {
			items.add(object);
		}

		notifyItemInserted(items.size() - 1);
	}

	public void notifyItemChanged(T object) {
		synchronized (items) {
			int idx = items.indexOf(object);
			if (idx > -1) {
				notifyItemChanged(idx);
			}
		}
	}
}
