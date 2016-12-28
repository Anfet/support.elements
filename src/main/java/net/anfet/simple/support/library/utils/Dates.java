package net.anfet.simple.support.library.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Oleg on 22.07.2016.
 */
public final class Dates {
	public static final long MSEC_IN_SEC = 1000;
	public static final long MSEC_IN_MIN = MSEC_IN_SEC * 60;
	public static final long MSEC_IN_HOUR = MSEC_IN_MIN * 60;
	public static final long MSEC_IN_DAY = MSEC_IN_HOUR * 24;

	public static final SimpleDateFormat yyyyMMdd_HHmmss = new SimpleDateFormat("yyyy.MM.dd_HHmmss", Locale.getDefault());
	public static final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
	public static final SimpleDateFormat ddMMM = new SimpleDateFormat("dd MMM", Locale.getDefault());
	public static final SimpleDateFormat MMMyyyy = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
	public static final SimpleDateFormat MMMMMyyyy = new SimpleDateFormat("MMMMM yyyy", Locale.getDefault());
	public static final SimpleDateFormat HHmm = new SimpleDateFormat("HH:mm", Locale.getDefault());
	public static final SimpleDateFormat EEEddMMM = new SimpleDateFormat("EEE dd MMM", Locale.getDefault());
	public static final SimpleDateFormat MMMddEEEHHMM = new SimpleDateFormat("EEE dd MMM HH:mm", Locale.getDefault());
	public static final SimpleDateFormat HHmmss = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
	public static final SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	public static final SimpleDateFormat ddMMMMHHmm = new SimpleDateFormat("dd MMMM HH:mm", Locale.getDefault());
	public static final SimpleDateFormat ddEEEHHmm = new SimpleDateFormat("dd EEE HH:mm", Locale.getDefault());
	public static final SimpleDateFormat DDMMYYYY = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
	public static final SimpleDateFormat ddMMMMyyyy = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
	public static final SimpleDateFormat ddMMMyyyy = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());


	public static List<Date> makeList(Date from, Date till) {
		Calendar cycle = Calendar.getInstance();
		cycle.setTime(from);
		cycle.set(Calendar.HOUR_OF_DAY, 0);
		cycle.set(Calendar.MINUTE, 0);
		cycle.set(Calendar.SECOND, 0);
		cycle.set(Calendar.MILLISECOND, 0);

		List<Date> dates = new LinkedList<>();
		do {
			dates.add(cycle.getTime());
			cycle.add(Calendar.DAY_OF_MONTH, 1);
		} while (!cycle.getTime().after(till));

		return dates;
	}

	public static Date truncateDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());

		int h = calendar.get(Calendar.HOUR_OF_DAY);
		int m = calendar.get(Calendar.MINUTE);
		int s = calendar.get(Calendar.SECOND);
		int ms = calendar.get(Calendar.MILLISECOND);
		calendar.setTimeInMillis(0);
		calendar.set(Calendar.HOUR_OF_DAY, h);
		calendar.set(Calendar.MINUTE, m);
		calendar.set(Calendar.SECOND, s);
		calendar.set(Calendar.MILLISECOND, ms);
		return calendar.getTime();
	}

	public static Date truncateTime(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());

		int y = calendar.get(Calendar.YEAR);
		int m = calendar.get(Calendar.MONTH);
		int d = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.setTimeInMillis(0);
		calendar.set(Calendar.YEAR, y);
		calendar.set(Calendar.MONTH, m);
		calendar.set(Calendar.DAY_OF_MONTH, d);
		return calendar.getTime();
	}

	public static boolean datesEqual(Date a, Date b) {
		return truncateTime(a).equals(truncateTime(b));
	}

	public static String makeMinSecString(long msec) {
		long h = msec / MSEC_IN_HOUR;
		msec -= h * MSEC_IN_HOUR;
		long min = msec / MSEC_IN_MIN;
		msec -= min * MSEC_IN_MIN;
		long sec = msec / MSEC_IN_SEC;
		msec -= sec * MSEC_IN_SEC;
		return String.format(Locale.US, "%02d:%02d", min, sec);
	}

	public static String makeHmsString(long msec) {
		long h = msec / MSEC_IN_HOUR;
		msec -= h * MSEC_IN_HOUR;
		long min = msec / MSEC_IN_MIN;
		msec -= min * MSEC_IN_MIN;
		long sec = msec / MSEC_IN_SEC;
		msec -= sec * MSEC_IN_SEC;
		return String.format(Locale.US, "%02d:%02d:%02d", h, min, sec);
	}

	public static String makeHMString(long msec) {
		long h = msec / MSEC_IN_HOUR;
		msec -= h * MSEC_IN_HOUR;
		long min = msec / MSEC_IN_MIN;
		msec -= min * MSEC_IN_MIN;
		long sec = msec / MSEC_IN_SEC;
		msec -= sec * MSEC_IN_SEC;
		return String.format(Locale.US, "%02d:%02d", h, min);
	}

	public static boolean isToday(Date date) {
		return truncateTime(new Date()).equals(truncateTime(date));
	}

	public static boolean isYesterday(Date date) {
		Calendar a = Calendar.getInstance();
		a.setTime(truncateTime(a.getTime()));
		Calendar b = Calendar.getInstance();
		b.setTime(truncateTime(date));
		a.add(Calendar.DAY_OF_MONTH, -1);
		return a.getTime().equals(b.getTime());
	}
}
