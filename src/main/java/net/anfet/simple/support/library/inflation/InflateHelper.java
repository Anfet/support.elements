package net.anfet.simple.support.library.inflation;

import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;

import net.anfet.simple.support.library.anotations.InflatableView;
import net.anfet.simple.support.library.anotations.fonts.Underline;
import net.anfet.simple.support.library.anotations.fonts.UseFont;
import net.anfet.simple.support.library.anotations.receivers.ReceiveBroadcast;
import net.anfet.simple.support.library.reflection.ReflectionSupport;
import net.anfet.simple.support.library.utils.Fonts;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Oleg on 12.07.2016.
 *
 * Помошник для распаковки объектов
 */
public final class InflateHelper {
	
	/**
	 * Инжектит найденные {@link InflatableView} в {@param target}.
	 * @param target            - цель
	 * @param root              - корневой view
	 * @param lowestSuperclass  - самый верхний парент, ниже которого нет смысла лезть
	 */
	public static void injectViewsAndFragments(final Object target, IRootWrapper root, Class lowestSuperclass) {
		List<Field> fields = ReflectionSupport.getFields(target.getClass(), lowestSuperclass);
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				InflatableView viewNotation = field.getAnnotation(InflatableView.class);
				if (viewNotation != null) {
					View view = root.findViewById(viewNotation.value());
					if (view != null) {
						try {
							field.set(target, view);
							if (view instanceof TextView) {
								UseFont fontNotation = field.getAnnotation(UseFont.class);
								if (fontNotation != null) {
									String fontName = fontNotation.value();
									Fonts.setFont(view, fontName);
								}

								if (field.getAnnotation(Underline.class) != null) {
									Fonts.underline(view);
								}
							}
						} catch (IllegalArgumentException ex) {
							Log.e(InflateHelper.class.getName(), "Illegal class assignment: " + ex.getMessage(), ex);
						}


					} else {
						if (viewNotation.required()) {
							throw new AssertionError("Required view: " + field.getName() + " not found");
						}
					}
				}

			} catch (IllegalAccessException e) {
				FirebaseCrash.report(e);
				Log.e(InflateHelper.class.getSimpleName().toUpperCase(), e.getMessage(), e);
			}
		}
	}

	public static void injectViewsAndFragments(final Object target, View root, Class lowestSuperclass) {
		injectViewsAndFragments(target, new ViewRootWrapper(root), lowestSuperclass);
	}

	public static Collection<DetachableBroadcastReceiver> registerLocalReceivers(final Object target, Context context, Class lowestSuperclass) {
		List<DetachableBroadcastReceiver> receivers = new LinkedList<>();
		List<Method> methods = ReflectionSupport.getMethods(target.getClass(), lowestSuperclass);
		for (final Method method : methods) {
			method.setAccessible(true);

			ReceiveBroadcast receiveBroadcastAnnotation = method.getAnnotation(ReceiveBroadcast.class);
			if (receiveBroadcastAnnotation != null) {
				IntentFilter filter = new IntentFilter();

				for (String action : receiveBroadcastAnnotation.value()) {
					filter.addAction(action);
				}

				DetachableBroadcastReceiver receiver = new DetachableBroadcastReceiver(method, target);
				receivers.add(receiver);
				LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);
				context.registerReceiver(receiver, filter);
				receiver.attach();
			}
		}

		return receivers;
	}

	public static void detachReceivers(Context context, Collection<DetachableBroadcastReceiver> receivers) {
		for (DetachableBroadcastReceiver receiver : receivers) {
			receiver.detach();
			LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
			context.unregisterReceiver(receiver);
		}
	}
}
