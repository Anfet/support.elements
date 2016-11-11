package net.anfet.simple.support.library.inflation;

import android.view.View;

/**
 * Created by Oleg on 12.07.2016.
 * Враппер для любых типов
 */
public interface IRootWrapper {
	<T extends View> T findViewById(int id);

	View getRoot();
}
