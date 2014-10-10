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
	private PageWidget mPageWidget;// view对象
	private Bitmap mCurPageBitmap, mNextPageBitmap;
	private Canvas mCurpageCanvas, mNextPageCanvas;
	private BookPageFactory mBookPagefactory;// 书页工厂类
	public String bookFilePath = null;

	@SuppressLint("WrongCall")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 获取屏幕的width,height
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;

		// 创建一个view对象并对myactivity初始化
		mPageWidget = new PageWidget(this);
		setContentView(mPageWidget);
		// 创建两张和屏幕一样大的bitmap
		mCurPageBitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		mNextPageBitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		// 创建两张画布
		mCurpageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);
		// 初始化书页大小
		mBookPagefactory = new BookPageFactory(width, height);
		// 设置书页的背景图片
		mBookPagefactory.setBgBitmap(BitmapFactory.decodeResource(
				this.getResources(), R.drawable.bg));

		mBookPagefactory.openbook(bookFilePath);// 打开书文件 获取到一个书内容的缓存

		mBookPagefactory.onDraw(mCurpageCanvas);// 在画布上写入内容

		mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);// 设置当前页和下一页的bitmap

		// 给mPageWidget view添加屏幕触摸事件
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
