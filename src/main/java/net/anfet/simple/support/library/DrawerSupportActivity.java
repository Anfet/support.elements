package net.anfet.simple.support.library;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;

import net.anfet.simple.support.library.anotations.Drawer;
import net.anfet.simple.support.library.exceptions.NoIdException;
import net.anfet.simple.support.library.inflation.InflateHelper;
import net.anfet.simple.support.library.inflation.ViewRootWrapper;
import net.anfet.simple.support.library.utils.Fonts;
import net.anfet.simple.support.library.utils.IBackpressPropagator;


/**
 * Класс поддержки для {@link SupportActivity} у которых присуствует {@link DrawerLayout} навигационный дравер
 */
public abstract class DrawerSupportActivity extends ToolbarActivity {

	/**
	 * переключатель дравера
	 */
	protected ActionBarDrawerToggle drawerToggle;
	/**
	 * сам {@link DrawerLayout}
	 */
	protected DrawerLayout drawerLayout;
	/**
	 * внутренности {@link DrawerLayout}
	 */
	protected View drawerView;

	private IBackpressPropagator mBackPressPropagator = null;
	private boolean shouldProcessBack = false;

	/**
	 * @return - идентификатор самого дравера
	 */
	public int getDrawerLayoutId() {
		if (getClass().isAnnotationPresent(Drawer.class)) {
			return getClass().getAnnotation(Drawer.class).layout();
		}

		throw new NoIdException("No Drawer annotation/ not drawer layout id present");
	}

	/**
	 * открывает дравер
	 */
	public void openDrawer() {
		drawerLayout.openDrawer(GravityCompat.START);
	}

	/**
	 * закрывает дравер
	 */
	public void closeDrawer() {
		drawerLayout.closeDrawer(GravityCompat.START);
	}

	/**
	 * переключает дравер
	 */
	public void toggleDrawer() {
		if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
			closeDrawer();
		} else {
			openDrawer();
		}
	}

	/**
	 * @return включен ли переключатель дравера из меню
	 */
	public boolean isToggleEnabled() {
		if (getClass().isAnnotationPresent(Drawer.class)) {
			return getClass().getAnnotation(Drawer.class).toggleEnabled();
		}

		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onPreDrawerInit();
		initDrawer();
	}

	private void initDrawer() {
		if (getRoot() instanceof DrawerLayout) {
			drawerLayout = (DrawerLayout) getRoot();
		} else {
			drawerLayout = (DrawerLayout) getRoot().findViewById(getDrawerLayoutId());
		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		if (isToggleEnabled()) {

			drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0) {

				public void onDrawerClosed(View view) {
					super.onDrawerClosed(view);
					DrawerSupportActivity.this.onDrawerClosed();
				}

				public void onDrawerOpened(View drawerView) {
					super.onDrawerOpened(drawerView);
					DrawerSupportActivity.this.supportInvalidateOptionsMenu();
					DrawerSupportActivity.this.onDrawerOpened();
				}
			};

			restoreDrawerHamburger();
		}

		drawerView = getDrawerView();
		InflateHelper.injectViewsAndFragments(this, new ViewRootWrapper(drawerView), null, getClass());
		InflateHelper.registerSimpleHandlers(this, drawerView, getClass());
		Fonts.setFont(drawerView, getFont());
	}

	/**
	 * вызывается перед инициализацией дравера. Поскльку для некоторых фукнций требуется {@link android.support.v7.app.ActionBar} то возможно следуется ее установить тут. Например {@link android.support.v7.widget.Toolbar}
	 */
	protected void onPreDrawerInit() {

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if (drawerToggle != null)
			drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (drawerToggle != null)
			drawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * @return внутренности {@link DrawerLayout}
	 */
	public View getDrawerView() {
		return drawerView == null ? (drawerView = findViewById(getDrawerViewId())) : drawerView;
	}

	/**
	 * @return - сам дравер
	 */
	public DrawerLayout getDrawerLayout() {
		return drawerLayout;
	}

	/**
	 * @return - идентификато корня {@link DrawerLayout}
	 */
	public int getDrawerViewId() {
		if (getClass().isAnnotationPresent(Drawer.class)) {
			return getClass().getAnnotation(Drawer.class).view();
		}

		throw new NoIdException("No drawer view or annotation present");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
	}

	protected void onDrawerOpened() {

	}

	protected void onDrawerClosed() {

	}

	@Override
	public void onBackPressed() {
		shouldProcessBack = true;
		if (mBackPressPropagator != null) {
			shouldProcessBack = !mBackPressPropagator.onBackButtonPressed();
			if (!shouldProcessBack) return;
		}

		super.onBackPressed();
	}

	public boolean shouldProcessBack() {
		return shouldProcessBack;
	}

	public void suppressDrawerHamburger(IBackpressPropagator iBackpressPropagator) {
		mBackPressPropagator = iBackpressPropagator;
		drawerLayout.setDrawerListener(null);
	}

	public void restoreDrawerHamburger() {
		mBackPressPropagator = null;
		drawerLayout.setDrawerListener(drawerToggle);
	}
}
