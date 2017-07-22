package net.anfet.simple.support.library.inflation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * Created by Oleg on 13.02.2017.
 * <p>
 * ресивер для инфлейтера.
 * позволяет отсоединяться от цели и не вызывает метод обработчик
 */

public class DetachableBroadcastReceiver extends BroadcastReceiver {

	private final Method method;
	private final Object target;
	private ReceiverState state;

	public DetachableBroadcastReceiver(Method method, Object target) {
		this.method = method;
		this.target = target;
		this.state = ReceiverState.DETACHED;
	}

	public void attach() {
		state = ReceiverState.ATTACHED;
	}

	public void detach() {
		state = ReceiverState.DETACHED;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (state == ReceiverState.ATTACHED) {
			try {
				method.invoke(target, context, intent, this);
			} catch (IllegalAccessException | InvocationTargetException e) {
				FirebaseCrash.log(String.format(Locale.US, "Crash from: %s; intent: %s; data: %s", String.valueOf(target), intent.getAction(), new Gson().toJson(intent)));
				FirebaseCrash.report(e);
			}
		}
	}


	private enum ReceiverState {
		ATTACHED, DETACHED;
	}
}
