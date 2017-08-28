package net.anfet.simple.support.library.recycler.view.support;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Oleg on 28.07.2017.
 */

public class BinderViewHolder<T, B extends ViewDataBinding> extends RecyclerView.ViewHolder {

	@NonNull
	private final Context context;


	private B mBinder;

	protected BinderViewHolder(@NonNull Context context, @NonNull View itemView) {
		super(itemView);
		this.context = context;
	}

	@NonNull
	protected Context getContext() {
		return context;
	}

	public B getBinder() {
		return mBinder;
	}

	protected void setBinder(B mBinder) {
		this.mBinder = mBinder;
	}
}
