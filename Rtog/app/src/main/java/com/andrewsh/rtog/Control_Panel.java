package com.andrewsh.rtog;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class Control_Panel extends Activity {

    private static final int[] INCLUDE_PAGES = {1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control__panel);
        readUtts();
        for (int page : INCLUDE_PAGES) {
            // put the utterances from the page into includedUtts
            includedUtts.addAll(uttConvert(uttWorkbook[page - 1]));
        }
        pickUtts();
        changeButtons(true);
    }

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
        ArrayList<Utterance> utts = new ArrayList<Utterance>();
        for (int i = 1; i < page.length; i++) {
            utts.add(new Utterance(legend, page[i]));
        }
        return utts;
    }

    private void changeButtons(boolean toStd) {
        buttons = new Button[5];
        buttons[0] = (Button) findViewById(R.id.b1);
        buttons[1] = (Button) findViewById(R.id.b2);
        buttons[2] = (Button) findViewById(R.id.b3);
        buttons[3] = (Button) findViewById(R.id.b4);
        buttons[4] = (Button) findViewById(R.id.b5);
        String utterance;
        for (int i = 0; i < 5; i++) {
            if (toStd) {
                utterance = pickedUtts.get(i).stdText;
            }
            else {
                utterance = pickedUtts.get(i).diaText;
            }
            buttons[i].setText(utterance.toCharArray(), 0, utterance.length());
        }
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
        boolean isStd = !((Switch) view).isChecked();

        changeButtons(isStd);
    }

    private Button[] buttons;
    private String[][][] uttWorkbook;
    private ArrayList<Utterance> includedUtts = new ArrayList<Utterance>();
    private ArrayList<Utterance> pickedUtts = new ArrayList<Utterance>();

    /*
    // this is sort of a workaround, the utterance list starts in /assets
    // and then gets moved to the files dir so it's super easy to work with
    private void fetchUtts() {
        if (Arrays.asList(this.getFilesDir().list()).contains("utts.txt"))
            return;
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
            uttsStr = "";
        }
        File file = new File(getFilesDir(), "utts.txt");
        PrintWriter pw;
        try {
            pw = new PrintWriter(file);
            pw.print(uttsStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
    /*
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
        }
        String[] uttsArr = uttsStr.split("\n");
        stdUtts = new String[uttsArr.length / 2];
        diaUtts = new String[uttsArr.length / 2];
        for (int i =  0; i < uttsArr.length / 2; i++) {
            stdUtts[i] = uttsArr[i * 2];
            diaUtts[i] = uttsArr[i * 2 + 1];
        }
    }
    */

    private String[][] parseCSV(String text) {
        String[] lines = text.split("\n");
        String[][] cells = new String[lines.length][];
        for (int i = 0; i < lines.length; i++) {
            String cell = "";
            boolean inQuotes = false;
            char[] line = lines[i].toCharArray();
            ArrayList<String> row = new ArrayList<String>();
            for (int j = 0; j < line.length; j++) {
                if (line[j] == '"') {
                    inQuotes = !inQuotes;
                }
                else if (line[j] == ',' && !inQuotes) {
                    row.add(cell);
                    cell = "";
                }
                else {
                    cell += line[j];
                }
            }
            cells[i] = row.toArray(new String[row.size()]);
        }
        return cells;
    }
}

