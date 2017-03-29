package net.anfet.simple.support.library.tasks;

import android.os.Handler;
import android.support.annotation.MainThread;
import android.util.Log;

import net.anfet.tasks.Runner;

import java.util.concurrent.CountDownLatch;

/**
 * Раннер предназначенный для выполнения задач и выдачу результатов в UI поток. Обязательно должен вызываться в UI потоке
 */
public abstract class UiRunner extends Runner {

	private final Handler handler;
	private volatile boolean ran = true;

	@MainThread
	public UiRunner(Object owner) {
		super(owner);
		handler = new Handler();
	}

	@Override
	protected void publishPreExecute() throws Exception {
		if (getState() == FORFEITED) return;

		final Exception[] exception = {null};
		final CountDownLatch latch = new CountDownLatch(1);
		handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					onPreExecute();
				} catch (Exception e) {
					exception[0] = e;
				}

				latch.countDown();
			}
		});

		if (exception[0] != null) throw exception[0];

		try {
			latch.await();
		} catch (InterruptedException ignored) {
			//it's ok if thread gets interrupted
		}
	}

	@Override
	protected void publishFinished() {
		if (getState() == FORFEITED) return;

		final CountDownLatch latch = new CountDownLatch(1);
		handler.post(new Runnable() {
			@Override
			public void run() {
				onFinished();
				latch.countDown();
			}
		});

		try {
			latch.await();
		} catch (InterruptedException ignored) {
			//it's ok if thread gets interrupted
		}
	}

	@Override
	protected void publishPostExecute() {
		if (getState() == FORFEITED) return;
		final CountDownLatch latch = new CountDownLatch(1);
		handler.post(new Runnable() {
			@Override
			public void run() {
				onPostExecute();
				latch.countDown();
			}
		});

		try {
			latch.await();
		} catch (InterruptedException ignored) {
			//it's ok if thread gets interrupted
		}
	}


	/**
	 * запускает обновление прогресса если оно еще не сделано
	 */
	protected final void schedulePublishProgress() {
		if (getState() == Runner.FORFEITED) return;

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

	/**
	 * запускает обновление прогресса и дожидается его выполнения
	 * @throws InterruptedException
	 */
	protected final void publishProgress() throws InterruptedException {
		if (getState() == FORFEITED) return;

		final CountDownLatch latch = new CountDownLatch(1);
		handler.post(new Runnable() {
			@Override
			public void run() {
				onPublishProgress();
				latch.countDown();
			}
		});

		latch.await();
	}

	@Override
	protected void publishError(final Throwable ex) {
		if (getState() == FORFEITED) return;
		Log.e(getClass().getName(), ex.getMessage(), ex);

		final CountDownLatch latch = new CountDownLatch(1);
		handler.post(new Runnable() {
			@Override
			public void run() {
				onError(ex);
				latch.countDown();
			}
		});

		try {
			latch.await();
		} catch (InterruptedException ignored) {
			//it's ok if thread gets interrupted
		}
	}

	@MainThread
	protected void onPublishProgress() {

	}
}
