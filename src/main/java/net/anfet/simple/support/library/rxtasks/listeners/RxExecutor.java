package net.anfet.simple.support.library.rxtasks.listeners;

import net.anfet.simple.support.library.rxtasks.RxRunner;

/**
 * Created by Oleg on 17.07.2017.
 */

public interface RxExecutor<In, Out, Progress> {
	Out doExecute(RxRunner<In, Out, Progress> runner, In in) throws Exception;
}
