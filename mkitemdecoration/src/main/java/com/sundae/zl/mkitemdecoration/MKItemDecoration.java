package com.sundae.zl.mkitemdecoration;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by @author hzzhoulong
 * on 2017-9-28.
 * # Copyright 2017 netease. All rights reserved.
 */

public class MKItemDecoration extends RecyclerView.ItemDecoration {

	Map<Integer, VHolder> vHolderMap = new HashMap<>();
	private Drawable mDivider;
	private TextPaint textPaint;
	private Builder builder;

	private MKItemDecoration(@NonNull Builder builder) {
		this.builder = builder;
		textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		if (builder.drawable != null) {
			mDivider = builder.drawable;
		} else {
			mDivider = new ColorDrawable(builder.decorationColor);
		}
		textPaint.setTextSize(builder.textSize);
		textPaint.setColor(builder.textColor);

	}

	public enum Type {
		/**
		 * 普通颜色分割线
		 */
		DIVIDER,
		/**
		 * 带文本的分组分割
		 */
		TEXT_HOVER,
		/**
		 * 自定义分组分割样式
		 */
		CUSTOM_HOVER,
	}

	public static class Builder {
		public static final int ALIGN_LEFT = 0;
		public static final int ALIGN_MIDDLE = 1;
		public static final int ALIGN_RIGHT = 2;
		// 整体高度
		private int decorationHeight;
		// 背景色
		private int decorationColor;
		// 分组悬停功能接口
		private IHover iHover;

		private AbstractViewModel viewModel;

		// 分割线开始绘制的位置，一般等于头部数量
		private int itemOffset;

		// 分组时显示的文本大小
		private int textSize;
		// 分组时显示的文本颜色
		private int textColor;
		// 分组时显示的文本距离最近一侧的距离2
		private int textPaddingAbout;
		// 文本在整个分割线中的位置，居左、居中、居右
		private int textAlign;
		// 自定义的背景drawable
		private Drawable drawable;


		public Builder viewModel(AbstractViewModel viewModel) {
			this.viewModel = viewModel;
			return this;
		}

		public Builder drawable(Drawable drawable) {
			this.drawable = drawable;
			return this;
		}

		public Builder height(int decorationHeight) {
			this.decorationHeight = decorationHeight;
			return this;
		}

		public Builder textAlign(int textAlign) {
			this.textAlign = textAlign;
			return this;
		}

		public Builder color(@ColorInt int decorationColor) {
			this.decorationColor = decorationColor;
			return this;
		}

		public Builder iHover(IHover iHover) {
			this.iHover = iHover;
			return this;
		}

		public Builder itemOffset(int itemOffset) {
			this.itemOffset = itemOffset;
			return this;
		}

		public Builder textSize(int textSize) {
			this.textSize = textSize;
			return this;
		}

		public Builder textColor(@ColorInt int textColor) {
			this.textColor = textColor;
			return this;
		}

		public Builder textLeftPadding(int textLeftPadding) {
			this.textPaddingAbout = textLeftPadding;
			return this;
		}

		public MKItemDecoration build() {
			return new MKItemDecoration(this);
		}


	}

	public static class VHolder extends RecyclerView.ViewHolder {

		private SparseArray<View> views;

		public VHolder(View view) {
			super(view);
			this.views = new SparseArray<>();
		}

		public <V extends View> V getView(int viewId) {
			View view = views.get(viewId);
			if (view == null) {
				view = itemView.findViewById(viewId);
				views.put(viewId, view);
			}
			return (V) view;
		}

		public View getRootView() {
			return itemView;
		}
	}

