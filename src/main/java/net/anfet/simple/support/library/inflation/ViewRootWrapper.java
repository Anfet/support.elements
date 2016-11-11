package net.anfet.simple.support.library.inflation;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by Oleg on 12.07.2016.
 */
public class ViewRootWrapper implements IRootWrapper {
	private final View root;

	public ViewRootWrapper(View root) {
		this.root = root;
	}

	private <X extends View> X findViewById(View root, int id) {
		SparseArray<View> holder = (SparseArray<View>) root.getTag();
		if (holder == null)
			root.setTag(holder = new SparseArray<View>());

		View view = holder.get(id);
		if (view == null)
			holder.put(id, (view = root.findViewById(id)));

		return (X) view;
	}

	@Override
	public <T extends View> T findViewById(int id) {
		return findViewById(root, id);
	}

	@Override
	public View getRoot() {
		return root;
	}
}
