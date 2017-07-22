package net.anfet.simple.support.library.rxtasks;

import net.anfet.MultiMap;
import net.anfet.support.Nullsafe;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Oleg on 16.06.2017.
 */
public final class RxTasks {

	private static final MultiMap<WeakReference<Object>, RxRunner> tasks = new MultiMap<>();

	private synchronized static WeakReference<Object> findRef(Object owner) {
		Iterator<WeakReference<Object>> keys = tasks.keys().iterator();
		while (keys.hasNext()) {
			WeakReference<Object> ref = keys.next();
			if (ref.get() == null) {
				keys.remove();
				continue;
			}

			if (owner.equals(ref.get())) {
				return ref;
			}
		}

		return null;
	}

	static <In, Out, Progress> void enqueue(final RxRunner<In, Out, Progress> runner, Object owner) {
		WeakReference<Object> ref = Nullsafe.get(findRef(owner), new WeakReference<>(owner));
		tasks.add(ref, (RxRunner) runner);

		Observable.fromCallable(runner)
				.subscribeOn(Schedulers.io())
				.doOnTerminate(() -> {
					abandon(runner);
					runner.publishComplete();
				})
				.subscribe(out -> {

				}, runner::publishError);
	}

	static void abandon(RxRunner runner) {
		WeakReference<Object> ref = findRef(runner.getOwner());
		if (ref != null) tasks.remove(ref, runner);
		runner.disposed = true;
	}

	static boolean isAbandoned(RxRunner runner) {
		return runner.disposed;
	}

	public static void abandonAllFor(Object owner) {
		WeakReference<Object> ref = findRef(owner);

		if (ref != null) {
			List<RxRunner> runners = tasks.get(ref);
			for (RxRunner runner : runners) {
				runner.disposed = true;
			}

			tasks.remove(ref);
		}
	}
}
