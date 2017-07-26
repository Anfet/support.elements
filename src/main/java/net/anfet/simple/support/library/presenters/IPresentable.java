package net.anfet.simple.support.library.presenters;

/**
 * Created by Oleg on 22.07.2017.
 * <p>
 * элемент который можно презентовать
 */

public interface IPresentable<Z extends Presenter> {

	/**
	 * настраивает презентер. Следует учесть, что настраивать нужно именно указанные презентер
	 */
	void configurePresenter(Z presenter);

	/**
	 * @return презентер
	 */
	Z getPresenter();
}
