package net.anfet.simple.support.library;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import junit.framework.Assert;

import net.anfet.simple.support.library.anotations.Alert;
import net.anfet.simple.support.library.anotations.Font;
import net.anfet.simple.support.library.anotations.Layout;
import net.anfet.simple.support.library.anotations.Menu;
import net.anfet.simple.support.library.anotations.NoLayout;
import net.anfet.simple.support.library.exceptions.NoLayoutException;
import net.anfet.simple.support.library.inflation.DetachableBroadcastReceiver;
import net.anfet.simple.support.library.inflation.InflateHelper;
import net.anfet.simple.support.library.utils.Fonts;
import net.anfet.tasks.Tasks;

import java.util.Collection;


/**
 * Фрагмент поддежки
 */
public abstract class SupportFragment extends DialogFragment {

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
		setHasOptionsMenu(getClass().isAnnotationPresent(Menu.class));
	}

	@Override
	public void onResume() {
		super.onResume();
		broadcastReceivers = InflateHelper.registerLocalReceivers(this, getActivity(), getClass());
	}

	/**
	 * @return value раскладки. Если присутствет {@link Layout} возвращаем ее, если {@link Alert} то берет оттуда. Если {@link NoLayout} то 0. Если ничего нет, то выбрасываем {@link NoLayoutException}
	 */
	public
	@LayoutRes
	int getLayoutId() {
		Layout layoutAnnotation = getClass().getAnnotation(Layout.class);
		if (layoutAnnotation != null)
			return layoutAnnotation.value();

		if (getClass().isAnnotationPresent(Alert.class)) {
			return getClass().getAnnotation(Alert.class).layoutId();
		}

		if (getClass().isAnnotationPresent(NoLayout.class)) {
			return 0;
		}

		throw new NoLayoutException();
	}

	public SupportActivity getSupportActivity() {
		Assert.assertTrue(getActivity() instanceof SupportActivity);
		return (SupportActivity) getActivity();
	}

	protected void inflateLayoutIntoBuilder(AlertDialog.Builder builder) {
		mRoot = LayoutInflater.from(builder.getContext()).inflate(getLayoutId(), null, false);
//		InflateHelper.injectViewsAndFragments(this, mRoot, null, getClass());
//		InflateHelper.registerSimpleHandlers(this, mRoot, getClass());

		builder.setView(mRoot);
	}

	@Override
	public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
		if (getClass().isAnnotationPresent(Menu.class)) {
			Menu notation = getClass().getAnnotation(Menu.class);
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

		InflateHelper.injectViewsAndFragments(this, mRoot, getChildFragmentManager(), getClass());
		InflateHelper.registerSimpleHandlers(this, mRoot, getClass());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (getClass().isAnnotationPresent(Alert.class) || getClass().isAnnotationPresent(NoLayout.class)) {
			return super.onCreateView(inflater, container, savedInstanceState);
		}

		this.mRoot = inflater.inflate(getLayoutId(), container, false);
		onAfterInflate(savedInstanceState);
		return mRoot;
	}

	protected
	@StyleRes
	int getStyleRes() {
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
	public void onAttach(Context context) {
		super.onAttach(context);
		Log.v(getClass().getSimpleName(), "attached");
	}

	@Override
	public void onDetach() {
		Log.v(getClass().getSimpleName(), "detached");
		super.onDetach();
	}

	@Override
	public void onPause() {
		Log.v(getClass().getSimpleName(), "paused");

		Tasks.forfeitAllFor(this);
		InflateHelper.detachReceivers(getActivity(), broadcastReceivers);
		broadcastReceivers = null;

		super.onPause();
	}

	public String getFont() {
		Font font = getClass().getAnnotation(Font.class);
		if (font != null)
			return font.value();

		return Fonts.OPEN_SANS;
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
