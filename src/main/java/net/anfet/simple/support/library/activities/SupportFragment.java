package net.anfet.simple.support.library.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import junit.framework.Assert;

import net.anfet.simple.support.library.anotations.Alert;
import net.anfet.simple.support.library.anotations.fonts.UseFont;
import net.anfet.simple.support.library.anotations.layout.LayoutId;
import net.anfet.simple.support.library.anotations.layout.MenuId;
import net.anfet.simple.support.library.anotations.layout.NoLayout;
import net.anfet.simple.support.library.exceptions.NoLayoutException;
import net.anfet.simple.support.library.inflation.DetachableBroadcastReceiver;
import net.anfet.simple.support.library.inflation.InflateHelper;
import net.anfet.simple.support.library.rxtasks.RxTasks;
import net.anfet.simple.support.library.utils.Fonts;
import net.anfet.simple.support.library.utils.IFonted;

import java.util.Collection;

import butterknife.ButterKnife;


/**
 * Фрагмент поддежки
 */
public abstract class SupportFragment extends DialogFragment implements IFonted {

	/**
	 * рут
	 */
	protected View mRoot;
	/**
	 * Список бродкастов для дерегистрации
	 */
	private Collection<DetachableBroadcastReceiver> broadcastReceivers;

	public SupportFragment() {

	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(getClass().isAnnotationPresent(MenuId.class));
	}

	@Override
	public void onResume() {
		super.onResume();
		broadcastReceivers = InflateHelper.registerLocalReceivers(this, getActivity(), getClass());
	}

	/**
	 * @return value раскладки. Если присутствет {@link LayoutId} возвращаем ее, если {@link Alert} то берет оттуда. Если {@link NoLayout} то 0. Если ничего нет, то выбрасываем {@link NoLayoutException}
	 */
	public
	@LayoutRes
	int getLayoutId() {
		LayoutId layoutAnnotation = getClass().getAnnotation(LayoutId.class);
		if (layoutAnnotation != null)
			return layoutAnnotation.value();

		if (getClass().isAnnotationPresent(Alert.class)) {
			return getClass().getAnnotation(Alert.class).layoutId();
		}

		if (getClass().isAnnotationPresent(NoLayout.class)) {
			return 0;
		}

		throw new NoLayoutException("No layoutId for " + getClass().getSimpleName());
	}

	public SupportActivity getSupportActivity() {
		Assert.assertTrue(getActivity() instanceof SupportActivity);
		return (SupportActivity) getActivity();
	}

	protected void inflateLayoutIntoBuilder(AlertDialog.Builder builder) {
		mRoot = LayoutInflater.from(builder.getContext()).inflate(getLayoutId(), null, false);
		builder.setView(mRoot);
	}

	@Override
	public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
		if (getClass().isAnnotationPresent(MenuId.class)) {
			MenuId notation = getClass().getAnnotation(MenuId.class);
			inflater.inflate(notation.value(), menu);
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	/**
	 * Вызывается после того как форму распаковали, заинжектили поля и выставили шрифты
	 */
	public void onAfterInflate(Bundle savedInstanceState) {
		if (getFont() != null) {
			Fonts.setFont(mRoot, getFont());
		}

		InflateHelper.injectViewsAndFragments(this, mRoot, getClass());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (getClass().isAnnotationPresent(Alert.class) || getClass().isAnnotationPresent(NoLayout.class)) {
			return super.onCreateView(inflater, container, savedInstanceState);
		}

		mRoot = inflater.inflate(getLayoutId(), container, false);
		ButterKnife.bind(this, mRoot);
		onAfterInflate(savedInstanceState);
		return mRoot;
	}

	@StyleRes
	protected int getStyleRes() {
		return 0;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if (getClass().isAnnotationPresent(Alert.class)) {

			AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), getStyleRes());
			inflateLayoutIntoBuilder(builder);
			onAfterInflate(savedInstanceState);

			return onSetupDialog(builder, savedInstanceState);
		} else {
			return super.onCreateDialog(savedInstanceState);
		}
	}

	public final View getRoot() {
		return mRoot;
	}

	@Override
	public void onPause() {

		RxTasks.abandonAllFor(this);
		InflateHelper.detachReceivers(getActivity(), broadcastReceivers);
		broadcastReceivers = null;

		super.onPause();
	}

	@Override
	public String getFont() {
		UseFont font = getClass().getAnnotation(UseFont.class);
		if (font != null)
			return font.value();

		return Fonts.ROBOTO_REGULAR;
	}

	/**
	 * Предназначена для обработки билдера и настройки всех моментов диалога. На этот момент билдер уже есть, корневой элемент прописан, поля заинжектены
	 * @param builder билдер
	 * @return диалог для возврата в создание фрагмента
	 */
	protected Dialog onSetupDialog(AlertDialog.Builder builder, Bundle savedInstanceState) {
		return builder.create();
	}
}
