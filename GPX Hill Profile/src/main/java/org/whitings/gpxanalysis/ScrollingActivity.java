package org.whitings.gpxanalysis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScrollingActivity extends AppCompatActivity {
    private GPXData gpxdata=null;
    final int REQUEST_CODE_LOAD_FILE = 100;
    final int REQUEST_CODE_UPDATE_PREFS = REQUEST_CODE_LOAD_FILE + 1;

    protected void loadGPX(Intent intent) {
        try {
            gpxdata = new GPXData(getContentResolver().openInputStream(intent.getData()));
            applyPrefs();
        } catch (Exception e) {
            System.out.println("Failed to parse gpx file");
            e.printStackTrace();
        }
    }

    private void showStats() {
        TextView stats = (TextView) findViewById(R.id.stats);
        stats.setText(gpxdata.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        if (intent != null && intent.getType()!=null) {
            if(intent.getAction().equals("android.intent.action.SEND")) {
                requestRemoteFile(intent);
            } else {
                loadGPX(intent);
            }
        }
    }

    private void requestRemoteFile(Intent intent) {
        String s=intent.getClipData().getItemAt(0).coerceToText(this).toString();
        TextView label = (TextView) findViewById(R.id.stats);
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            String url = "https://www.komoot.com/tour/"+matcher.group()+"/download";
            label.setText(url);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case REQUEST_CODE_LOAD_FILE:
                    loadGPX(data);
                    break;
                case REQUEST_CODE_UPDATE_PREFS:
                    if (gpxdata != null) applyPrefs();
                    break;
            }
    }

    private void applyPrefs() {
        gpxdata.setParams(PreferenceManager.getDefaultSharedPreferences(getBaseContext()));
        showStats();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivityForResult(new Intent(this, MyPreferencesActivity.class),
                        REQUEST_CODE_UPDATE_PREFS);
                return true;
            case R.id.load_file:
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a file"),
                        REQUEST_CODE_LOAD_FILE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
