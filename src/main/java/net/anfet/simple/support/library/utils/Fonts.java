package net.anfet.simple.support.library.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.SoftReference;
import java.util.Hashtable;

public class Fonts {

	public static final String ROBOTO_LIGHT = "Roboto-Light";
	public static final String ROBOTO_BOLD = "Roboto-Bold";
	public static final String ROBOTO_ITALIC = "Roboto-Italic";
	public static final String ROBOTO_MEDIUM = "Roboto-Medium";
	public static final String ROBOTO_REGULAR = "Roboto-Regular";
	public static final String ROBOTO_THIN = "Roboto-Thin";

	private static final Hashtable<String, SoftReference<Typeface>> fonts = new Hashtable<>();

	public static void setFont(View view, String font) {
		if (view == null)
			return;

		Typeface tf = getFont(view.getContext(), font);

		if (view instanceof TextView) {
			((TextView) view).setTypeface(tf);
		} else if (view instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) view;

			for (int i = 0; i < vg.getChildCount(); i++) {
				View child = vg.getChildAt(i);
				setFont(child, font);
			}
		}
	}

	public static Typeface getFont(Context context, String name) {
		if (fonts.containsKey(name))
			return fonts.get(name).get();

		Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/" + name + ".ttf");
		fonts.put(name, new SoftReference<>(font));
		return font;
	}

	/**
	 * устанавливает во все текстовые поля указанный цвет
	 * @param context
	 * @param view
	 * @param plainTextColor
	 */
	public static void setTextColor(Context context, @Nullable View view, @ColorRes int plainTextColor) {
		if (view == null)
			return;

		if (view instanceof TextView) {
			((TextView) view).setTextColor(context.getResources().getColor(plainTextColor));
		} else if (view instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) view;

			for (int i = 0; i < vg.getChildCount(); i++) {
				View child = vg.getChildAt(i);
				setTextColor(context, child, plainTextColor);
			}
		}
	}


	public static void underline(View view) {
		if (view instanceof TextView) {
			TextView textView = (TextView) view;
			String label = textView.getText().toString();
			SpannableString spannableString = new SpannableString(label);
			spannableString.setSpan(new UnderlineSpan(), 0, label.length(), 0);
			textView.setText(spannableString);
		}
	}
}
