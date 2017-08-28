package net.anfet.simple.support.library.recycler.view.support;

/**
 * Created by Oleg on 13.08.2017.
 */

public interface IStableIdSupply<T> {
	long getStableId(T item);
}
