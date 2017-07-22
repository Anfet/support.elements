package net.anfet.simple.support.library.activities;

import android.content.res.Configuration;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;

import net.anfet.simple.support.library.anotations.layout.DrawerId;
import net.anfet.simple.support.library.exceptions.NoIdException;
import net.anfet.simple.support.library.inflation.InflateHelper;
import net.anfet.simple.support.library.inflation.ViewRootWrapper;
import net.anfet.simple.support.library.utils.Fonts;


/**
 * Класс поддержки для {@link SupportActivity} у которых присуствует {@link DrawerLayout} навигационный дравер
 */
public abstract class DrawerSupportActivity<T extends ViewDataBinding> extends ToolbarActivity<T> {

	/**
	 * переключатель дравера
	 */
	protected ActionBarDrawerToggle mDrawerToggle;
	/**
	 * сам {@link DrawerLayout}
	 */
	protected DrawerLayout mDrawerLayout;
	/**
	 * внутренности {@link DrawerLayout}
	 */
	protected View drawerView;



	/**
	 * @return - идентификатор самого дравера
	 */
	public int getDrawerLayoutId() {
		if (getClass().isAnnotationPresent(DrawerId.class)) {
			return getClass().getAnnotation(DrawerId.class).layout();
		}

		throw new NoIdException("No DrawerId annotation/ not drawer layout value present");
	}

	/**
	 * открывает дравер
	 */
	public void openDrawer() {
		mDrawerLayout.openDrawer(GravityCompat.START);
	}

	/**
	 * закрывает дравер
	 */
	public void closeDrawer() {
		mDrawerLayout.closeDrawer(GravityCompat.START);
	}

	/**
	 * переключает дравер
	 */
	public void toggleDrawer() {
		if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
			closeDrawer();
		} else {
			openDrawer();
		}
	}

	/**
	 * @return включен ли переключатель дравера из меню
	 */
	public boolean isToggleEnabled() {
		return !getClass().isAnnotationPresent(DrawerId.class) || getClass().getAnnotation(DrawerId.class).toggleEnabled();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onPreDrawerInit();
		initDrawer();
	}

	private void initDrawer() {
		if (getRoot() instanceof DrawerLayout) {
			mDrawerLayout = (DrawerLayout) getRoot();
		} else {
			mDrawerLayout = (DrawerLayout) getRoot().findViewById(getDrawerLayoutId());
		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		if (isToggleEnabled()) {

			mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0) {

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

			mDrawerLayout.setDrawerListener(mDrawerToggle);
		}

		drawerView = getDrawerView();
		InflateHelper.injectViewsAndFragments(this, new ViewRootWrapper(drawerView), getClass());
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
		if (mDrawerToggle != null)
			mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (mDrawerToggle != null)
			mDrawerToggle.onConfigurationChanged(newConfig);
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
		return mDrawerLayout;
	}

	/**
	 * @return - идентификато корня {@link DrawerLayout}
	 */
	public int getDrawerViewId() {
		if (getClass().isAnnotationPresent(DrawerId.class)) {
			return getClass().getAnnotation(DrawerId.class).view();
		}

		throw new NoIdException("No drawer view or annotation present");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
	}

	protected void onDrawerOpened() {

	}

	protected void onDrawerClosed() {

	}



}
