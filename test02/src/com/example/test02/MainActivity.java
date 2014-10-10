package com.example.test02;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MainActivity extends Activity {
	private PageWidget mPageWidget;// view����
	private Bitmap mCurPageBitmap, mNextPageBitmap;
	private Canvas mCurpageCanvas, mNextPageCanvas;
	private BookPageFactory mBookPagefactory;// ��ҳ������
	public String bookFilePath = null;

	@SuppressLint("WrongCall")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ��ȡ��Ļ��width,height
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;

		// ����һ��view���󲢶�myactivity��ʼ��
		mPageWidget = new PageWidget(this);
		setContentView(mPageWidget);
		// �������ź���Ļһ�����bitmap
		mCurPageBitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		mNextPageBitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		// �������Ż���
		mCurpageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);
		// ��ʼ����ҳ��С
		mBookPagefactory = new BookPageFactory(width, height);
		// ������ҳ�ı���ͼƬ
		mBookPagefactory.setBgBitmap(BitmapFactory.decodeResource(
				this.getResources(), R.drawable.bg));

		mBookPagefactory.openbook(bookFilePath);// �����ļ� ��ȡ��һ�������ݵĻ���

		mBookPagefactory.onDraw(mCurpageCanvas);// �ڻ�����д������

		mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);// ���õ�ǰҳ����һҳ��bitmap

		// ��mPageWidget view�����Ļ�����¼�
		mPageWidget.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				boolean ret = false;
				if (v == mPageWidget) {
					if(event.getAction()==MotionEvent.ACTION_DOWN){
						mPageWidget.abortAnimation();
						mPageWidget.calcCornerXY(event.getX(), event.getY());
					}
				}

				return false;
			}
		});
	}

}
