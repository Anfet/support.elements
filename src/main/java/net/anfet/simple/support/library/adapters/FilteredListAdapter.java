package net.anfet.simple.support.library.adapters;

import android.content.Context;

import net.anfet.abstraction.IFilter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Списочный адаптер с возможностью фильтрации и сортировки
 */
public class FilteredListAdapter<T> extends ListHolderAdapter<T> {

	private final List<IFilter<T>> filters;
	private List<T> filteredItems;

	public FilteredListAdapter(Context context, int layoutId, List<T> items) {
		super(context, layoutId, items);
		this.filters = new LinkedList<>();
		this.filteredItems = new ArrayList<>();
	}

	public FilteredListAdapter<T> filterConditionChanged() {
		forceContentChanged();
		return this;
	}

	@Override
	public List<T> getItems() {
		if (requireUpdate.get()) {
			try {
				filteredItems = new ArrayList<>(super.getItems());
				if (filters.isEmpty() || filteredItems.isEmpty())
					return filteredItems;

				Iterator<T> iterator = filteredItems.iterator();
				while (iterator.hasNext()) {
					T item = iterator.next();
					boolean remove = false;
					for (IFilter<T> filter : filters) {
						if (!filter.filter(item)) {
							remove = true;
							break;
						}
					}
					if (remove)
						iterator.remove();
				}
			} finally {
				requireUpdate.set(false);
			}
		}

		return filteredItems;
	}

	public FilteredListAdapter<T> clearFilters() {
		synchronized (filters) {
			filters.clear();
		}

		requireUpdate.set(true);
		return this;
	}

	public FilteredListAdapter<T> addFilter(IFilter<T> filter) {
		synchronized (filters) {
			filters.add(filter);
		}

		requireUpdate.set(true);
		return this;
	}

	public FilteredListAdapter<T> removeFilter(IFilter<T> filter) {
		synchronized (filters) {
			filters.remove(filter);
		}

		requireUpdate.set(true);
		return this;
	}
}
