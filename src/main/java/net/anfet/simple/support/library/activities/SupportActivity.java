package net.anfet.simple.support.library.activities;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import net.anfet.simple.support.library.anotations.ActivityTransitition;
import net.anfet.simple.support.library.anotations.Title;
import net.anfet.simple.support.library.anotations.fonts.UseFont;
import net.anfet.simple.support.library.anotations.layout.LayoutId;
import net.anfet.simple.support.library.anotations.layout.MenuId;
import net.anfet.simple.support.library.anotations.layout.RootId;
import net.anfet.simple.support.library.exceptions.NoIdException;
import net.anfet.simple.support.library.exceptions.NoLayoutException;
import net.anfet.simple.support.library.inflation.DetachableBroadcastReceiver;
import net.anfet.simple.support.library.presenters.IPresentable;
import net.anfet.simple.support.library.presenters.Presenter;
import net.anfet.simple.support.library.presenters.PresenterSupport;
import net.anfet.simple.support.library.utils.Fonts;
import net.anfet.simple.support.library.utils.IBackpressPropagator;
import net.anfet.simple.support.library.utils.IFonted;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Поддерживающая форма, с рут элементом и уже проинжектенными вьюшками
 */
public abstract  class SupportActivity<T extends ViewDataBinding, Z extends Presenter> extends AppCompatActivity implements IFonted, IPresentable<Z> {

	protected View mRoot = null;
	protected Collection<DetachableBroadcastReceiver> mRegisteredReceivers;
	protected final List<IBackpressPropagator> mBackPressPropagators = new LinkedList<>();
	protected T mDataBinding;
	protected Z mPresenter;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuId menuAnnotation = getClass().getAnnotation(MenuId.class);
		if (menuAnnotation != null) {
			getMenuInflater().inflate(menuAnnotation.value(), menu);
		}

		return super.onCreateOptionsMenu(menu);
	}

	public Z getPresenter() {
		return mPresenter;
	}

	@Override
	public void onBackPressed() {
		if (canExecuteBackButton()) {
			ActivityTransitition activityTransitition = getClass().getAnnotation(ActivityTransitition.class);
			if (activityTransitition != null) {
				overridePendingTransition(activityTransitition.in(), activityTransitition.out());
			}

			super.onBackPressed();
		}
	}

	public int getRootId() {
		throw new NoIdException("No root value specified");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActivityTransitition activityTransitition = getClass().getAnnotation(ActivityTransitition.class);
		if (activityTransitition != null) {
			overridePendingTransition(activityTransitition.in(), activityTransitition.out());
		}


		mDataBinding = DataBindingUtil.setContentView(this, getLayoutId());

		if (getClass().isAnnotationPresent(RootId.class)) {
			mRoot = findViewById(getClass().getAnnotation(RootId.class).value());
		} else {
			mRoot = findViewById(getRootId());
		}

		Fonts.setFont(getRoot(), getFont());
//		InflateHelper.injectViewsAndFragments(this, getRoot(), SupportActivity.class);
		mPresenter = PresenterSupport.create(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Title titleNotation = getClass().getAnnotation(Title.class);
		if (titleNotation != null) setTitle(titleNotation.value());

		if (mPresenter != null) mPresenter.created();
	}

	public T getDataBinding() {
		return mDataBinding;
	}

	@Override
	public String getFont() {
		UseFont font = getClass().getAnnotation(UseFont.class);
		if (font != null)
			return font.value();

		return Fonts.ROBOTO_REGULAR;
	}

	public int getFragmentContentId() {
		throw new NoIdException();
	}

	public boolean hasFragmentContent() {
		try {
			return getFragmentContentId() != 0;
		} catch (NoIdException e) {
			return false;
		}
	}

	public void addFragment(SupportFragment fragment, boolean animate, boolean addToBackStack) {
		if (!hasFragmentContent())
			throw new RuntimeException("Fragment container not initialized = 0");

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if (animate)
			transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

		if (addToBackStack)
			transaction.addToBackStack(fragment.getClass().getName());
		transaction.add(getFragmentContentId(), fragment).commit();
	}

	public void setFragment(Fragment fragment, boolean animate) {
		if (!hasFragmentContent())
			throw new RuntimeException("Fragment container not initialized = 0; Use getFragmentContentId() to set it");

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if (animate)
			transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

		transaction.replace(getFragmentContentId(), fragment, fragment.getClass().getName()).commit();
		supportInvalidateOptionsMenu();
	}

	public int getLayoutId() {
		LayoutId annotation = getClass().getAnnotation(LayoutId.class);
		if (annotation != null && annotation.value() > 0)
			return annotation.value();

		throw new NoLayoutException("No root specified for " + getClass().getSimpleName());

	}

	@Override
	protected void onResume() {
		super.onResume();
//		mRegisteredReceivers = InflateHelper.registerLocalReceivers(this, this, getClass());
		if (mPresenter != null) mPresenter.resumed();
	}

	@Override
	protected void onPause() {
		if (mPresenter != null) mPresenter.paused();
//		InflateHelper.detachReceivers(this, mRegisteredReceivers);
//		RxTasks.abandonAllFor(this);
//		mRegisteredReceivers = null;
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (mPresenter != null) mPresenter.destroyed();
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public final View getRoot() {
		return mRoot;
	}


	/**
	 * функция может быть вызвана чтобы понять нужно ли выполнять действие назад
	 * @return true, если все пропагаторы сказали ок - false, если кто-то сказал нет. пропагаторы сами ответственны за обработку действий
	 */
	public boolean canExecuteBackButton() {
		synchronized (mBackPressPropagators) {
			for (IBackpressPropagator propagator : mBackPressPropagators) {
				if (!propagator.shouldGoBack()) {
					return false;
				}
			}
		}

		return true;
	}

	public void addBackpressPropagator(IBackpressPropagator propagator) {
		synchronized (mBackPressPropagators) {
			mBackPressPropagators.add(propagator);
		}
	}

	public void removeBackpressPropagator(IBackpressPropagator propagator) {
		synchronized (mBackPressPropagators) {
			mBackPressPropagators.remove(propagator);
		}
	}

	@Override
	public void configurePresenter(Z presenter) {

	}
}
