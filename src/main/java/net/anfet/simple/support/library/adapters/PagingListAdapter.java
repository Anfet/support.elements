package net.anfet.simple.support.library.adapters;

import android.content.Context;
import android.util.Log;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Списочный адаптер с возможностью паггинации
 */
public class PagingListAdapter<T> extends FilteredListAdapter<T> {

	private int itemsPerPage = 10;
	private int currentPage = 0;
	private List<T> activePageItems;

	public PagingListAdapter(Context context, int layoutId, List<T> items) {
		super(context, layoutId, items);
		activePageItems = new LinkedList<>();
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int page) {
		if (page < 0 || page > getMaxPages() - 1)
			throw new IndexOutOfBoundsException();

		currentPage = page;
		requireUpdate.set(true);
	}

	public int getMaxPages() {
		List<T> items = super.getItems();
		int size = items.size();
		return size % itemsPerPage == 0 ? (size / itemsPerPage) : ((size / itemsPerPage) + 1);
	}

	@Override
	public List<T> getItems() {
		if (requireUpdate.get()) {
			List<T> items = super.getItems();
			activePageItems.clear();
			int start = currentPage * itemsPerPage;
			int end = Math.min(start + itemsPerPage, items.size());

			Log.v(getClass().getSimpleName(), "fetching from " + start + " to " + end + "; size: " + items.size());
			activePageItems = new LinkedList<>(items.subList(start, end));
		}

		return activePageItems;
	}

	public boolean hasNext() {
		return currentPage < getMaxPages() - 1;
	}

	public boolean hasPrior() {
		return currentPage > 0;
	}

	@Override
	public ListHolderAdapter<T> setItems(Collection<T> items) {
		super.setItems(items);
		currentPage = 0;
		return this;
	}

	public int getItemsPerPage() {
		return itemsPerPage;
	}

	public void setItemsPerPage(int itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
		setCurrentPage(0);
		requireUpdate.set(true);
	}

	public boolean prior() {
		try {
			if (currentPage > 0) {
				currentPage--;
				requireUpdate.set(true);
			}

			return requireUpdate.get();
		} finally {
			notifyDataSetChanged();
		}
	}

	public boolean next() {
		try {
			if (currentPage < getMaxPages() - 1) {
				currentPage++;
				requireUpdate.set(true);
			}

			return requireUpdate.get();
		} finally {
			notifyDataSetChanged();
		}
	}

	public boolean last() {
		try {
			if (currentPage != getMaxPages() - 1) {
				currentPage = getMaxPages() - 1;
				requireUpdate.set(true);
			}

			return requireUpdate.get();
		} finally {
			notifyDataSetChanged();
		}
	}

	public boolean first() {
		try {
			if (currentPage != 0) {
				currentPage = 0;
				requireUpdate.set(true);
			}

			return requireUpdate.get();
		} finally {
			notifyDataSetChanged();
		}
	}

	public int size() {
		return super.getItems().size();
	}

	public List<T> getAllItems() {
		return super.getItems();
	}
}
