package net.anfet.simple.support.library.inflation;

import android.view.View;

/**
 * Сгруппированные вьюхи переключателей
 */

public class RadioViewGroup extends InflatableViewGroup {


	public void switchTo(View v) {
		v.setSelected(true);
		for (View view : views) {
			if (view == v)
				continue;

			view.setSelected(false);
		}
	}
}
