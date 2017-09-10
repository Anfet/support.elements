package net.anfet.simple.support.library.presenters;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

/**
 * Created by Oleg on 26.07.2017.
 */

public final class PresenterSupport {

	@Nullable
	public static <Z extends Presenter> Z create(IPresentable target) {

		Presentable presentableNotation = target.getClass().getAnnotation(Presentable.class);
		if (presentableNotation != null) {
			try {
				Z mPresenter = (Z) presentableNotation.value().newInstance();
				mPresenter.setControl(target);
				target.configurePresenter(mPresenter);
				return mPresenter;
			} catch (java.lang.InstantiationException e) {
				FirebaseCrash.report(e);
			} catch (IllegalAccessException e) {
				FirebaseCrash.report(e);
			}
		} else {
			Log.w(PresenterSupport.class.getSimpleName(), "No Presentable notation is specified");
		}

		return null;
	}
}
