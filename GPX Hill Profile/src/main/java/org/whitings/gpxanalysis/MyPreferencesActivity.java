package org.whitings.gpxanalysis;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.BaseAdapter;

import java.util.prefs.PreferenceChangeListener;

/**
 * Created by whitingpt on 4/26/17.
 */

public class MyPreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().clear().commit();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        setResult(RESULT_OK);

    }



    public static class MyPreferenceFragment extends PreferenceFragment {
        SharedPreferences.OnSharedPreferenceChangeListener listener;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            SharedPreferences prefs=getPreferenceManager().getSharedPreferences();
            updateSummary(prefs,"min_grade");
            updateSummary(prefs,"min_gain");
            updateSummary(prefs,"start_grade");
            updateSummary(prefs,"min_length");
            listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                    updateSummary(getPreferenceManager().getSharedPreferences(),key);
                }
            };
            prefs.registerOnSharedPreferenceChangeListener(listener);
        }

        private void updateSummary(SharedPreferences prefs, String key) {
            String format="%s";
            switch(key) {
                case "min_grade":
                    format = (String) getResources().getText(R.string.min_grade_summary);
                    break;
                case "min_gain":
                    format = (String) getResources().getText(R.string.min_gain_summary);
                    break;
                case "start_grade":
                    format = (String) getResources().getText(R.string.start_grade_summary);
                    break;
                case "min_length":
                    format = (String) getResources().getText(R.string.min_len_summary);
                    break;
                default:
                    return;
            }
            findPreference(key).setSummary(String.format(format,prefs.getString(key,null)));
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if(preference.getKey().equals("Reset")) {
                PreferenceManager.getDefaultSharedPreferences(preferenceScreen.getContext()).edit().clear().apply();
                PreferenceManager.setDefaultValues(preferenceScreen.getContext(), R.xml.preferences, true);
                getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);

        }
    }
}
