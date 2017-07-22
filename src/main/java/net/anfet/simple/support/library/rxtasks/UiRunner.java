package net.anfet.simple.support.library.rxtasks;

import android.util.Log;

import net.anfet.simple.support.library.rxtasks.exceptions.InterruptedBeforeExecturion;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Oleg on 17.07.2017.
 */

public class UiRunner<In, Out, Progress> extends RxRunner<In, Out, Progress> {


	@Override
	public void publishPreExecute() throws InterruptedBeforeExecturion {
		Observable.just(this)
				.observeOn(AndroidSchedulers.mainThread())
				.doOnNext(inOutProgressUiRunner -> onPreExecute(in))
				.blockingSubscribe();
	}

	@Override
	public void publishComplete() {
		Observable.just(this)
				.observeOn(AndroidSchedulers.mainThread())
				.doOnNext(inOutProgressUiRunner -> onComplete())
				.blockingSubscribe();
	}

	@Override
	public void publishError(Throwable throwable) {
		Observable.just(this)
				.observeOn(AndroidSchedulers.mainThread())
				.doOnError(throwable1 -> Log.i("WOW", "NEW ERROR", throwable1))
				.doOnNext(inOutProgressUiRunner -> onError(throwable))
				.blockingSubscribe();
	}

	@Override
	public void publishPostExecute(Out out) {
		Observable.just(this)
				.observeOn(AndroidSchedulers.mainThread())
				.doOnNext(inOutProgressUiRunner -> onPostExecute(out))
				.blockingSubscribe();
	}

	@Override
	public void publishProgress(Progress progress) {
		Observable.just(this)
				.observeOn(AndroidSchedulers.mainThread())
				.doOnNext(inOutProgressUiRunner -> onProgress(progress))
				.blockingSubscribe();
	}
}
