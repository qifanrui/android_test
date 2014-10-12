package com.example.test02;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;

public class BookPageFactory {
	private int mWidth;
	private int mHeight;

	private int m_fontSize = 24;
	private int m_textColor = Color.BLACK;
	private int m_backColor = 0xffff9e85; // 背景颜色
	private int marginHeight = 20; // 上下与边缘的距离
	private int marginWidth = 15; // 左右与边缘的距离

	private File bookFile = null;// 声明书文件
	private MappedByteBuffer m_mbBuf = null;// 定义缓冲空间
	private Bitmap m_book_bg = null;// 书页的背景图
	/** 字节总长度 */
	private int m_mbBufLen = 0;// 书的大小
	/** 字节开始位置 */
	private int m_mbBufBegin = 0;
	/** 字节结束位置 */
	private int m_mbBufEnd;
	/** Vector<String> m_lines String型数组 */
	private Vector<String> m_lines = new Vector<String>();
	/** 每页可以显示的行数 */
	private int mLineCount; // 每页可以显示的行数
	/** 编码格式 */
	private String m_strCharsetName = "utf-8";
	private float mVisibleHeight; // 绘制内容的高
	private float mVisibleWidth; // 绘制内容的宽
	private boolean m_isfirstPage, m_islastPage;

	private Paint mPaint;// 内容画笔
	private Paint percentPaint;// 百分比画笔
	private String strPercent;// 百分比

	public BookPageFactory(int mWidth, int mHeight) {
		super();
		this.mWidth = mWidth;
		this.mHeight = mHeight;
		// percentPaint.设置不变样式 ；写百分比专用
		percentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		percentPaint.setTextAlign(Align.LEFT);
		percentPaint.setTextSize(24);// 字体大小 24
		percentPaint.setColor(Color.BLACK);// 黑体
		// 下面是几个设置paint的。
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextAlign(Align.LEFT);
		mPaint.setTextSize(m_fontSize);//字体大小 24
		mPaint.setColor(m_textColor);//黑体
		mVisibleWidth = mWidth - marginWidth * 2;// 绘制内容的宽   上下左右的边缘都空一定的距离 所以*2
		mVisibleHeight = mHeight - marginHeight * 2;// 绘制内容的搞
		mLineCount = (int) (mVisibleHeight / m_fontSize); // 可显示的行数 / 可显示的高度除于每个字体的高度
	}

	/**
	 * 设置书页背景图片
	 * 
	 * @param decodeResource
	 *            图片资源
	 */
	public void setBgBitmap(Bitmap decodeResource) {
		m_book_bg = decodeResource;
	}

