package net.anfet.simple.support.library.wrappers;

import android.view.View;
import android.widget.AdapterView;

/**
 * Created by Oleg on 22.07.2016.
 */
public class WrapperSelectedListener<T extends Object> implements AdapterView.OnItemSelectedListener {


	public void onItemClick(T item) {

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		onItemClick((T) parent.getItemAtPosition(position));
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
}
