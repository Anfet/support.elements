package net.anfet.simple.support.library.inflation;

import android.view.View;

import java.util.LinkedList;
import java.util.List;

/**
 * Группа элементов
 */

public class InflatableViewGroup {
	protected final List<View> views;

	public InflatableViewGroup() {
		views = new LinkedList<>();
	}

	public void addView(View view) {
		if (view != null) {
			synchronized (views) {
				views.add(view);
			}
		}
	}

	public void setVisibility(int visibility) {
		for (View view : views) {
			view.setVisibility(visibility);
		}
	}

	public void removeAllViews() {
		synchronized (views) {
			views.clear();
		}
	}

	public boolean contains(View view) {
		synchronized (views) {
			return views.contains(view);
		}
	}
}
