package net.anfet.simple.support.library;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import junit.framework.Assert;

import net.anfet.simple.support.library.anotations.ActivityTransitition;
import net.anfet.simple.support.library.anotations.Font;
import net.anfet.simple.support.library.anotations.Layout;
import net.anfet.simple.support.library.anotations.Root;
import net.anfet.simple.support.library.exceptions.NoIdException;
import net.anfet.simple.support.library.exceptions.NoLayoutException;
import net.anfet.simple.support.library.inflation.InflateHelper;
import net.anfet.simple.support.library.utils.Fonts;
import net.anfet.tasks.Tasks;

import java.util.List;


/**
 * Поддерживающая форма, с рут элементом и уже проинжектенными вьюшками
 */
public abstract class SupportActivity extends AppCompatActivity {

	private View mRoot = null;
	private List<BroadcastReceiver> registeredReceivers;
	private List<BroadcastReceiver> registeredGlovalReceivers;

	public void initToolbar(@NonNull Toolbar toolbar) {
		Assert.assertNotNull(toolbar);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setTitle("");
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		ActivityTransitition activityTransitition = getClass().getAnnotation(ActivityTransitition.class);
		if (activityTransitition != null) {
			overridePendingTransition(activityTransitition.in(), activityTransitition.out());
		}
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
		}

		if (getRoot() == null) {
			throw new NullPointerException("No root specified for " + getClass().getSimpleName());
		}


		if (!(getRoot() instanceof CoordinatorLayout) && !(getRoot() instanceof FrameLayout))
			Log.v(SupportActivity.class.getSimpleName(), "Root is recommended as Coordinator/Frame Layouts");

		Fonts.setFont(getRoot(), getFont());
		InflateHelper.injectViewsAndFragments(this, getRoot(), getSupportFragmentManager(), SupportActivity.class);
		InflateHelper.registerSimpleHandlers(this, getRoot(), SupportActivity.class);
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
		registeredGlovalReceivers = InflateHelper.registerGlobalReceivers(this, this, getClass());
	}

	@Override
	protected void onPause() {
		Tasks.forfeitAllFor(this);
		for (BroadcastReceiver receiver : registeredReceivers)
			LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

		for (BroadcastReceiver receiver : registeredGlovalReceivers) {
			unregisterReceiver(receiver);
		}

		registeredReceivers = null;
		registeredGlovalReceivers = null;
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


}
