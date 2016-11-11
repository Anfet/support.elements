package net.anfet.simple.support.library.wrappers;

import android.view.View;
import android.widget.AdapterView;

/**
 * Created by Oleg on 22.07.2016.
 */
public abstract class WrapperAdapterClickListener<T extends Object> implements AdapterView.OnItemClickListener {
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		onItemClick((T) parent.getItemAtPosition(position));
	}

	public abstract void onItemClick(T item);
}
