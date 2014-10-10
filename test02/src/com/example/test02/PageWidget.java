package com.example.test02;

import android.content.Context;
import android.graphics.Bitmap;
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
	
	private boolean mIsRTandLB; // �Ƿ�������������

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

}
