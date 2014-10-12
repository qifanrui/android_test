package com.example.test02;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.MotionEvent;
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
	private PointF mTouch = new PointF(); // 拖拽点
	private boolean mIsRTandLB; // 是否属于右上左下
	
	PointF mBezierStart1 = new PointF(); // 贝塞尔曲线起始点1
	PointF mBezierControl1 = new PointF(); // 贝塞尔曲线控制点1
	PointF mBeziervertex1 = new PointF(); // 贝塞尔曲线顶点1
	PointF mBezierEnd1 = new PointF(); // 贝塞尔曲线结束点1

	PointF mBezierStart2 = new PointF(); // 贝塞尔曲线起始点2
	PointF mBezierControl2 = new PointF();// 贝塞尔曲线控制点2
	PointF mBeziervertex2 = new PointF();// 贝塞尔曲线顶点2
	PointF mBezierEnd2 = new PointF();// 贝塞尔曲线结束点2
	
	
	/** 中间的x坐标 */
	float mMiddleX;
	/** 中间的y坐标 */
	float mMiddleY;
	float mTouchToCornerDis;
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
	
	/**
	 * 判断翻页方向
	 * @return false：点击点为屏幕右边；true：点击点为屏幕左边
	 */
	public boolean DragToRight() {
		if (mCornerX > 0)
			return false;
		return true;
	}

	/**
	 * 做触摸事件
	 * @param event
	 * @return
	 */
	public boolean doTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			mTouch.x = event.getX();
			mTouch.y = event.getY();
			this.postInvalidate();
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mTouch.x = event.getX();
			mTouch.y = event.getY();
			// calcCornerXY(mTouch.x, mTouch.y);
			// this.postInvalidate();
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (canDragOver()) {
				startAnimation(1200);
			} else {
				mTouch.x = mCornerX - 0.09f;
				mTouch.y = mCornerY - 0.09f;
			}

			this.postInvalidate();
		}
		// return super.onTouchEvent(event);
		return true;
	}
	/**
	 * 开始动画
	 * @param delayMillis 动画用时，ms;
	 */
	private void startAnimation(int delayMillis) {
		int dx, dy;
		// dx 水平方向滑动的距离，负值会使滚动向左滚动
		// dy 垂直方向滑动的距离，负值会使滚动向上滚动
		if (mCornerX > 0) {
			dx = -(int) (mWidth + mTouch.x);
		} else {
			dx = (int) (mWidth - mTouch.x + mWidth);
		}
		if (mCornerY > 0) {
			dy = (int) (mHeight - mTouch.y);
		} else {
			dy = (int) (1 - mTouch.y); // 防止mTouch.y最终变为0
		}
		mScroller.startScroll((int) mTouch.x, (int) mTouch.y, dx, dy,
				delayMillis);
	}
	/**
	 * 判断是否可以拖过
	 * @return
	 */
	private boolean canDragOver() {
		if (mTouchToCornerDis > mWidth / 10)
			return true;
		return false;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(0xFFAAAAAA);
		calcPoints();
	}

	private void calcPoints() {
		mMiddleX = (mTouch.x + mCornerX) / 2;
		mMiddleY = (mTouch.y + mCornerY) / 2;
		mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY)
				* (mCornerY - mMiddleY) / (mCornerX - mMiddleX);
		mBezierControl1.y = mCornerY;
		mBezierControl2.x = mCornerX;
		mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
				* (mCornerX - mMiddleX) / (mCornerY - mMiddleY);
		mBezierStart1.x = mBezierControl1.x - (mCornerX - mBezierControl1.x)
				/ 2;
		mBezierStart1.y = mCornerY;

		// 当mBezierStart1.x < 0或者mBezierStart1.x > 480时
		// 如果继续翻页，会出现BUG故在此限制
		if (mTouch.x > 0 && mTouch.x < mWidth) {
			if (mBezierStart1.x < 0 || mBezierStart1.x > mWidth) {
				if (mBezierStart1.x < 0)
					mBezierStart1.x = mWidth - mBezierStart1.x;

				float f1 = Math.abs(mCornerX - mTouch.x);
				float f2 = mWidth * f1 / mBezierStart1.x;
				mTouch.x = Math.abs(mCornerX - f2);

				float f3 = Math.abs(mCornerX - mTouch.x)
						* Math.abs(mCornerY - mTouch.y) / f1;
				mTouch.y = Math.abs(mCornerY - f3);

				mMiddleX = (mTouch.x + mCornerX) / 2;
				mMiddleY = (mTouch.y + mCornerY) / 2;

				mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY)
						* (mCornerY - mMiddleY) / (mCornerX - mMiddleX);
				mBezierControl1.y = mCornerY;

				mBezierControl2.x = mCornerX;
				mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
						* (mCornerX - mMiddleX) / (mCornerY - mMiddleY);
				// Log.i("hmg", "mTouchX --> " + mTouch.x + "  mTouchY-->  "
				// + mTouch.y);
				// Log.i("hmg", "mBezierControl1.x--  " + mBezierControl1.x
				// + "  mBezierControl1.y -- " + mBezierControl1.y);
				// Log.i("hmg", "mBezierControl2.x -- " + mBezierControl2.x
				// + "  mBezierControl2.y -- " + mBezierControl2.y);
				mBezierStart1.x = mBezierControl1.x
						- (mCornerX - mBezierControl1.x) / 2;
			}
		}
		mBezierStart2.x = mCornerX;
		mBezierStart2.y = mBezierControl2.y - (mCornerY - mBezierControl2.y)
				/ 2;

		mTouchToCornerDis = (float) Math.hypot((mTouch.x - mCornerX),
				(mTouch.y - mCornerY));

		mBezierEnd1 = getCross(mTouch, mBezierControl1, mBezierStart1,
				mBezierStart2);
		mBezierEnd2 = getCross(mTouch, mBezierControl2, mBezierStart1,
				mBezierStart2);

		// Log.i("hmg", "mBezierEnd1.x  " + mBezierEnd1.x + "  mBezierEnd1.y  "
		// + mBezierEnd1.y);
		// Log.i("hmg", "mBezierEnd2.x  " + mBezierEnd2.x + "  mBezierEnd2.y  "
		// + mBezierEnd2.y);

		/*
		 * mBeziervertex1.x 推导
		 * ((mBezierStart1.x+mBezierEnd1.x)/2+mBezierControl1.x)/2 化简等价于
		 * (mBezierStart1.x+ 2*mBezierControl1.x+mBezierEnd1.x) / 4
		 */
		mBeziervertex1.x = (mBezierStart1.x + 2 * mBezierControl1.x + mBezierEnd1.x) / 4;
		mBeziervertex1.y = (2 * mBezierControl1.y + mBezierStart1.y + mBezierEnd1.y) / 4;
		mBeziervertex2.x = (mBezierStart2.x + 2 * mBezierControl2.x + mBezierEnd2.x) / 4;
		mBeziervertex2.y = (2 * mBezierControl2.y + mBezierStart2.y + mBezierEnd2.y) / 4;
	
	}

	private PointF getCross(PointF P1, PointF P2,
			PointF P3, PointF P4) {
		PointF CrossP = new PointF();
		// 二元函数通式： y=ax+b
				float a1 = (P2.y - P1.y) / (P2.x - P1.x);
				float b1 = ((P1.x * P2.y) - (P2.x * P1.y)) / (P1.x - P2.x);

				float a2 = (P4.y - P3.y) / (P4.x - P3.x);
				float b2 = ((P3.x * P4.y) - (P4.x * P3.y)) / (P3.x - P4.x);
				CrossP.x = (b2 - b1) / (a1 - a2);
				CrossP.y = a1 * CrossP.x + b1;
				return CrossP;
	}
}
