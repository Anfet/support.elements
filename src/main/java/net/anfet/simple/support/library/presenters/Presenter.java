package net.anfet.simple.support.library.presenters;

import java.lang.ref.SoftReference;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Oleg on 21.07.2017.
 *
 * базовый презентер
 */

public abstract class Presenter<T extends IPresentable> implements Disposable {

	protected SoftReference<T> controlReference;
	protected CompositeDisposable disposable;

	public Presenter() {

	}

	public Presenter<T> setControl(T control) {
		this.controlReference = new SoftReference<T>(control);
		return this;
	}

	public void created() {

	}

	public CompositeDisposable getDisposable() {
		if (this.disposable == null || this.disposable.isDisposed()) this.disposable = new CompositeDisposable();
		return disposable;
	}

	public void resumed() {

	}

	public void paused() {
		dispose();
	}

	public T getView() {
		if (controlReference == null && isDisposed()) {
			throw new EDisposed("Presenter is already disposed; Did you register disposable?");
		}

		if (controlReference != null && controlReference.get() == null) {
			throw new NullPointerException("Control not set for presenter. Weird");
		}

		return controlReference.get();
	}

	@Override
	public void dispose() {
		if (disposable != null) disposable.dispose();
	}

	@Override
	public boolean isDisposed() {
		return disposable != null && disposable.isDisposed();
	}

	public void synchToLifecycle(Disposable disposable) {
		getDisposable().add(disposable);
	}

	public void destroyed() {
		controlReference = null;
	}
}
