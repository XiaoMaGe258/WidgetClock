package com.pb.app.widgetclock;

import android.Manifest;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.TwoStatePreference;

import com.jaredrummler.android.colorpicker.ColorPreferenceCompat;

public class SettingsActivity extends AppCompatActivity implements WidgetUpdatedInterface {

    int appWidgetId;
    private WidgetViewCreator widgetViewCreator;


    FrameLayout preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        preview = findViewById(R.id.preview_view);

        widgetViewCreator = new WidgetViewCreator(this, this);

        setupPreviewFrame();
        widgetSetup();

        findViewById(R.id.tv_save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); //thats enough. finishing this activity will activate the widget
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sp_main), MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(widgetViewCreator);
        widgetViewCreator.onSharedPreferenceChanged(sharedPreferences, "");
    }

    @Override
    public void onPause() {
        super.onPause();
        getSharedPreferences(getString(R.string.sp_main), MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(widgetViewCreator);
    }

    @Override
    public void widgetDataUpdated() {
        widgetSetup();
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.sp_main), MODE_PRIVATE);

            ColorPreferenceCompat colorPreference = findPreference(getString(R.string.sp_clock_color));
            colorPreference.setDefaultValue(sharedPref.getInt(getString(R.string.sp_clock_color), Color.WHITE));
            colorPreference.setOnPreferenceChangeListener(listener);
            TwoStatePreference show_time = findPreference(getString(R.string.sp_show_time));
            show_time.setChecked(sharedPref.getBoolean(getString(R.string.sp_show_time), true));
            show_time.setOnPreferenceChangeListener(listener);
            EditTextPreference timeFormat = findPreference(getString(R.string.sp_time_format));
            timeFormat.setOnPreferenceChangeListener(listener);

            ListPreference fontList = findPreference(getString(R.string.sp_font));
            fontList.setOnPreferenceChangeListener(listener);
            SeekBarPreference clockSize = findPreference(getString(R.string.sp_time_size));
            clockSize.setOnPreferenceChangeListener(listener);
            ListPreference timeAlign = findPreference(getString(R.string.sp_time_align));
            timeAlign.setOnPreferenceChangeListener(listener);

            ListPreference dateAlign = findPreference(getString(R.string.sp_date_align));
            dateAlign.setOnPreferenceChangeListener(listener);
            SeekBarPreference dateSize = findPreference(getString(R.string.sp_date_size));
            dateSize.setOnPreferenceChangeListener(listener);
            ColorPreferenceCompat dateColorPreference = findPreference(getString(R.string.sp_date_color));
            dateColorPreference.setDefaultValue(sharedPref.getInt(getString(R.string.sp_date_color), Color.WHITE));
            dateColorPreference.setOnPreferenceChangeListener(listener);
            TwoStatePreference show_date = findPreference(getString(R.string.sp_show_date));
            show_date.setChecked(sharedPref.getBoolean(getString(R.string.sp_show_date), true));
            show_date.setOnPreferenceChangeListener(listener);
            EditTextPreference dateFormat = findPreference(getString(R.string.sp_date_format));
            dateFormat.setOnPreferenceChangeListener(listener);

            ListPreference widgetOrientation = findPreference(getString(R.string.sp_layout));
            widgetOrientation.setOnPreferenceChangeListener(listener);
        }

        Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.sp_main), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                if (getString(R.string.sp_clock_color).equals(preference.getKey()))
                    editor.putInt(getString(R.string.sp_clock_color), (int) newValue);
                if (getString(R.string.sp_show_time).equals(preference.getKey()))
                    editor.putBoolean(getString(R.string.sp_show_time), (boolean) newValue);
                if (getString(R.string.sp_font).equals(preference.getKey()))
                    editor.putString(getString(R.string.sp_font), (String) newValue);
                if (getString(R.string.sp_time_format).equals(preference.getKey()))
                    editor.putString(getString(R.string.sp_time_format), (String) newValue);
                if (getString(R.string.sp_time_size).equals(preference.getKey()))
                    editor.putInt(getString(R.string.sp_time_size), (int) newValue);
                if (getString(R.string.sp_time_align).equals(preference.getKey()))
                    editor.putInt(getString(R.string.sp_time_align), Integer.parseInt((String) newValue));
                if (getString(R.string.sp_date_align).equals(preference.getKey()))
                    editor.putInt(getString(R.string.sp_date_align), Integer.parseInt((String) newValue));
                if (getString(R.string.sp_date_size).equals(preference.getKey()))
                    editor.putInt(getString(R.string.sp_date_size), (int) newValue);
                if (getString(R.string.sp_date_color).equals(preference.getKey()))
                    editor.putInt(getString(R.string.sp_date_color), (int) newValue);
                if (getString(R.string.sp_show_date).equals(preference.getKey()))
                    editor.putBoolean(getString(R.string.sp_show_date), (boolean) newValue);
                if (getString(R.string.sp_date_format).equals(preference.getKey()))
                    editor.putString(getString(R.string.sp_date_format), (String) newValue);
                if (getString(R.string.sp_layout).equals(preference.getKey()))
                    editor.putInt(getString(R.string.sp_layout), Integer.parseInt((String) newValue));


                editor.apply();
                return true;
            }
        };

    }

    private void setupPreviewFrame() {
        ImageView preview = findViewById(R.id.bg);
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
            return;
        }
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        preview.setImageDrawable(wallpaperDrawable);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        setupPreviewFrame(); //thats here to setup the preview window after permission to fetch the user wallpaper has being approved
    }

    private void widgetSetup() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());

        RemoteViews views = widgetViewCreator.createWidgetRemoteView();
        preview.removeAllViews();
        View previewView = views.apply(this, preview);
        preview.addView(previewView);

        appWidgetManager.updateAppWidget(appWidgetId, views);
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);
    }

}