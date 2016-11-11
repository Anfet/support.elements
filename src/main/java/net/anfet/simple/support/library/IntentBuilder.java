package net.anfet.simple.support.library;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Билдер для интентов
 */

public class IntentBuilder {

	private final Intent intent;

	public IntentBuilder(Context context, Class<?> type) {
		intent = new Intent(context, type);
	}

	public IntentBuilder(String action) {
		intent = new Intent(action);
	}

	public IntentBuilder putExtra(String name, Serializable extra) {
		intent.putExtra(name, extra);
		return this;
	}

	public IntentBuilder putExtra(String name, String extra) {
		intent.putExtra(name, extra);
		return this;
	}

	public IntentBuilder putExtra(String name, Integer extra) {
		intent.putExtra(name, extra);
		return this;
	}

	public IntentBuilder putExtra(String name, Long extra) {
		intent.putExtra(name, extra);
		return this;
	}

	public Intent build() {
		return intent;
	}

	public IntentBuilder putExtra(String name, Parcelable extra) {
		intent.putExtra(name, extra);
		return this;
	}

	public IntentBuilder putExtra(String name, boolean extra) {
		intent.putExtra(name, extra);
		return this;
	}
}


