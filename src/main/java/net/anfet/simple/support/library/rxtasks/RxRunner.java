package net.anfet.simple.support.library.rxtasks;

import net.anfet.simple.support.library.rxtasks.exceptions.DisposedException;
import net.anfet.simple.support.library.rxtasks.listeners.RxCompleteListener;
import net.anfet.simple.support.library.rxtasks.listeners.RxErrorListener;
import net.anfet.simple.support.library.rxtasks.listeners.RxExecutor;
import net.anfet.simple.support.library.rxtasks.listeners.RxPostExecuteListener;
import net.anfet.simple.support.library.rxtasks.listeners.RxPreExecuteListener;
import net.anfet.simple.support.library.rxtasks.listeners.RxProgressListener;

import java.util.concurrent.Callable;

import io.reactivex.disposables.Disposable;

/**
 * Created by Oleg on 17.07.2017.
 */

public class RxRunner<In, Out, Progress> implements Callable<Out>, Disposable {

	protected In in;
	protected Object owner;
	protected RxProgressListener<Progress> progressListener;
	protected RxPostExecuteListener<Out> postExecuteListener;
	protected RxCompleteListener completeListener;
	protected RxErrorListener errorListener;
	protected RxPreExecuteListener<In> preExecuteListener;
	protected RxExecutor<In, Out, Progress> executor;
	protected boolean disposed = false;


	protected Out publishExecute() throws Exception {
		return executor == null ? null : executor.doExecute(this, in);
	}


	public void publishError(Throwable throwable) {
		onError(throwable);
	}

	void onError(Throwable throwable) {
		if (errorListener != null) errorListener.onError(throwable);
	}

	public void publishPostExecute(Out out) {
		onPostExecute(out);
	}

	void onPostExecute(Out out) {
		if (postExecuteListener != null) postExecuteListener.onPostExecute(out);
	}

	public void publishComplete() {
		onComplete();
	}

	void onComplete() {
		if (completeListener != null) completeListener.onComplete(this);
	}

	public void publishPreExecute() throws InterruptedException {
		onPreExecute(in);
	}

	void onPreExecute(In in) throws InterruptedException {
		if (disposed) throw new DisposedException();
		if (preExecuteListener != null) preExecuteListener.onPreExecute(in);
	}

	public void publishProgress(Progress progress) {
		onProgress(progress);
	}

	void onProgress(Progress progress) {
		if (progressListener != null) progressListener.onProgress(progress);
	}

	public RxRunner<In, Out, Progress> onProgress(RxProgressListener<Progress> progressListener) {
		this.progressListener = progressListener;
		return this;
	}

	public RxRunner<In, Out, Progress> onPostExecute(RxPostExecuteListener<Out> postExecuteListener) {
		this.postExecuteListener = postExecuteListener;
		return this;
	}

	public RxRunner<In, Out, Progress> onComplete(RxCompleteListener listener) {
		this.completeListener = listener;
		return this;
	}

	public RxRunner<In, Out, Progress> onError(RxErrorListener listener) {
		this.errorListener = listener;
		return this;
	}

	public RxRunner<In, Out, Progress> onPreExecute(RxPreExecuteListener<In> listener) {
		this.preExecuteListener = listener;
		return this;
	}

	public RxRunner<In, Out, Progress> doExecute(RxExecutor<In, Out, Progress> executor) {
		this.executor = executor;
		return this;
	}

	public Disposable queue(Object owner, In... in) {
		this.in = (in == null || in.length == 0) ? null : in[0];
		this.owner = owner;
		RxTasks.enqueue(this, owner);
		return this;
	}

	public Object getOwner() {
		return owner;
	}

	@Override
	public Out call() throws Exception {
		publishPreExecute();
		Out out = publishExecute();
		publishPostExecute(out);
		return out;
	}

	@Override
	public void dispose() {
		RxTasks.abandon(this);
		disposed = true;
	}

	@Override
	public boolean isDisposed() {
		return disposed;
	}
}
