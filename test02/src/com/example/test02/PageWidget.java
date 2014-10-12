package com.example.test02;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

public class PageWidget extends View {
	// view�Ŀ�͸�
	private int mWidth = 0;
	private int mHeight = 0;

	private Bitmap mCurPageBitmap = null; // ��ǰҳ
	private Bitmap mNextPageBitmap = null;// ��һҳ
	private Scroller mScroller;// ������װ�˹���
	// ��ק���Ӧ��ҳ��
	private int mCornerX = 0;
	private int mCornerY = 0;
	private PointF mTouch = new PointF(); // ��ק��
	private boolean mIsRTandLB; // �Ƿ�������������
	
	PointF mBezierStart1 = new PointF(); // ������������ʼ��1
	PointF mBezierControl1 = new PointF(); // ���������߿��Ƶ�1
	PointF mBeziervertex1 = new PointF(); // ���������߶���1
	PointF mBezierEnd1 = new PointF(); // ���������߽�����1

	PointF mBezierStart2 = new PointF(); // ������������ʼ��2
	PointF mBezierControl2 = new PointF();// ���������߿��Ƶ�2
	PointF mBeziervertex2 = new PointF();// ���������߶���2
	PointF mBezierEnd2 = new PointF();// ���������߽�����2
	
	
	/** �м��x���� */
	float mMiddleX;
	/** �м��y���� */
	float mMiddleY;
	float mTouchToCornerDis;
	public PageWidget(Context context) {
		super(context);
	}

	/**
	 * ���õ�ǰҳ����һҳ��bitmap
	 * 
	 * @param mCurPageBitmap
	 *            ��ǰҳ
	 * @param mNextPageBitmap
	 *            ��һҳ
	 */
	public void setBitmaps(Bitmap mCurPageBitmap, Bitmap mNextPageBitmap) {
		this.mCurPageBitmap = mCurPageBitmap;
		this.mNextPageBitmap = mNextPageBitmap;
	}

	/**
	 * ��ֹ����,�����Ƭ����û��ɹ���
	 */
	public void abortAnimation() {
		if (!mScroller.isFinished()) {
			mScroller.abortAnimation();
		}
	}

	/**
	 * ������ק���Ӧ����ק�ţ��Ե���ĵ������λ���ж����ҷ�
	 * 
	 * @param x
	 *            ��ָ������Ļ��Ӧ��x����
	 * @param y
	 *            ��ָ������Ļ��Ӧ��y����
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
	 * �жϷ�ҳ����
	 * @return false�������Ϊ��Ļ�ұߣ�true�������Ϊ��Ļ���
	 */
	public boolean DragToRight() {
		if (mCornerX > 0)
			return false;
		return true;
	}

	/**
	 * �������¼�
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
	 * ��ʼ����
	 * @param delayMillis ������ʱ��ms;
	 */
	private void startAnimation(int delayMillis) {
		int dx, dy;
		// dx ˮƽ���򻬶��ľ��룬��ֵ��ʹ�����������
		// dy ��ֱ���򻬶��ľ��룬��ֵ��ʹ�������Ϲ���
		if (mCornerX > 0) {
			dx = -(int) (mWidth + mTouch.x);
		} else {
			dx = (int) (mWidth - mTouch.x + mWidth);
		}
		if (mCornerY > 0) {
			dy = (int) (mHeight - mTouch.y);
		} else {
			dy = (int) (1 - mTouch.y); // ��ֹmTouch.y���ձ�Ϊ0
		}
		mScroller.startScroll((int) mTouch.x, (int) mTouch.y, dx, dy,
				delayMillis);
	}
	/**
	 * �ж��Ƿ�����Ϲ�
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

		// ��mBezierStart1.x < 0����mBezierStart1.x > 480ʱ
		// ���������ҳ�������BUG���ڴ�����
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
		 * mBeziervertex1.x �Ƶ�
		 * ((mBezierStart1.x+mBezierEnd1.x)/2+mBezierControl1.x)/2 ����ȼ���
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
		// ��Ԫ����ͨʽ�� y=ax+b
				float a1 = (P2.y - P1.y) / (P2.x - P1.x);
				float b1 = ((P1.x * P2.y) - (P2.x * P1.y)) / (P1.x - P2.x);

				float a2 = (P4.y - P3.y) / (P4.x - P3.x);
				float b2 = ((P3.x * P4.y) - (P4.x * P3.y)) / (P3.x - P4.x);
				CrossP.x = (b2 - b1) / (a1 - a2);
				CrossP.y = a1 * CrossP.x + b1;
				return CrossP;
	}
}
