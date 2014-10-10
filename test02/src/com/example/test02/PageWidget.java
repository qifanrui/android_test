package com.example.test02;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Scroller;

public class PageWidget extends View {
	// view的宽和高
	private int mWidth = 0;
	private int mHeight = 0;

	private Bitmap mCurPageBitmap = null; // 当前页
	private Bitmap mNextPageBitmap = null;// 下一页
	private Scroller mScroller;// 这个类封装了滚动
	// 拖拽点对应的页角
	private int mCornerX = 0;
	private int mCornerY = 0;
	
	private boolean mIsRTandLB; // 是否属于右上左下

	public PageWidget(Context context) {
		super(context);
	}

	/**
	 * 设置当前页和下一页的bitmap
	 * 
	 * @param mCurPageBitmap
	 *            当前页
	 * @param mNextPageBitmap
	 *            下一页
	 */
	public void setBitmaps(Bitmap mCurPageBitmap, Bitmap mNextPageBitmap) {
		this.mCurPageBitmap = mCurPageBitmap;
		this.mNextPageBitmap = mNextPageBitmap;
	}

	/**
	 * 中止动画,如果照片卷轴没完成滚动
	 */
	public void abortAnimation() {
		if (!mScroller.isFinished()) {
			mScroller.abortAnimation();
		}
	}

	/**
	 * 计算拖拽点对应的拖拽脚，以点击的点的坐标位置判断左右翻
	 * 
	 * @param x
	 *            手指按下屏幕对应的x坐标
	 * @param y
	 *            手指按下屏幕对应的y坐标
	 * 
	 */
	public void calcCornerXY(float x, float y) {

		if (x <= mWidth / 2) {
			mCornerX = 0;
		} else {
			mCornerX = mWidth;
		}

		if (y <= mHeight / 2) {
			mCornerY = 0;
		} else {
			mCornerY = mHeight;
		}
		if ((mCornerX == 0 && mCornerY == mHeight)
				|| (mCornerX == mWidth && mCornerY == 0))
			mIsRTandLB = true;
		else
			mIsRTandLB = false;
	}

}
