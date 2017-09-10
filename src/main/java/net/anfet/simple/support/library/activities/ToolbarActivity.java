package net.anfet.simple.support.library.activities;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;

import net.anfet.simple.support.library.anotations.layout.ToolbarId;
import net.anfet.simple.support.library.exceptions.NoIdException;
import net.anfet.simple.support.library.presenters.Presenter;

/**
 * Активити с {@link Toolbar}
 * для работы либо оверрайднуть {@link #getToolbarId()} либо проставить {@link ToolbarId} нотацию
 */

public class ToolbarActivity<T extends ViewDataBinding, Z extends Presenter> extends SupportActivity<T, Z> {

	protected Toolbar mToolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mToolbar = findViewById(getToolbarId());
		initToolbar(mToolbar);
	}

	public void initToolbar(@NonNull Toolbar toolbar) {
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setTitle("");
	}


	private
	@IdRes
	int getToolbarId() {
		if (getClass().isAnnotationPresent(ToolbarId.class))
			return getClass().getAnnotation(ToolbarId.class).value();

		throw new NoIdException();
	}

	@NonNull
	public Toolbar getToolbar() {
		return mToolbar;
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		getToolbar().setTitle(title);
	}

	@Override
	public void setTitle(int titleId) {
		super.setTitle(titleId);
		getToolbar().setTitle(titleId);
	}
}
