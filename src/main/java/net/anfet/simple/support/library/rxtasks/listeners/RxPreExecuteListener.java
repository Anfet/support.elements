package net.anfet.simple.support.library.rxtasks.listeners;

/**
 * Created by Oleg on 17.07.2017.
 */

public interface RxPreExecuteListener<In> {
	void onPreExecute(In in) throws InterruptedException;
}
