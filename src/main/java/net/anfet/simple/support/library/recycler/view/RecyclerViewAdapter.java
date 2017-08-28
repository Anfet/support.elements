package net.anfet.simple.support.library.recycler.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import net.anfet.simple.support.library.recycler.view.support.IPresenter;
import net.anfet.simple.support.library.recycler.view.support.IStableIdSupply;
import net.anfet.simple.support.library.recycler.view.support.RecycleViewHolder;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

/**
 * Враппер для адаптера
 */

public class RecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


	protected final Context context;
	protected final LinkedList<T> items;
	protected final IPresenter presenter;
	protected final Queue<RecyclerView.ViewHolder> pool;
	protected IStableIdSupply<T> mStableIdSupply;

	public RecyclerViewAdapter(@NonNull Context context, @NonNull IPresenter presenter, @Nullable Collection<T> items) {
		this.context = context;

		this.items = new LinkedList<>();
		if (items != null) {
			this.items.addAll(items);
		}

		this.presenter = presenter;
		this.pool = new SynchronousQueue<>();
		setHasStableIds(true);
	}

	public void setStableIdSupply(IStableIdSupply<T> supply) {
		this.mStableIdSupply = supply;
	}

	@Override
	public long getItemId(int position) {

		T item = items.get(position);
		if (item == null)
			return RecyclerView.NO_ID;


		if (mStableIdSupply != null)
			return mStableIdSupply.getStableId(item);

		return items.get(position).hashCode();
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		RecyclerView.ViewHolder holder = pool.poll();
		if (holder == null) {
			holder = presenter.getNewViewHolder(context, parent);
			presenter.initHolder(holder);
		}
		return holder;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
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
				notifyItemRemoved(idx);
			}
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

	void releaseViewHolder(RecycleViewHolder<T> holder) {
		if (!pool.contains(holder)) pool.offer(holder);
	}

	public void addItems(T... objects) {
		for (T object : objects) {
			addItem(object);
		}
	}

	public void addItem(T item, int position) {
		items.add(position, item);
		notifyItemInserted(position);
	}

	public void addItems(Collection<T> objects) {
		for (T object : objects) {
			addItem(object);
		}
	}

	private void addItem(T object) {
		synchronized (this.items) {
			items.add(object);
			notifyItemInserted(items.size() - 1);
		}
	}

	public void notifyItemChanged(T object) {
		synchronized (items) {
			int idx = items.indexOf(object);
			if (idx > -1) {
				notifyItemChanged(idx);
			}
		}
	}

	public void replace(T object) {
		synchronized (items) {
			int idx = items.indexOf(object);
			if (idx > -1) {
				items.set(idx, object);
				notifyItemChanged(idx);
			}
		}
	}


	public void removeItems(@NonNull Collection<T> items) {
		for (T item : items) removeItem(item);
	}

	public void updateAndMove(T object, int idx) {
		synchronized (items) {
			int old = items.indexOf(object);
			items.set(old, object);
			if (old != idx) {
				items.remove(old);
				items.add(idx, object);
				notifyItemMoved(old, idx);
			}
		}
	}
}
