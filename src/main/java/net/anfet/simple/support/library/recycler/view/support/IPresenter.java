package net.anfet.simple.support.library.recycler.view.support;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

/**
 * Presenter for {RecyclerAdapterViewHolder}
 * Класс отвечает за заполнения всей вьюшки адаптера
 */

public interface IPresenter<T, X extends RecycleViewHolder<T>> {

	/**
	 * Заполняет все данные вьюшки
	 * @param holder   холдер
	 * @param t        элемент адаптера
	 * @param position позиция
	 */
	void populateView(@NonNull Context context, @NonNull X holder, @NonNull T t, int position);

	/**
	 * Вызывается на этапе создания холдера. Поскольку это чистая инициализация - никаких данных о элементе тут нет. Они будут переданы в {@link IPresenter#populateView(RecycleViewHolder, Object, int)}
	 * @param holder холдер
	 */
	void initHolder(@NonNull X holder);

	@LayoutRes
	int getLayoutId();

	/**
	 * Переписываем это когда нужно создать собственный кастомный холдер
	 * @param context контекст
	 * @param parent  родительская вьюшка
	 * @return новый холдер
	 */
	X getNewViewHolder(@NonNull Context context, @NonNull ViewGroup parent);

}
