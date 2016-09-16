package com.example.vc.lifecalendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.vc.lifecalendar.util.PreferenceHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class LifeCalendarView extends View {
    public enum DISPLAY {
      CURRENT_YEAR, LIFE
    }
    public static int DISPLAY_CURRENT_YEAR = 0;
    public static int DISPLAY_LIFE = 1;
    public static String TAG = "LifeCalendarView";

    DISPLAY mDisplay;
    Paint mPastPaint;
    Paint mPresentPaint;
    Paint mFuturePaint;

    int mLifeExpectancy;
    Calendar mBirthday;
    RectF mRectF;
    int mTotalDots;
    int mCurrentDot;
    int mDotsPerLine;
    int mDiameter;
    int mSpacingH;
    int mSpacingV;
    public LifeCalendarView(Context context) {
        this(context, null, 0);
    }

    public LifeCalendarView(Context context, Bundle bundle) {
        super(context);
        mLifeExpectancy = bundle.getInt(PreferenceHelper.LIFE_EXPECTANCY);
        String birthday = bundle.getString(PreferenceHelper.BIRTHDAY, null);
        mDisplay = DISPLAY.values()[bundle.getInt(PreferenceHelper.DISPLAY, 1)];
        try {
            mBirthday = new GregorianCalendar();
            mBirthday.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        init();
        updateDisplyParam();
    }

    public LifeCalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public LifeCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.LifeCalendarView,
                0, 0);

        try {
            mLifeExpectancy = a.getInt(R.styleable.LifeCalendarView_lifeExpectancy, 70);
            mDisplay = DISPLAY.values()[a.getInt(R.styleable.LifeCalendarView_display, 1)];
            String birthday = a.getString(R.styleable.LifeCalendarView_birthday);
            if (birthday == null) {
                birthday = "1989-04-10";
            }
            try {
                mBirthday = new GregorianCalendar();
                mBirthday.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } finally {
            a.recycle();
        }
        init();
        updateDisplyParam();
    }

    private void init() {
        int gray = getResources().getColor(R.color.gray);
        mPastPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPastPaint.setColor(gray);
        mPastPaint.setStyle(Paint.Style.FILL);
        mPresentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPresentPaint.setStyle(Paint.Style.FILL);
        mPresentPaint.setColor(getResources().getColor(R.color.orange));
        mFuturePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFuturePaint.setStyle(Paint.Style.STROKE);
        mFuturePaint.setColor(gray);
        mRectF = new RectF();
    }

    private void updateDisplyParam() {
        Calendar rightNow = Calendar.getInstance();
        int age = getAge(rightNow);
        Calendar year = new GregorianCalendar();
        year.setTime(new Date());
        year.set(Calendar.DAY_OF_MONTH, mBirthday.get(Calendar.DAY_OF_MONTH));
        year.set(Calendar.MONTH, mBirthday.get(Calendar.MONTH));

        int currentWeek =  (int) Math.ceil((rightNow.getTimeInMillis() - year.getTimeInMillis())/(1000*60*60*24*7.0));

        switch (mDisplay) {
            case CURRENT_YEAR:
                mTotalDots = 52;
                mCurrentDot = currentWeek;
                mDotsPerLine = 10;
                break;
            case LIFE:
            default:
                mTotalDots = mLifeExpectancy * 52 ;
                mCurrentDot = age * 52 + currentWeek;
                mDotsPerLine = 52;
                break;
        }
    }

    public void setLifeExpectancy(int lifeExpectancy) {
        this.mLifeExpectancy = lifeExpectancy;
        updateDisplyParam();
    }

    public void setBirthday(String birthday) {
        try {
            mBirthday.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        updateDisplyParam();
    }

    public void setDisplay(DISPLAY display) {
        if (display != mDisplay) {
            this.mDisplay = display;
            updateDisplyParam();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mRectF.set(0, 0, mDiameter, mDiameter);
        mRectF.offset(mSpacingH, mSpacingV);

        for(int i = 1; i <= mTotalDots; i++) {
            Paint paint;
            if (i < mCurrentDot) {
                paint = mPastPaint;
            } else if (i == mCurrentDot) {
                paint = mPresentPaint;
            } else {
                paint = mFuturePaint;
            }
            canvas.drawOval(mRectF, paint);
            if (i % mDotsPerLine == 0) {
                // new line
                mRectF.offsetTo(mSpacingH, (i / mDotsPerLine) * (mDiameter + mSpacingV) + mSpacingV);
            } else {
                // shift one right
                mRectF.offset(mDiameter + mSpacingH ,0);
            }
        }
    }

    private int getAge(Calendar rightNow) {
        int age;
        age = rightNow.get(Calendar.YEAR) - mBirthday.get(Calendar.YEAR);
        if(rightNow.get(Calendar.DAY_OF_YEAR) < mBirthday.get(Calendar.DAY_OF_YEAR))  {
            age-=1;
        }
        return age;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mDiameter = w*2/ 3 /mDotsPerLine;
        int spacing = w/ 3 /(mDotsPerLine + 1);

        int lines = mTotalDots / mDotsPerLine + 1;
        int drawHeight = lines*(mDiameter + spacing) +  spacing;

        if (drawHeight > h) {
            float diffPercent = (float)h / drawHeight;
            mDiameter = (int) (mDiameter * diffPercent);
            mSpacingV = (int) (spacing * diffPercent);
            mSpacingH = (w - mDotsPerLine * mDiameter) / (mDotsPerLine + 1);

        } else {
            mSpacingH = spacing;
            mSpacingV = (h - lines * mDiameter) / (lines + 1);
        }
    }
}
