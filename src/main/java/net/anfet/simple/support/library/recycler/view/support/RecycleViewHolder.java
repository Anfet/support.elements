package net.anfet.simple.support.library.recycler.view.support;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import junit.framework.Assert;


/**
 * Элемент ресайкл списка.
 */

public class RecycleViewHolder<T> extends RecyclerView.ViewHolder {

	private final Context context;

	public RecycleViewHolder(@NonNull Context context, @NonNull View view) {
		super(view);
		Assert.assertNotNull(context);
		this.context = context;
	}

	@NonNull
	public Context getContext() {
		return context;
	}
}
