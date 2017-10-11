package com.sundae.zl.mkitemdecoration;

import android.content.Context;
import android.support.annotation.LayoutRes;

import java.util.List;
/**
 * Created by @author hzzhoulong
 * on 2017-10-10.
 * # Copyright 2017 netease. All rights reserved.
 */

public abstract class AbstractViewModel<T> {

	public List<T> data;
	int layoutId;

	public AbstractViewModel(Context context, List<T> data, @LayoutRes int resId) {
		this.data = data;
		this.layoutId = resId;
	}

	public abstract void bindView(MKItemDecoration.VHolder view, int position);
}
