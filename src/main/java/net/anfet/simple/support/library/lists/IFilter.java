package net.anfet.simple.support.library.lists;

/**
 * Интерфейс простой фильтрации любых объектов
 */
public interface IFilter<T> {
	/**
	 * Функция фильтратора
	 * @param t входящий элемент
	 * @return true если объект прошел фильтрацию
	 */
	boolean onFilter(T t);
}
