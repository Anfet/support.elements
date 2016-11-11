package net.anfet.simple.support.library.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.DimenRes;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Hashtable;

public class Fonts {

	public static final String OPEN_SANS = "opensans-regular";
	public static final String OPEN_SANS_LIGHT = "OpenSans-Light";
	public static final String ROBOTO_LIGHT = "roboto-light";
	public static final String OPEN_SANS_SEMIBOLD = "opensans-semibold";
	public static final String OPEN_SANS_BOLD = "opensans-bold";
	public static final String ROBOTO_BOLD = "Roboto-Bold";
	public static final String ROBOTO_ITALIC = "Roboto-Italic";
	public static final String ROBOTO_MEDIUM = "Roboto-Medium";
	public static final String ROBOTO_REGULAR = "Roboto-Regular";
	public static final String ROBOTO_THIN = "Roboto-Thin";

	public static final String OPEN_SANS_ITALIC = "opensans-italic";

	private static final Hashtable<String, Typeface> fonts = new Hashtable<String, Typeface>();

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
			return fonts.get(name);

		Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/" + name + ".ttf");
		fonts.put(name, font);
		return font;
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

	public static float getFontSizeFromDimension(Context context, @DimenRes int dimen) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		float density = dm.density;
		return context.getResources().getDimension(dimen) / density;
	}
}
