package net.anfet.simple.support.library.reflection;

import net.anfet.MultiMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Класс для поддержания рефлексии
 */
public final class ReflectionSupport {


	private static final MultiMap<Class, Field> fields = new MultiMap<>();
	private static final MultiMap<Class, Method> methods = new MultiMap<>();


	/**
	 * Возвращает все поля класса, включая приватные и родительские по цепочке
	 * @param type - требуемый тип
	 * @return список полей
	 */

	public static synchronized final List<Field> getFields(Class<?> type, Class lowestSuperClass) {
		if (ReflectionSupport.fields.contains(type)) {
			return ReflectionSupport.fields.get(type);
		}

		List<Field> fields = new LinkedList<Field>();
		Field[] a = type.getDeclaredFields();

		for (Field f : a) {
			if ((f.getModifiers() & Modifier.FINAL) == 0) {
				f.setAccessible(true);
				fields.add(f);
			}
		}

		if (type.getSuperclass() != lowestSuperClass && type.getSuperclass() != null)
			fields.addAll(getFields(type.getSuperclass(), lowestSuperClass));

		ReflectionSupport.fields.set(type, fields);
		return fields;
	}


	/**
	 * Возвращает все методы класса включаю приватные и родительские
	 * @param type - требуемый тип
	 * @return список методов
	 */
	public static synchronized final List<Method> getMethods(Class<?> type, Class lowestSuperClass) {
		if (ReflectionSupport.methods.contains(type))
			return ReflectionSupport.methods.get(type);

		List<Method> methods = new ArrayList<Method>();
		Method[] a = type.getDeclaredMethods();
		for (Method m : a) {
			m.setAccessible(true);
			methods.add(m);
		}

		if (type.getSuperclass() != null && type.getSuperclass() != null)
			methods.addAll(getMethods(type.getSuperclass(), lowestSuperClass));

		ReflectionSupport.methods.set(type, methods);
		return methods;
	}

	public static synchronized Field findField(Class type, String name, Class lowestSuperclass) {

		Field f = null;
		try {
			f = type.getDeclaredField(name);
			f.setAccessible(true);
		} catch (NoSuchFieldException e) {
			if (!type.equals(lowestSuperclass)) {
				f = findField(type.getSuperclass(), name, lowestSuperclass);
			}
		}

		return f;
	}

}
