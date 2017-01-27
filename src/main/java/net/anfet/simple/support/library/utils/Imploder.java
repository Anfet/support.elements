package net.anfet.simple.support.library.utils;

import net.anfet.simple.support.library.reflection.ReflectionSupport;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Imploder {

	private List<Object> items;
	private String separator;

	public Imploder(String separator) {
		this.items = new ArrayList<Object>();
		this.separator = separator;
	}

	public static String[] makeListOfValues(Collection<?> collection) {
		ArrayList list = new ArrayList(collection);
		String[] array = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i).toString();
		}

		return array;

	}

	public static String implode(Iterable<?> iterable, String separator) {
		String ret = "";
		for (Iterator<?> i = iterable.iterator(); i.hasNext(); ) {
			Object o = i.next();
			ret += o.toString() + (i.hasNext() ? separator : "");
		}

		return ret;
	}

	public static String implodeUsingField(Iterable<?> iterable, String separator, String field) {
		String ret = "";
		for (Iterator<?> i = iterable.iterator(); i.hasNext(); ) {
			Object o = i.next();
			try {
				Field f = ReflectionSupport.findField(o.getClass(), field, Object.class);
				if (f != null) {
					ret += String.valueOf(f.get(o)) + (i.hasNext() ? separator : "");
				} else {
					throw new RuntimeException("Invalid field " + field + " in class " + o.getClass().getName());
				}

			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	public static String implode(Object[] iterable, String separator) {
		if (iterable == null)
			return "";

		String ret = "";
		List<Object> objects = Arrays.asList(iterable);
		for (Iterator<?> i = objects.iterator(); i.hasNext(); ) {
			Object o = i.next();
			ret += o.toString() + (i.hasNext() ? separator : "");
		}

		return ret;
	}

	@Override
	public String toString() {
		return getString();
	}

	public Imploder add(Object item) {
		items.add(item);
		return this;
	}

	public String getString() {
		if (items.isEmpty())
			return "";

		return Imploder.implode(items, separator);
	}
}
