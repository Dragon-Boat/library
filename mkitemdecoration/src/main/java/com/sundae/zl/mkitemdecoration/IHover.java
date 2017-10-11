package com.sundae.zl.mkitemdecoration;

/**
 * 分组悬停接口
 * Created by @author zhoulong
 * on 2017-10-9.
 * # Copyright 2017 . All rights reserved.
 */

public interface IHover {

	/**
	 * 当前position是否需要绘制分组栏
	 * @param position 当前位置
	 * @return true表示需要绘制
	 */
	boolean isGroup(int position);


	/**
	 * 当前位置需要绘制的文本
	 * @param position 当前位置
	 * @return String
	 */
	String groupText(int position);
}
