package net.anfet.simple.support.library.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.anfet.abstraction.IFilter;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Фильтр для любых коллекций
 */

public class CollectionManipulator<T> {

	private final List<IFilter<T>> filters;
	private Comparator<T> comparator;

	public CollectionManipulator() {
		filters = new LinkedList<>();
	}

	public static <X> Collection<X> getItems(Collection<X> items, IFilter<X> filter) {
		return new CollectionManipulator<X>().addFilter(filter).getItems(items);
	}

	public CollectionManipulator<T> setComparator(Comparator<T> comparator) {
		this.comparator = comparator;
		return this;
	}

	public CollectionManipulator<T> addFilter(@NonNull IFilter<T> filter) {
		synchronized (filters) {
			filters.add(filter);
		}
		return this;
	}

	public CollectionManipulator<T> removeFilter(@NonNull IFilter<T> filter) {
		synchronized (filters) {
			filters.remove(filter);
		}

		return this;
	}

	@NonNull
	public Collection<T> getItems(@Nullable Collection<T> items) {
		List<T> list = new LinkedList<>();

		if (items == null || items.isEmpty()) {
			return list;
		}

		if (filters.isEmpty())
			list.addAll(items);
		else {
			for (T item : items) {
				boolean allow = true;
				synchronized (filters) {
					for (IFilter<T> filter : filters) {
						if (!filter.filter(item)) {
							allow = false;
							break;
						}
					}
				}

				if (allow) {
					list.add(item);
				}
			}
		}

		if (comparator != null)
			Collections.sort(list, comparator);

		return list;
	}
}
