package net.anfet.simple.support.library.inflation;

import android.view.View;

/**
 * Created by Oleg on 12.07.2016.
 */
public interface IViewFinder {
	<X extends View> X findViewById(int id);
}
