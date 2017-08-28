package net.anfet.simple.support.library;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import net.anfet.simple.support.library.anotations.ActivityTransitition;
import net.anfet.simple.support.library.anotations.Font;
import net.anfet.simple.support.library.anotations.Layout;
import net.anfet.simple.support.library.anotations.Root;
import net.anfet.simple.support.library.exceptions.NoIdException;
import net.anfet.simple.support.library.exceptions.NoLayoutException;
import net.anfet.simple.support.library.inflation.DetachableBroadcastReceiver;
import net.anfet.simple.support.library.inflation.InflateHelper;
import net.anfet.simple.support.library.utils.Fonts;
import net.anfet.simple.support.library.utils.IBackpressPropagator;
import net.anfet.tasks.Tasks;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


/**
 * Поддерживающая форма, с рут элементом и уже проинжектенными вьюшками
 */
public abstract class SupportActivity extends AppCompatActivity {

	private View mRoot = null;
	private Collection<DetachableBroadcastReceiver> registeredReceivers;
	protected CompositeDisposable mLifecycleDisposable;
	private final List<IBackpressPropagator> mBackPressPropagators = new LinkedList<>();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		net.anfet.simple.support.library.anotations.Menu menuAnnotation = getClass().getAnnotation(net.anfet.simple.support.library.anotations.Menu.class);
		if (menuAnnotation != null) {
			getMenuInflater().inflate(menuAnnotation.value(), menu);
		}

		return super.onCreateOptionsMenu(menu);
	}

	public void syncToLifecycle(Disposable disposable) {
		mLifecycleDisposable.add(disposable);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		ActivityTransitition activityTransitition = getClass().getAnnotation(ActivityTransitition.class);
		if (activityTransitition != null) {
			overridePendingTransition(activityTransitition.in(), activityTransitition.out());
		}
	}

	public int getRootId() {
		throw new NoIdException("No root id specified");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActivityTransitition activityTransitition = getClass().getAnnotation(ActivityTransitition.class);
		if (activityTransitition != null) {
			overridePendingTransition(activityTransitition.in(), activityTransitition.out());
		}

		setContentView(getLayoutId());
		if (getClass().isAnnotationPresent(Root.class)) {
			mRoot = findViewById(getClass().getAnnotation(Root.class).value());
		} else {
			mRoot = findViewById(getRootId());
		}

		if (getRoot() == null) {
			throw new NullPointerException("No root specified for " + getClass().getSimpleName());
		}

		Fonts.setFont(getRoot(), getFont());
		InflateHelper.injectViewsAndFragments(this, getRoot(), getSupportFragmentManager(), SupportActivity.class);
		InflateHelper.registerSimpleHandlers(this, getRoot(), SupportActivity.class);

		mLifecycleDisposable = new CompositeDisposable();
	}

	protected String getFont() {
		Font font = getClass().getAnnotation(Font.class);
		if (font != null)
			return font.value();

		return Fonts.OPEN_SANS;
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
		Layout annotation = getClass().getAnnotation(Layout.class);
		if (annotation != null && annotation.value() > 0)
			return annotation.value();

		throw new NoLayoutException();

	}

	@Override
	protected void onResume() {
		super.onResume();
		registeredReceivers = InflateHelper.registerLocalReceivers(this, this, getClass());
	}

	@Override
	protected void onPause() {
		Tasks.forfeitAllFor(this);
		InflateHelper.detachReceivers(this, registeredReceivers);
		registeredReceivers = null;
		mLifecycleDisposable.clear();
		mLifecycleDisposable = new CompositeDisposable();
		super.onPause();
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

}
