package com.andrewsh.rtog;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class Control_Panel extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control__panel);
        readUtts();
        // pick first utterances
        // store them
        // set std or dialect
        // populate buttons
    }

    private void readUtts() {
        String uttsStr = null;
        try {
            InputStream is = getAssets().open("utts.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            uttsStr = new String(buffer, "UTF-8");
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }
        String[] uttsArr = uttsStr.split("\n");
        for(int i = 0; i < uttsArr.length/2; i++) {
            stdUtts[i] = uttsArr[i*2];
            dialectUtts[i] = uttsArr[i*2 + 1];
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_control__panel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void dialectToggleClick(View view) {
        // is it standard?
        boolean isStd = ((Switch) view).isChecked();

        if (isStd) {
            // change to standard
        }
        else {
            // change to dialect
        }
    }

    private String[] dialectUtts;
    private String[] stdUtts;
}
