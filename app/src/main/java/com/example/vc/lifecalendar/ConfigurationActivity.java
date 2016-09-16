package com.example.vc.lifecalendar;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.vc.lifecalendar.util.PreferenceHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ConfigurationActivity extends Activity {
    int mAppWidgetId;
    int mLifeExpectancy;
    String mBirthday;
    int mDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration_activity);
        findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allInputValid()) {
                    initializeAppWidget();
                }
            }
        });
    }

    private boolean allInputValid() {
        int checkedRadioBtnId = ((RadioGroup) findViewById(R.id.display_method)).getCheckedRadioButtonId();
        if (checkedRadioBtnId == -1) {
            return false;
        }
        if (checkedRadioBtnId == R.id.radioButtonYear) {
            mDisplay = LifeCalendarView.DISPLAY_CURRENT_YEAR;
        } else if (checkedRadioBtnId == R.id.radioButtonLife) {
            mDisplay = LifeCalendarView.DISPLAY_LIFE;
        } else {
            return false;
        }

        try {
            mLifeExpectancy  = Integer.parseInt(((EditText) findViewById(R.id.life_expectancy)).getText().toString());
        } catch (NumberFormatException e) {
            return false;
        }
        if (mLifeExpectancy < 30 || mLifeExpectancy > 150) {
            return false;
        }
        mBirthday = ((EditText) findViewById(R.id.birthday)).getText().toString();
        try {
            Calendar birthdayCalendar = new GregorianCalendar();
            birthdayCalendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(mBirthday));
            if (birthdayCalendar.get(Calendar.YEAR) < 1900) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    private void initializeAppWidget(){
        int INVALID_APPWIDGET_ID = AppWidgetManager.INVALID_APPWIDGET_ID;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    INVALID_APPWIDGET_ID);
            Bundle bundle = PreferenceHelper.createPreferenceBundle(mLifeExpectancy, mBirthday, mDisplay);
            PreferenceHelper.setPreference(getApplicationContext(), mAppWidgetId, bundle);
        }
        if (mAppWidgetId == INVALID_APPWIDGET_ID) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        updateWidgetAndFinish();
    }
    private void updateWidgetAndFinish(){
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this, CalendarWidgetProvider.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {mAppWidgetId});
        sendBroadcast(intent);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
