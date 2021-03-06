package net.anfet.simple.support.library.inflation;

import android.content.Context;
import android.content.IntentFilter;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import net.anfet.simple.support.library.anotations.AdapterItemClickHandler;
import net.anfet.simple.support.library.anotations.ClickHandler;
import net.anfet.simple.support.library.anotations.Font;
import net.anfet.simple.support.library.anotations.InflatableFragment;
import net.anfet.simple.support.library.anotations.InflatableGroup;
import net.anfet.simple.support.library.anotations.InflatableView;
import net.anfet.simple.support.library.anotations.LongClickHandler;
import net.anfet.simple.support.library.anotations.MultiActionLocalReceiver;
import net.anfet.simple.support.library.anotations.RadioGroup;
import net.anfet.simple.support.library.anotations.SingleActionLocalReceiver;
import net.anfet.simple.support.library.anotations.Underline;
import net.anfet.simple.support.library.reflection.ReflectionSupport;
import net.anfet.simple.support.library.utils.Fonts;
import net.anfet.simple.support.library.wrappers.WrapperAdapterClickListener;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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


	private static final Object mAttachLock = new Object();
	
	/**
	 * Инжектит найденные {@link InflatableView} и {@link InflatableFragment} в {@param target}.
	 * @param target            - цель
	 * @param root              - корневой view
	 * @param fragmentManagerV4 - фрагмент менеджер
	 * @param lowestSuperclass  - самый верхний парент, ниже которого нет смысла лезть
	 */
	public static final void injectViewsAndFragments(final Object target, IRootWrapper root, FragmentManager fragmentManagerV4, Class lowestSuperclass) {
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
								Font fontNotation = field.getAnnotation(Font.class);
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

					continue;
				}

				InflatableGroup inflatableGroup = field.getAnnotation(InflatableGroup.class);
				if (inflatableGroup != null) {
					InflatableViewGroup viewGroup = new InflatableViewGroup();
					for (Integer i : inflatableGroup.value()) {
						viewGroup.addView(root.findViewById(i));
					}
					field.set(target, viewGroup);
					continue;
				}

				RadioGroup radioGroup = field.getAnnotation(RadioGroup.class);
				if (radioGroup != null) {
					RadioViewGroup viewGroup = new RadioViewGroup();
					for (Integer i : radioGroup.value()) {
						viewGroup.addView(root.findViewById(i));
					}
					field.set(target, viewGroup);
					continue;
				}

				if (fragmentManagerV4 != null) {
					InflatableFragment fragmentNotation = field.getAnnotation(InflatableFragment.class);
					if (fragmentNotation != null) {
						field.set(target, fragmentManagerV4.findFragmentById(fragmentNotation.value()));
					}
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public static void injectViewsAndFragments(final Object target, View root, FragmentManager fragmentManagerV4, Class lowestSuperclass) {
		injectViewsAndFragments(target, new ViewRootWrapper(root), fragmentManagerV4, lowestSuperclass);
	}

	public static void registerSimpleHandlers(final Object target, View root, Class lowestSuperclass) {
		List<Method> methods = ReflectionSupport.getMethods(target.getClass(), lowestSuperclass);
		for (final Method method : methods) {
			ClickHandler clickHandler = method.getAnnotation(ClickHandler.class);
			if (clickHandler != null) {
				for (int id : clickHandler.value()) {
					final View view = root.findViewById(id);
					if (view == null) {
						if (clickHandler.required()) {
							throw new AssertionError("Some id not found for click method: " + method.getName());
						}

						Log.e(InflateHelper.class.getSimpleName(), "Some id not found for click method: " + method.getName());
						continue;
					}

					view.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View element) {
							try {
								method.invoke(target, element);
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
						}
					});
				}
			}

			LongClickHandler handler = method.getAnnotation(LongClickHandler.class);
			if (handler != null) {
				for (int id : handler.value()) {
					final View view = root.findViewById(id);
					if (view == null) {
						Log.e(InflateHelper.class.getSimpleName(), "Some id not found for long click method: " + method.getName());
						continue;
					}

					view.setOnLongClickListener(new View.OnLongClickListener() {

						@Override
						public boolean onLongClick(View view) {
							try {
								return (boolean) method.invoke(target, view);
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
							return false;
						}
					});
				}
			}

			AdapterItemClickHandler itemClickHandler = method.getAnnotation(AdapterItemClickHandler.class);
			if (itemClickHandler != null) {
				final View view = root.findViewById(itemClickHandler.value());
				if (view == null) {
					Log.e(InflateHelper.class.getSimpleName(), "Some id not found for AdapterItemClickHandler click method: " + method.getName());
					continue;
				}

				if (view instanceof AdapterView) {
					((AdapterView) view).setOnItemClickListener(new WrapperAdapterClickListener<Object>() {
						@Override
						public void onItemClick(Object item) {
							try {
								method.invoke(target, item);
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
						}
					});
				}
			}
		}
	}

	public static Collection<DetachableBroadcastReceiver> registerLocalReceivers(final Object target, Context context, Class lowestSuperclass) {
		List<DetachableBroadcastReceiver> receivers = new LinkedList<>();
		List<Method> methods = ReflectionSupport.getMethods(target.getClass(), lowestSuperclass);
		for (final Method method : methods) {
			method.setAccessible(true);

			SingleActionLocalReceiver annotation = method.getAnnotation(SingleActionLocalReceiver.class);
			if (annotation != null) {
				DetachableBroadcastReceiver receiver = new DetachableBroadcastReceiver(method, target);
				receivers.add(receiver);
				context.registerReceiver(receiver, new IntentFilter(annotation.value()));
				LocalBroadcastManager.getInstance(context).registerReceiver(receiver, new IntentFilter(annotation.value()));
				receiver.attach();
			}


			MultiActionLocalReceiver multiActionLocalReceiverAnnotation = method.getAnnotation(MultiActionLocalReceiver.class);
			if (multiActionLocalReceiverAnnotation != null) {
				IntentFilter filter = new IntentFilter();

				for (String action : multiActionLocalReceiverAnnotation.value()) {
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

	/**
	 * устанавливает во все текстовые поля указанный цвет
	 * @param context
	 * @param view
	 * @param plainTextColor
	 */
	public static void setTextColor(Context context, @Nullable View view, @ColorRes int plainTextColor) {
		if (view == null)
			return;

		if (view instanceof TextView) {
			((TextView) view).setTextColor(context.getResources().getColor(plainTextColor));
		} else if (view instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) view;

			for (int i = 0; i < vg.getChildCount(); i++) {
				View child = vg.getChildAt(i);
				setTextColor(context, child, plainTextColor);
			}
		}
	}

	public static void detachReceivers(Context context, Collection<DetachableBroadcastReceiver> receivers) {
		for (DetachableBroadcastReceiver receiver : receivers) {
			receiver.detach();
			LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
			context.unregisterReceiver(receiver);
		}
	}
}
