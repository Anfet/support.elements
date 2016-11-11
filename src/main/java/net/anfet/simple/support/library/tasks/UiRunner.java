package net.anfet.simple.support.library.tasks;

import android.os.Handler;
import android.support.annotation.MainThread;

import net.anfet.tasks.Runner;

/**
 * Раннер предназначенный для выполнения задач и выдачу результатов в UI поток. Обязательно должен вызываться в UI потоке
 */
public abstract class UiRunner extends Runner {

	private final Handler handler;
	private final Object publishLock = new Object();
	private volatile boolean ran = true;

	@MainThread
	public UiRunner(Object owner) {
		super(owner);
		handler = new Handler();
	}

	@Override
	protected void publishFinished() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				onPublishFinished();
			}
		});
	}

	/**
	 * запускает обновление прогресса если оно еще не сделано
	 */
	public final void schedulePublishProgress() {
		synchronized (this) {
			if (isRuninng()) {
				if (ran) {
					ran = false;
					handler.post(new Runnable() {
						@Override
						public void run() {
							onPublishProgress();
							ran = true;
						}
					});
				}
			}
		}
	}

	/**
	 * запускает обновление прогресса и дожидается его выполнения
	 * @throws InterruptedException
	 */
	protected final void publishProgress() throws InterruptedException {
		synchronized (this) {
			if (isRuninng()) {
				synchronized (publishLock) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							onPublishProgress();
							synchronized (publishLock) {
								publishLock.notifyAll();
							}
						}
					});

					publishLock.wait();
				}
			}
		}
	}

	@Override
	protected void publishError(final Throwable ex) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				onError(ex);
			}
		});
	}

	@MainThread
	public void onPublishProgress() {

	}
}
