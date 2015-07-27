package com.andrewsh.rtog;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Control_Panel_old extends Activity {

    private static final int[] INCLUDE_PAGES = {1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control__panel_old);
        makeButtons();
        client = new WoZClient(PreferenceManager
                .getDefaultSharedPreferences(this));
        // TODO: change this to a settings-based system
        readUtts();
        for (int page : INCLUDE_PAGES) {
            // put the utterances from the page into includedUtts
            includedUtts.addAll(uttConvert(uttWorkbook[page - 1]));
        }
        pickUtts();
        changeButtons(true);
    }

    private void makeButtons() {
        buttons = new Button[5];
        final LinearLayout root = (LinearLayout) findViewById(R.id.linearMain);
        String textDef = getString(R.string.button_default);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        for (int i = 0; i < buttons.length; i++) {
            Button button = new Button(this);
            button.setText(textDef.toCharArray(), 0, textDef.length());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonClick(v);
                }
            });
            button.setLayoutParams(params);
            buttons[i] = button;
            root.addView(button);
        }
    }

    private void changeButtons(boolean toStd) {
        String utterance;
        for (int i = 0; i < buttons.length; i++) {
            if (toStd) {
                utterance = pickedUtts.get(i).stdText;
            }
            else {
                utterance = pickedUtts.get(i).diaText;
            }
            buttons[i].setText(utterance.toCharArray(), 0, utterance.length());
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void dialectToggleClick(View view) {
        // is it standard?
        boolean isStd = !((Switch) view).isChecked();

        changeButtons(isStd);
    }

    public void buttonClick(View view) {
        boolean isStd = !((Switch) findViewById(R.id.dialecttoggle)).isChecked();

        // not sure if this will find it properly
        int clickedButton = Arrays.asList(buttons).indexOf(view);
        Utterance pickedUtt = pickedUtts.get(clickedButton);
        String audioName = pickedUtt.commandName(isStd);

        client.sendCommand(audioName);
    }

    private WoZClient client;
    private Button[] buttons;
    private String[][][] uttWorkbook;
    private ArrayList<Utterance> includedUtts = new ArrayList<>();
    private ArrayList<Utterance> pickedUtts = new ArrayList<>();

    private void pickUtts() {
        Random ran = new Random();
        int index;
        for (int i = 0; i < 5; i++) {
            do {
                index = ran.nextInt(includedUtts.size());
            } while (pickedUtts.contains(includedUtts.get(index)));
            pickedUtts.add(includedUtts.get(index));
        }
    }

    private ArrayList<Utterance> uttConvert(String[][] page) {
        // TODO: this will assume that the first row is a legend
        // should add some sort of test perhaps?
        String[] legend = page[0];
        ArrayList<Utterance> utts = new ArrayList<>();
        for (int i = 1; i < page.length; i++) {
            utts.add(new Utterance(legend, page[i]));
        }
        return utts;
    }

    private void readUtts() {
        String[] files = new String[0];
        AssetManager am = getAssets();
        // get file list from assets/utts
        try {
            files = am.list("utts");
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: exception handling
        }
        uttWorkbook = new String[files.length][][];
        // get the files and parse them into a big 3d array
        for (int i = 0; i < files.length; i++) {
            try {
                String path = "utts" + File.separator + files[i];
                InputStream is = am.open(path);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                uttWorkbook[i] = parseCSV(new String(buffer, "UTF-8"));
            }
            catch (IOException e) {
                e.printStackTrace();
                // TODO: exception handling
            }
        }
    }

    private String[][] parseCSV(String text) {
        String[] lines = text.split("\n");
        String[][] cells = new String[lines.length][];
        for (int i = 0; i < lines.length; i++) {
            String cell = "";
            boolean inQuotes = false;
            char[] line = lines[i].toCharArray();
            ArrayList<String> row = new ArrayList<>();
            for (char c : line) {
                if (c == '"') {
                    inQuotes = !inQuotes;
                } else if (c == ',' && !inQuotes) {
                    row.add(cell);
                    cell = "";
                } else {
                    cell += c;
                }
            }
            cells[i] = row.toArray(new String[row.size()]);
        }
        return cells;
    }
}

