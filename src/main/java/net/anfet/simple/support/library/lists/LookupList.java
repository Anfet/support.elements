package net.anfet.simple.support.library.lists;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import junit.framework.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Отсортированный список объектов объектов забэкованный с помощью списка и карты, позволяющий быстро искать элементы как по интексу, так и по ключу
 * {@see Keys}
 */
@Deprecated
public class LookupList<T extends Key> implements Collection<T> {

	private List<T> list = new LinkedList<>();
	private Map<Object, T> map = new HashMap<>();
	private Comparator<T> comparator = null;
	private volatile boolean requireSort = false;

	public LookupList(Collection<T> list) {
		addAll(list);
	}

	public LookupList() {

	}


	public static <A extends Object> LookupList<WrappedKey<A>> wrap(Collection<A> collection, final KeyWrapper<A> wrapper) {
		LookupList<WrappedKey<A>> keys = new LookupList<>();

		for (A element : collection) {
			keys.add(new WrappedKey<A>(element) {
				@Override
				public Long getId() {
					return (Long) wrapper.getKey(getValue());
				}
			});
		}

		return keys;
	}

	public LookupList<T> distinct() {
		return new LookupList<>(new LinkedList<>(map.values()));
	}


	public LookupList<T> setComparator(Comparator<T> comparator) {
		this.comparator = comparator;
		this.requireSort = !isEmpty();
		return this;
	}

	@Override
	public boolean add(@NonNull T object) {
		Assert.assertNotNull(object);

		synchronized (this) {
			T old = map.put(object.getId(), object);
			if (old != null) {
				list.remove(old);
			}

			list.add(object);
			return (requireSort = comparator != null);
		}
	}


	@Override
	public boolean addAll(@Nullable Collection<? extends T> collection) {
		if (collection == null)
			return false;

		for (T element : collection) {
			add(element);
		}
		return true;
	}

	@Override
	public void clear() {
		synchronized (this) {
			map.clear();
			list.clear();
		}
	}

	@Override
	public boolean contains(Object object) {
		synchronized (this) {
			return list.contains(object);
		}
	}

	@Override
	public boolean containsAll(@Nullable Collection<?> collection) {
		if (collection == null) {
			return false;
		}

		synchronized (this) {
			return list.containsAll(collection);
		}
	}

	@Override
	public boolean isEmpty() {
		synchronized (this) {
			return list.isEmpty();
		}
	}

	@NonNull
	@Override
	public Iterator<T> iterator() {
		items();
		return new Iterator<T>() {

			private Iterator<T> iterator = list.iterator();
			private T nextElement;

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public T next() {
				return nextElement = iterator.next();
			}

			@Override
			public void remove() {
				iterator.remove();
				map.remove(nextElement.getId());
			}
		};
	}

	@Override
	public boolean remove(Object object) {
		if (object == null)
			return false;

		if (!(object instanceof Key))
			return false;

		synchronized (this) {
			T element = map.remove(((Key) object).getId());
			return list.remove(element);
		}
	}

	@Override
	public boolean removeAll(@Nullable Collection<?> collection) {
		if (collection == null)
			return false;

		synchronized (this) {
			boolean removed = false;
			for (Object element : collection)
				removed |= remove(element);
			return removed;
		}
	}

	@Override
	public boolean retainAll(@NonNull Collection<?> collection) {
		Assert.assertNotNull(collection);

		synchronized (this) {
			list.retainAll(collection);

			map.clear();
			if (!list.isEmpty()) {
				for (T object : list) {
					map.put(object.getId(), object);
				}

				requireSort = comparator != null;
			}
		}

		return requireSort;
	}

	@Override
	public int size() {
		synchronized (this) {
			return list.size();
		}
	}

	public Collection<T> items() {
		if (requireSort && comparator != null) {
			synchronized (this) {
				Collections.sort(list, comparator);
			}
			requireSort = false;
		}

		return list;
	}

	@NonNull
	@Override
	public Object[] toArray() {
		synchronized (this) {
			return items().toArray();
		}
	}

	@NonNull
	@Override
	public <T1> T1[] toArray(@NonNull T1[] array) {
		Assert.assertNotNull(array);

		synchronized (this) {
			return items().toArray(array);
		}
	}

	public T get(int index) {
		items();
		synchronized (this) {
			return list.get(index);
		}
	}

	public T lookup(@Nullable Object key) {
		if (key == null)
			return null;

		synchronized (this) {
			return map.get(key);
		}
	}

	@NonNull
	public LookupList<T> filter(@NonNull IFilter<T> filter) {
		Assert.assertNotNull(filter);

		LookupList<T> list = new LookupList<>();
		list.setComparator(comparator);
		synchronized (this) {
			for (T element : this) {
				if (filter.onFilter(element))
					list.add(element);
			}
		}
		return list;
	}

	public LookupList<T> copy() {
		return new LookupList<T>(this.list).setComparator(comparator);
	}

	@NonNull
	public List<T> list() {
		return new LinkedList<>(items());
	}

	@NonNull
	public List<Object> ids() {
		List<Object> ids = new LinkedList<>();
		synchronized (this) {
			for (T element : this) {
				ids.add(element.getId());
			}
		}
		return ids;
	}

	@NonNull
	public String[] names() {
		String[] names = new String[size()];
		int i = 0;
		synchronized (this) {
			for (T obj : this) {
				names[i++] = obj.toString();
			}
		}

		return names;
	}

	@NonNull
	public T first() throws NoElementException {
		if (isEmpty())
			throw new NoElementException();

		return get(0);
	}

	@NonNull
	public T last() throws NoElementException {
		if (isEmpty())
			throw new NoElementException();

		return get(size() - 1);
	}

	public int indexOf(@Nullable T element) {
		if (element == null) {
			return -1;
		}

		int i = 0;
		synchronized (this) {
			for (T item : this) {
				if (item.getId().equals(element.getId()))
					return i;
				i++;
			}
		}
		return -1;
	}

	public void removeByKey(@Nullable Object key) {
		if (key != null) {
			synchronized (this) {
				T element = map.remove(key);
				if (element != null) {
					list.remove(element);
				}
			}
		}
	}
}
