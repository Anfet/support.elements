package net.anfet.simple.support.library;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

/**
 * Класс поддержки для {@link Snackbar}. Поскольку абсолютно неясно как менять цвет текста на нем, то приходится изощряться вот таким способом
 */

public final class SupportSnackbar {

	private Snackbar snackbar;

	public static SupportSnackbar make(View root, String text, int duration) {
		SupportSnackbar snackbar = new SupportSnackbar();
		snackbar.snackbar = Snackbar.make(root, text, duration);
		return snackbar;
	}

	public static SupportSnackbar make(View root, String text, int duration, int textColor) {
		SupportSnackbar snackbar = new SupportSnackbar();
		snackbar.snackbar = Snackbar.make(root, text, duration);
		snackbar.setTextColor(textColor);
		return snackbar;
	}

	public static SupportSnackbar make(View root, String text, int duration, int textColor, int actionColor) {
		SupportSnackbar snackbar = new SupportSnackbar();
		snackbar.snackbar = Snackbar.make(root, text, duration);
		snackbar.setTextColor(textColor);
		snackbar.setActionColor(actionColor);
		return snackbar;
	}

	public static SupportSnackbar make(View root, String text, int duration, int textColor, int actionColor, int background) {
		SupportSnackbar snackbar = new SupportSnackbar();
		snackbar.snackbar = Snackbar.make(root, text, duration);
		snackbar.setTextColor(textColor);
		snackbar.setActionColor(actionColor);
		snackbar.getSnackbar().getView().setBackgroundResource(background);
		return snackbar;
	}

	private SupportSnackbar setActionColor(int actionColor) {
		View view = snackbar.getView();
		TextView tv = view.findViewById(R.id.snackbar_action);
		tv.setTextColor(actionColor);
		return this;
	}

	public Snackbar getSnackbar() {
		return snackbar;
	}

	public SupportSnackbar setTextColor(int color) {
		View view = snackbar.getView();
		TextView tv = view.findViewById(R.id.snackbar_text);
		tv.setTextColor(color);
		return this;
	}
}
