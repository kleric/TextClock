package com.kleric.textclock;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

public class TextClock extends WallpaperService {
	private final Handler mHandler = new Handler();
	public static final String SHARED_PREFS_NAME = "textClockSettings";
	private boolean[][] letters = new boolean[12][12];
	private static final char[][] letterValues = {{'I', 'T', 'Q', 'I', 'S', 'W', 'E', 'R', 'H', 'A', 'L', 'F'},
			{'A', 'Y', 'T', 'W', 'E', 'N', 'T', 'Y', 'F', 'I', 'V', 'E'},
			{'T', 'Q', 'U', 'A', 'R', 'T', 'E', 'R', 'U', 'T', 'E', 'N'},
			{'I', 'T', 'O', 'N', 'M', 'P', 'A', 'S', 'T', 'D', 'G', 'F'},
			{'E','Y','T','O','N','E','H','J','S','I','X','K'},
			{'Y','N','I','N','E','C','F','I','V','E','H','L'},
				{'T','W','O','P','O','R','U','T','H','R','E','E'},
				{'X','Z','Y','F','O','U','R','K','R','A','L','C'},
					{'E','I','G','H','T','B','S','E','V','E','N','Q'},
					{'T','W','E','L','V','E','E','F','G','T','E','N'},
						{'V','B','E','L','E','V','E','N','T','I','M','E'},
						{'M','N','K','U','L','O','O','C','L','O','C','K'}};
	
	private static final Point[] ONE_HOUR = {new Point(4, 3), new Point(4, 4), new Point(4, 5)};
	private static final Point[] TWO_HOUR = {new Point(6, 0), new Point(6, 1), new Point(6, 2)};
	private static final Point[] THREE_HOUR = {new Point(6, 7), new Point(6, 8), new Point(6, 9), new Point(6, 10), new Point(6, 11)};
	private static final Point[] FOUR_HOUR = {new Point(7, 3), new Point(7, 4), new Point(7, 5), new Point(7, 6)};
	private static final Point[] FIVE_HOUR = {new Point(1, 8), new Point(1, 9), new Point(1, 10), new Point(1, 11)};
	private static final Point[] SIX_HOUR = {new Point(4, 8), new Point(4, 9), new Point(4, 10)};
	private static final Point[] SEVEN_HOUR = {new Point(8, 6), new Point(8, 7), new Point(8, 8), new Point(8, 9), new Point(8, 10)};
	private static final Point[] EIGHT_HOUR = {new Point(8, 0), new Point(8, 1), new Point(8, 2), new Point(8, 3), new Point(8, 4)};
	private static final Point[] NINE_HOUR = {new Point(5, 1), new Point(5, 2), new Point(5, 3), new Point(5, 4)};
	private static final Point[] TEN_HOUR = {new Point(9, 9), new Point(9, 10), new Point(9, 11)};
	private static final Point[] ELEVEN_HOUR = {new Point(10, 2), new Point(10, 3), new Point(10, 4), new Point(10, 5), new Point(10, 6), new Point(10, 7)};
	private static final Point[] TWELVE_HOUR = {new Point(9, 0), new Point(9, 1), new Point(9, 2), new Point(9, 4), new Point(9, 5)};
	