	@Override
	public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
		super.onDraw(c, parent, state);
		if (!(parent.getLayoutManager() instanceof LinearLayoutManager)) {
			return;
		}
		// 目前只支持纵向
		if (((LinearLayoutManager) parent.getLayoutManager()).getOrientation() == LinearLayoutManager.VERTICAL) {
			drawVertical(c, parent);
		} else {
			// 暂时不支持
			throw new RuntimeException("only LinearLayoutManager.VERTICAL is supported!");
		}

	}

	private void drawVertical(Canvas c, RecyclerView parent) {

		if (getType() == Type.CUSTOM_HOVER) {
			drawCustomHovers(c, parent);
		} else if (getType() == Type.TEXT_HOVER) {
			drawTextHovers(c, parent);
		} else {
			drawDividers(c, parent);
		}
	}

	private Type getType() {
		if (builder.iHover != null) {
			return builder.viewModel == null ? Type.TEXT_HOVER : Type.CUSTOM_HOVER;
		}
		return Type.DIVIDER;
	}

	private void drawCustomHovers(Canvas c, RecyclerView parent) {
		int childCount = parent.getChildCount();

		for (int i = 0; i < childCount; i++) {
			final View childView = parent.getChildAt(i);
			int position = parent.getChildAdapterPosition(childView);
			// 跳过需要偏移的item
			if (position < builder.itemOffset) {
				continue;
			}
			if (builder.iHover.isGroup(position)) {
				int top = childView.getTop() - builder.decorationHeight;
				int bottom = childView.getTop();
				drawCustomHover(c, parent, top, bottom, position);
			}
		}
	}

	private void drawTextHovers(Canvas c, RecyclerView parent) {
		int childCount = parent.getChildCount();

		for (int i = 0; i < childCount; i++) {
			final View childView = parent.getChildAt(i);
			int position = parent.getChildAdapterPosition(childView);

			// 跳过需要偏移的item
			if (position < builder.itemOffset) {
				continue;
			}
			// 分组时，在item顶部绘制
			if (builder.iHover.isGroup(position)) {
				// 绘制颜色背景
				int bottom = childView.getTop();
				int top = bottom - builder.decorationHeight;
				drawTextHover(c, parent, top, bottom, position);
			}
		}
	}

	private void drawDividers(Canvas c, RecyclerView parent) {
		int childCount = parent.getChildCount();
		int bottom, top;
		int left = parent.getPaddingLeft();
		int right = parent.getWidth() - parent.getPaddingRight();

		for (int i = 0; i < childCount; i++) {
			final View childView = parent.getChildAt(i);
			int position = parent.getChildAdapterPosition(childView);

			// 跳过需要偏移的item
			if (position < builder.itemOffset || position == parent.getAdapter().getItemCount() - 1) {
				continue;
			}
			// 在item底部绘制一个指定分割线
			top = childView.getBottom();
			bottom = top + builder.decorationHeight;
			mDivider.setBounds(left, top, right, bottom);
			mDivider.draw(c);
		}

	}

	private void drawCustomHover(Canvas c, RecyclerView parent, int top, int bottom, int position) {
//		builder.viewModel.bindView(holder, position);
		c.save();
		c.translate(0, top);
		VHolder vHolder = vHolderMap.get(position);
		if (vHolder != null) {
			vHolder.getRootView().draw(c);
		}
//		holder.getRootView().draw(c);
		c.restore();
	}

	private void drawTextHover(Canvas c, RecyclerView parent, int top, int bottom, int position) {
		final int left = parent.getPaddingLeft();
		final int right = parent.getWidth() - parent.getPaddingRight();
		int baseLine;
		int textLeft;
		mDivider.setBounds(left, top, right, bottom);
		mDivider.draw(c);

		String text = builder.iHover.groupText(position);

		if (!TextUtils.isEmpty(text)) {
			baseLine = getBaseLine(textPaint, bottom);
			textLeft = getTextLeft(parent, text, builder.textAlign, builder.textPaddingAbout);
			c.drawText(text, textLeft, baseLine, textPaint);
		}
	}

	private int getBaseLine(Paint paint, int bottom) {
		int baseLine;
		Paint.FontMetrics fm = paint.getFontMetrics();
		baseLine = (int) (bottom - (builder.decorationHeight - (fm.bottom - fm.top)) / 2 - fm.bottom);
		return baseLine;
	}

	private int getTextLeft(RecyclerView parent, String text, int textAlign, int textPaddingAbout) {
		int textLeft;
		float textWidth = textPaint.measureText(text, 0, text.length());
		if (textAlign == Builder.ALIGN_MIDDLE) {
			textLeft = (int) (parent.getPaddingLeft() + parent.getWidth() / 2 - textWidth / 2);
		} else if (textAlign == Builder.ALIGN_RIGHT) {
			textLeft = (int) (parent.getPaddingLeft() + parent.getWidth() - textWidth - textPaddingAbout);
		} else {
			textLeft = parent.getPaddingLeft() + textPaddingAbout;
		}
		return textLeft;
	}

	@Override
	public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
		super.onDrawOver(c, parent, state);

		if (getType() == Type.DIVIDER) {
			return;
		}
		// 只有需要分组功能时，才走以下逻辑
		int position = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
		if (position <= -1 || position >= parent.getAdapter().getItemCount() - 1) {
			// 越界检查
			return;
		}
		// 寻找即将成为第一个可见item的child
		View child = parent.findViewHolderForLayoutPosition(position + 1).itemView;

		boolean flag = false;
		if (builder.iHover.isGroup(position + 1)) {
			int dy = child.getTop() - builder.decorationHeight * 2;
			// 分组栏移动效果
			if (dy <= 0) {
				c.save();
				c.translate(0, dy);
				flag = true;
			}
		}
		int top = parent.getPaddingTop();
		int bottom = top + builder.decorationHeight;
		if (getType() == Type.CUSTOM_HOVER) {
			drawCustomHover(c, parent, top, bottom, position);
		} else {
			drawTextHover(c, parent, top, bottom, position);
		}
		if (flag) {
			c.restore();
		}
	}

	@Override

	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		super.getItemOffsets(outRect, view, parent, state);
		int pos = parent.getChildAdapterPosition(view);

		if (getType() == Type.TEXT_HOVER) {
			if (builder.iHover.isGroup(pos)) {
				outRect.set(0, builder.decorationHeight, 0, 0);
			}
			return;
		}
		if (getType() == Type.CUSTOM_HOVER) {
			if (vHolderMap.get(pos) == null) {
				VHolder holder = createVH(parent, builder, pos);
				vHolderMap.put(pos, holder);
			}

			if (builder.iHover.isGroup(pos)) {
				outRect.set(0, builder.decorationHeight, 0, 0);
			}
			return;
		}

		if (pos < builder.itemOffset || pos == parent.getAdapter().getItemCount() - 1) {
			outRect.set(0, 0, 0, 0);
		} else {
			outRect.set(0, 0, 0, builder.decorationHeight);
		}

	}

	private VHolder createVH(RecyclerView parent, Builder builder, int pos) {

		View view = LayoutInflater.from(parent.getContext()).inflate(builder.viewModel.layoutId, parent, false);

		int toDrawWidthSpec;//用于测量的widthMeasureSpec
		int toDrawHeightSpec;//用于测量的heightMeasureSpec
		//拿到复杂布局的LayoutParams，如果为空，就new一个。
		// 后面需要根据这个lp 构建toDrawWidthSpec，toDrawHeightSpec
		ViewGroup.LayoutParams lp = view.getLayoutParams();
		if (lp == null) {
			//这里是根据复杂布局layout的width height，new一个Lp
			lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			view.setLayoutParams(lp);
		}
		if (lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
			//如果是MATCH_PARENT，则用父控件能分配的最大宽度和EXACTLY构建MeasureSpec。
			toDrawWidthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight(), View.MeasureSpec.EXACTLY);
		} else if (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
			//如果是WRAP_CONTENT，则用父控件能分配的最大宽度和AT_MOST构建MeasureSpec。
			toDrawWidthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight(), View.MeasureSpec.AT_MOST);
		} else {
			//否则则是具体的宽度数值，则用这个宽度和EXACTLY构建MeasureSpec。
			toDrawWidthSpec = View.MeasureSpec.makeMeasureSpec(lp.width, View.MeasureSpec.EXACTLY);
		}
		//高度同理
		if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {
			toDrawHeightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom(), View.MeasureSpec.EXACTLY);
		} else if (lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
			toDrawHeightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom(), View.MeasureSpec.AT_MOST);
		} else {
			toDrawHeightSpec = View.MeasureSpec.makeMeasureSpec(lp.height, View.MeasureSpec.EXACTLY);
		}
		VHolder vHolder = new VHolder(view);
		builder.viewModel.bindView(vHolder, pos);

		view.measure(toDrawWidthSpec, toDrawHeightSpec);
		view.layout(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getPaddingLeft() + view.getMeasuredWidth(),
				parent.getPaddingTop() + view.getMeasuredHeight());
		builder.decorationHeight = view.getMeasuredHeight();
		return vHolder;
	}
}
