package net.anfet.simple.support.library.utils;

import android.util.DisplayMetrics;

/**
 * Created by Oleg on 28.02.2017.
 * <p>
 * Конвертер
 */

public final class Convert {

	private static int pixel2dp(DisplayMetrics dm, int pixels) {
		int dpi = dm.densityDpi;
		return (int) (pixels * 160f / dpi);
	}

	private static int dp2pixels(DisplayMetrics dm, int dp) {
		int dpi = dm.densityDpi;
		return (int) (dp * (dpi / 160f));
	}
}
