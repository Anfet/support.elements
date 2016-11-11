package net.anfet.simple.support.library;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.Toolbar;

import net.anfet.simple.support.library.anotations.WithToolbar;
import net.anfet.simple.support.library.exceptions.NoIdException;

/**
 * Активити с {@link Toolbar}
 */

public class ToolbarActivity extends SupportActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initToolbar((Toolbar) findViewById(getToolbarId()));
	}

	private
	@IdRes
	int getToolbarId() {
		if (getClass().isAnnotationPresent(WithToolbar.class))
			return getClass().getAnnotation(WithToolbar.class).id();

		throw new NoIdException();
	}
}
