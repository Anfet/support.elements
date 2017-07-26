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

	public void resumed() {

	}

	public void stopped() {
		dispose();
	}

	public T getControl() {
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

	public void registerDisposable(Disposable disposable) {
		if (this.disposable == null || this.disposable.isDisposed()) this.disposable = new CompositeDisposable();
		this.disposable.add(disposable);
	}

}