	private static final Point[] IT = {new Point(0, 0), new Point(0, 1)};
	private static final Point[] IS = {new Point(0, 3), new Point(0, 4)};
	private static final Point[] A = {new Point(1, 0)};
	private static final Point[] TEN = {new Point(2, 9), new Point(2, 10), new Point(2, 11)};
	private static final Point[] TO = {new Point(3, 1), new Point(3, 2)};
	private static final Point[] HALF = {new Point(0, 8), new Point(0, 9), new Point(0, 10), new Point(0, 11)};
	private static final Point[] TWENTY = {new Point(1, 2), new Point(1, 3), new Point(1, 4), new Point(1, 5), new Point(1, 6), new Point(1, 7)};     
	private static final Point[] QUARTER = {new Point(2, 1), new Point(2, 2), new Point(2, 3), new Point(2, 4), new Point(2, 5), new Point(2, 6), new Point(2, 7)};      
	private static final Point[] FIVE = {new Point(1, 8),new Point(1, 9), new Point(1, 10),new Point(1, 11)};
	private static final Point[] PAST = {new Point(3, 5), new Point(3, 6), new Point(3, 7), new Point(3, 8)};
	private static final Point[] OCLOCK = {new Point(11, 6), new Point(11, 7), new Point(11, 8), new Point(11, 9), new Point(11, 10), new Point(11, 11)};
	@Override
	public void onCreate() {
		super.onCreate();
		for(int a = 0; a < letters.length; a++)
		{
			for(int b = 0; b < letters[0].length; b++)
			{
				letters[a][b] = true;
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public Engine onCreateEngine() {
		return new ClockEngine();
	}
	class ClockEngine extends Engine
	{
		private final Paint mPaint = new Paint();
		private long mStartTime;
		private float mOffset;
		private float mWidth = -1;
		private float mHeight = -1;
		private Typeface visitor;
		private final Runnable mDrawClock = new Runnable() {
			public void run() {
				drawFrame();
			}
		};
		private boolean mVisible;
		ClockEngine()
		{
			final Paint paint = mPaint;
			visitor = Typeface.create("Arial", Typeface.NORMAL);//Typeface.createFromAsset(getAssets(), "fonts/visitor.ttf");
			paint.setARGB(255, 200, 9, 9);
			paint.setAntiAlias(true);
			paint.setStrokeWidth(2);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setStyle(Paint.Style.STROKE);
			paint.setTypeface(visitor);
			mStartTime = SystemClock.elapsedRealtime();
		}
		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);

			// By default we don't get touch events, so enable them.
			//setTouchEventsEnabled(true);
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			mHandler.removeCallbacks(mDrawClock);
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			mVisible = visible;
			if (visible) {
				drawFrame();
			} else {
				mHandler.removeCallbacks(mDrawClock);
			}
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			// store the center of the surface, so we can draw the cube in the right spot
			mWidth = width;
			mHeight = height;
			if(mWidth >= mHeight)
			{
				int textSize = (int) (mWidth/28.44444444444444);
				Log.v("Text Clock", textSize + "");
				mPaint.setTextSize(textSize);
			}
			else
			{
				int textSize = (int) (mHeight/28.444444444444);
				Log.v("Text Clock", textSize + "");
				mPaint.setTextSize(textSize);
			}
			drawFrame();
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			mVisible = false;
			mHandler.removeCallbacks(mDrawClock);
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset,
				float xStep, float yStep, int xPixels, int yPixels) {
			mOffset = xOffset;
			drawFrame();
		}
		void drawFrame() {
			final SurfaceHolder holder = getSurfaceHolder();

			Canvas c = null;
			try {
				c = holder.lockCanvas();
				if (c != null) {
					if(mWidth == -1)
					{
						mWidth = c.getWidth();
						mHeight = c.getHeight();
					}
					clearBG(c);
					updateTime();
					drawClock(c);
				}
			} finally {
				if (c != null) holder.unlockCanvasAndPost(c);
			}

			// Reschedule the next redraw
			mHandler.removeCallbacks(mDrawClock);
			if (mVisible) {
				mHandler.postDelayed(mDrawClock, 1000 / 25);
			}
		}
		void clearBG(Canvas c)
		{
			mPaint.setStyle(Style.FILL);
			mPaint.setARGB(255, 0, 0, 0);
			c.drawRect(-1, -1, mWidth + 1, mHeight + 1, mPaint);
			int[] colors = {0x181818, 0xffffff};
			GradientDrawable gradient = new GradientDrawable(Orientation.BOTTOM_TOP, colors);
			gradient.setGradientType(GradientDrawable.LINEAR_GRADIENT);
			gradient.setBounds(0, 0, (int)mWidth + 1, (int)mHeight + 1);
			gradient.draw(c);
			
		}
		void displayPoints(Point[] p)
		{
			for(int a = 0; a < p.length; a++)
			{
				letters[p[a].x][p[a].y] = true;
			}
		}
		void clearLetters()
		{
			for(int a = 0; a < letters.length; a++)
			{
				for(int b = 0; b < letters[0].length; b++)
				{
					letters[a][b] = false;
				}
			}
			displayPoints(IT);
			displayPoints(IS);
		}
		void updateTime()
		{
			clearLetters();
			Date time = Calendar.getInstance().getTime();
			int hour = time.getHours();
			int minutes = time.getMinutes() + 1;
			if(minutes >= 35) //Will display next time
			{
				switch(hour)
				{
				case 0:
				case 12:
					displayPoints(ONE_HOUR);
					break;
				case 1:
				case 13:
					displayPoints(TWO_HOUR);
					break;
				case 2:
				case 14:
					displayPoints(THREE_HOUR);
					break;
				case 3:
				case 15:
					displayPoints(FOUR_HOUR);
					break;
				case 4:
				case 16:
					displayPoints(FIVE_HOUR);
					break;
				case 5:
				case 17:
					displayPoints(SIX_HOUR);
					break;
				case 6:
				case 18:
					displayPoints(SEVEN_HOUR);
					break;
				case 7:
				case 19:
					displayPoints(EIGHT_HOUR);
					break;
				case 8:
				case 20:
					displayPoints(NINE_HOUR);
					break;
				case 9:
				case 21:
					displayPoints(TEN_HOUR);
					break;
				case 10:
				case 22:
					displayPoints(ELEVEN_HOUR);
					break;
				case 11:
				case 23:
					displayPoints(TWELVE_HOUR);
					break;
				}
			}
			else
			{
				switch(hour)
				{
				case 0:
				case 12:
					displayPoints(TWELVE_HOUR);
					break;
				case 1:
				case 13:
					displayPoints(ONE_HOUR);
					break;
				case 2:
				case 14:
					displayPoints(TWO_HOUR);
					break;
				case 3:
				case 15:
					displayPoints(THREE_HOUR);
					break;
				case 4:
				case 16:
					displayPoints(FOUR_HOUR);
					break;
				case 5:
				case 17:
					displayPoints(FIVE_HOUR);
					break;
				case 6:
				case 18:
					displayPoints(SIX_HOUR);
					break;
				case 7:
				case 19:
					displayPoints(SEVEN_HOUR);
					break;
				case 8:
				case 20:
					displayPoints(EIGHT_HOUR);
					break;
				case 9:
				case 21:
					displayPoints(NINE_HOUR);
					break;
				case 10:
				case 22:
					displayPoints(TEN_HOUR);
					break;
				case 11:
				case 23:
					displayPoints(ELEVEN_HOUR);
					break;
				}
			}
			if(minutes >= 0 && minutes < 5)
			{
				//It's OCLOCK
				displayPoints(OCLOCK);
			}
			else if(minutes >= 5 && minutes < 10)
			{
				//IT'S 5 PAST
				displayPoints(FIVE);
				displayPoints(PAST);
			}
			else if(minutes >= 10 && minutes < 15)
			{
				displayPoints(TEN);
				displayPoints(PAST);
				
			}
			else if(minutes >= 15 && minutes < 20)
			{
				displayPoints(QUARTER);
				displayPoints(A);
				displayPoints(PAST);
			}
			else if(minutes >= 20 && minutes < 25)
			{
				displayPoints(TWENTY);
				displayPoints(PAST);
			}
			else if(minutes >= 25 && minutes < 30)
			{
				displayPoints(TWENTY);
				displayPoints(FIVE);
				displayPoints(PAST);
			}
			else if(minutes >= 30 && minutes < 35)
			{
				displayPoints(HALF);
				displayPoints(PAST);
			}
			else if(minutes >= 35 && minutes < 40)
			{
				displayPoints(TWENTY);
				displayPoints(FIVE);
				displayPoints(TO);
			}
			else if(minutes >= 40 && minutes < 45)
			{
				displayPoints(TWENTY);
				displayPoints(TO);
			}
			else if(minutes >= 45 && minutes < 50)
			{
				displayPoints(A);
				displayPoints(QUARTER);
				displayPoints(TO);
			}
			else if(minutes >= 50 && minutes < 55)
			{
				displayPoints(TEN);
				displayPoints(TO);
			}
			else
			{
				displayPoints(FIVE);
				displayPoints(TO);
			}
		}
		void drawClock(Canvas c)
		{
			updateTime();
			drawCurrentTime(c);
			drawBlanks(c);
		}
		void drawCurrentTime(Canvas c)
		{
			int y = (int) ((mHeight - textHeight())/2);	
			int x = (int) ((mWidth - textWidth(24))/2);
			int incre = (int) mPaint.getTextSize();
			int xincre = (int) mPaint.getTextSize()*6/5;
			for(int a = 0; a < letterValues.length; a++)
			{
				//String line = "";
				for(int b = 0; b < letterValues[0].length; b++)
				{
					if(letters[a][b] == true)
					{
						//line += letterValues[a][b];
						mPaint.setARGB(128, 0, 100, 150);
						mPaint.setFakeBoldText(true);
						c.drawText("" + letterValues[a][b], x, y, mPaint);
						mPaint.setARGB(255, 255, 255, 255);
						mPaint.setFakeBoldText(false);
						c.drawText("" + letterValues[a][b], x, y, mPaint);
						
					}
					else
					{
						//line += ' ';
					}
					//line+= ' ';
					x+=xincre;
				}
				/*mPaint.setARGB(255, 255, 255, 255);
				mPaint.setFakeBoldText(false);
				c.drawText(line, x, y, mPaint);
				mPaint.setARGB(128, 255, 255, 255);
				mPaint.setFakeBoldText(true);
				c.drawText(line, x, y, mPaint);*/
				x = (int) ((mWidth - textWidth(24))/2);
				y += incre;
				
			}
		}
		void drawBlanks(Canvas c)
		{
			int y = (int) ((mHeight - textHeight())/2);	
			Log.v("Lol", y + "");
			int x = (int) ((mWidth - textWidth(24))/2);
			int incre = (int) mPaint.getTextSize();
			int xincre = (int) mPaint.getTextSize()*6/5;
			for(int a = 0; a < letterValues.length; a++)
			{
				//String line = "";
				for(int b = 0; b < letterValues[0].length; b++)
				{
					if(letters[a][b] == false)
					{
						//line += letterValues[a][b];
						mPaint.setARGB(255, 69, 69, 69);
						mPaint.setFakeBoldText(false);
						c.drawText("" + letterValues[a][b], x, y, mPaint);
					}
					else
					{
						//line += ' ';
					}
					//line+= ' ';
					x+= xincre;
				}
				mPaint.setARGB(255, 69, 69, 69);
				mPaint.setFakeBoldText(false);
				//c.drawText(line, x, y, mPaint);
				y += incre;
				x = (int) ((mWidth - textWidth(24))/2);
				
			}
		}
		/*void drawDummyText(Canvas c)
		{
			int y = (int) ((mHeight - textHeight())/2);	
			Log.v("Lol", y + "");
			int x = (int) ((mWidth - textWidth(24))/2);
			int incre = (int) mPaint.getTextSize();
			mPaint.setARGB(255, 0, 0, 0);
			for(int a = 0; a < letterValues.length; a++)
			{
				String line = "";
				for(int b = 0; b < letterValues[0].length; b++)
				{
					
					c.drawText(, arg1, arg2, arg3)
				}
			}
			c.drawText(DUMMY_TEXT_ONE, x, y, mPaint);
			y += incre;
			c.drawText(DUMMY_TEXT_TWO, x, y, mPaint);
			y += incre;
			c.drawText(DUMMY_TEXT_THREE, x, y, mPaint);
			y += incre;
			c.drawText(DUMMY_TEXT_FOUR, x, y, mPaint);
			y += incre;
			c.drawText(DUMMY_TEXT_FIVE, x, y, mPaint);
			y += incre;
			c.drawText(DUMMY_TEXT_SIX, x, y, mPaint);
			y += incre;
			c.drawText(DUMMY_TEXT_SEVEN, x, y, mPaint);
			y += incre;
			c.drawText(DUMMY_TEXT_EIGHT, x, y, mPaint);
			y += incre;
			c.drawText(DUMMY_TEXT_NINE, x, y, mPaint);
			y += incre;
			c.drawText(DUMMY_TEXT_TEN, x, y, mPaint);
			y += incre;
			c.drawText(DUMMY_TEXT_ELEVEN, x, y, mPaint);
			y += incre;
			c.drawText(DUMMY_TEXT_TWELVE, x, y, mPaint);
		}*/
		int textWidth(int text)
		{
			return (int) (text * ((mPaint.getTextSize()/2) + mPaint.getTextSize()*0.0972222222222222)); 
		}
		int textHeight()
		{
			return (int) (1.208 * 12 * mPaint.getTextSize()/2);
		}
	}
}