	/**
	 * 打开书文件 获取到一个书内容的缓存
	 * 
	 * @param bookFilePath
	 *            书的路径
	 * 
	 */
	public void openbook(String bookFilePath) {
		bookFile = new File(bookFilePath);
		long lLen = bookFile.length();// 获取书的总字节数
		m_mbBufLen = (int) lLen;
		try {
			m_mbBuf = new RandomAccessFile(bookFile, "r").getChannel().map(
					FileChannel.MapMode.READ_ONLY, 0, lLen);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 在画布上写入内容
	 * 
	 * @param mCurpageCanvas
	 *            画布
	 */
	@SuppressLint("DrawAllocation")
	public void onDraw(Canvas c) {
		if (m_lines.size() == 0) {
			m_lines = pageDown();
		}
		if (m_lines.size() > 0) {
			if (m_book_bg == null) {
				c.drawColor(m_backColor);
			} else {
				c.drawBitmap(m_book_bg, 0, 0, null);
			}
			int y = marginHeight;
			for (String strLine : m_lines) {
				y += m_fontSize;
				c.drawText(strLine, marginWidth, y, mPaint);
			}
		}
		// 获取百分比并绘制
		float fPercent = (float) (m_mbBufBegin * 1.0 / m_mbBufLen);
		DecimalFormat df = new DecimalFormat("#0.0");
		strPercent = df.format(fPercent * 100) + "%";
		int nPercentWidth = (int) (percentPaint.measureText("999.9%") + 1);
		c.drawText(strPercent, mWidth - nPercentWidth, mHeight - 5,
				percentPaint);
		// TODO time
	}

	/**
	 * 下一页内容
	 * 
	 * @return
	 */
	private Vector<String> pageDown() { // ?
		String strParagraph = "";// 段落
		Vector<String> lines = new Vector<String>();
		while (lines.size() < mLineCount && m_mbBufEnd < m_mbBufLen) {
			byte[] paraBuf = readParagraphForward(m_mbBufEnd);// 读取一个段落
			m_mbBufEnd += paraBuf.length;
			try {
				strParagraph = new String(paraBuf, m_strCharsetName);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String strReturn = "";
			if (strParagraph.indexOf("\r\n") != -1) {
				strReturn = "\r\n";
				strParagraph = strParagraph.replaceAll("\r\n", "");
			} else if (strParagraph.indexOf("\n") != -1) {
				strReturn = "\n";
				strParagraph = strParagraph.replaceAll("\n", "");
			}

			if (strParagraph.length() == 0) {
				lines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				// float str_pixel=mPaint.measureText(strParagraph);
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
						null);
				lines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);
				if (lines.size() >= mLineCount) {
					break;
				}
			}
			if (strParagraph.length() != 0) {
				try {
					m_mbBufEnd -= (strParagraph + strReturn)
							.getBytes(m_strCharsetName).length;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return lines;
	}

	/**
	 * 读取下一个段落
	 * 
	 * @param m_mbBufEnd2
	 *            字节的结束位置
	 * @return
	 */
	private byte[] readParagraphForward(int nFromPos) {
		int nStart = nFromPos;
		int i = nStart;
		byte b0, b1;
		if (m_strCharsetName.equals("UTF-16LE")) {
			while (i < m_mbBufLen - 1) {
				b0 = m_mbBuf.get(i++);
				b1 = m_mbBuf.get(i++);
				if (b0 == 0x0a && b1 == 0x00) {
					break;
				}
			}
		} else if (m_strCharsetName.equals("UTF-16BE")) {
			while (i < m_mbBufLen - 1) {
				b0 = m_mbBuf.get(i++);
				b1 = m_mbBuf.get(i++);
				if (b0 == 0x00 && b1 == 0x0a) {
					break;
				}
			}
		} else {
			while (i < m_mbBufLen) {
				b0 = m_mbBuf.get(i++);
				if (b0 == 0x0a) { // \r\n?
					break;
				}
			}
		}
		int nParaSize = i - nStart;// 段落长度
		byte[] buf = new byte[nParaSize];
		for (i = 0; i < nParaSize; i++) {
			buf[i] = m_mbBuf.get(nFromPos + i);
		}
		return buf;
	}

	public void prePage() {
		if (m_mbBufBegin <= 0) {
			m_mbBufBegin = 0;
			m_isfirstPage = true;
			return;
		} else {
			m_isfirstPage = false;
		}
		m_lines.clear();
		pageUp();
		m_lines = pageDown();
	}

	/**
	 * 前一页的内容
	 */
	private void pageUp() {
		if (m_mbBufBegin < 0) {
			m_mbBufBegin = 0;
		}
		Vector<String> lines = new Vector<String>();
		String strParagraph = "";
		while (lines.size() < mLineCount && m_mbBufBegin > 0) {
			Vector<String> paraLines = new Vector<String>();
			byte[] paraBuf = readParagraphBack(m_mbBufBegin);
			m_mbBufBegin -= paraBuf.length;
			try {
				strParagraph = new String(paraBuf, m_strCharsetName);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			strParagraph = strParagraph.replaceAll("\r\n", "");
			strParagraph = strParagraph.replaceAll("\n", "");
			if (strParagraph.length() == 0) {
				paraLines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
						null);
				paraLines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);
			}
			lines.addAll(0, paraLines);
		}
		while (lines.size() > mLineCount) {
			try {
				m_mbBufBegin += lines.get(0).getBytes(m_strCharsetName).length;
				lines.remove(0);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		m_mbBufEnd = m_mbBufBegin;
		return;
	}

	/**
	 * 获取上一个段落
	 * 
	 * @param nFromPos
	 *            本段落的开始字节
	 * @return
	 */
	private byte[] readParagraphBack(int nFromPos) {
		int nEnd = nFromPos;
		int i;
		byte b0, b1;
		if (m_strCharsetName.equals("UTF-16LE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				b1 = m_mbBuf.get(i + 1);
				if (b0 == 0x0a && b1 == 0x00 && i != nEnd - 2) {
					i += 2;
					break;
				}
				i--;
			}

		} else if (m_strCharsetName.equals("UTF-16BE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				b1 = m_mbBuf.get(i + 1);
				if (b0 == 0x00 && b1 == 0x0a && i != nEnd - 2) {
					i += 2;
					break;
				}
				i--;
			}
		} else {
			i = nEnd - 1;// 之前的结束点， 往回获取
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				if (b0 == 0x0a && i != nEnd - 1) {
					i++;
					break;
				}
				i--;
			}
		}
		if (i < 0) {
			i = 0;
		}
		int nParaSize = nEnd - i;
		int j;
		byte[] buf = new byte[nParaSize];
		for (j = 0; j < nParaSize; j++) {
			buf[j] = m_mbBuf.get(i + j);
		}
		return buf;
	}

	/**
	 * 判断是否为第一页
	 * 
	 * @return true or false
	 */
	public boolean isfistPage() {
		return m_isfirstPage;
	}

	public void nextPage() {
		if (m_mbBufEnd >= m_mbBufLen) {
			m_islastPage = true;
			return;
		} else
			m_islastPage = false;
		m_lines.clear();
		m_mbBufBegin = m_mbBufEnd;// 把之前的结尾地方 为下一个开始位置
		m_lines = pageDown();
	}

	/**
	 * 判断是否为最后一页
	 * 
	 * @return true or false
	 */
	public boolean islastPage() {
		return m_islastPage;
	}

}
