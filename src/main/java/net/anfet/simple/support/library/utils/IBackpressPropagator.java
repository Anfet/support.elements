package net.anfet.simple.support.library.utils;

/**
 * Created by Oleg on 21.03.2017.
 * <p>
 * получает уведомление о клике "назад" и решает что с ним делать
 */

public interface IBackpressPropagator {

	/**
	 * передает нажатие кнопки назад
	 * @return true если событие обработано, false если нет
	 */
	boolean onBackButtonPressed();
}
