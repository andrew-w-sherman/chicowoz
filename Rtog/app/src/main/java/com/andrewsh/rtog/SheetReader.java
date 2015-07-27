package com.andrewsh.rtog;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Andrew on 7/24/15.
 */
public class SheetReader {
    private AssetManager am;
    private String[][][] uttWorkbook;

    public SheetReader(AssetManager callerAssets, String folderPath) {
        am = callerAssets;
        uttWorkbook = readUtts(folderPath);
    }

    public ArrayList<ArrayList<Utterance>> readSheet
            (int sheetNum, String[] categories) {
        String[][] uttSheet = uttWorkbook[sheetNum];
        ArrayList<Utterance> uttList = new ArrayList<>();
        String[] legend = uttSheet[0];
        // build basic list
        for (int i = 1; i < uttSheet.length; i++) {
            uttList.add(new Utterance(legend, uttSheet[i]));
        }
        // categorize and return
        ArrayList<ArrayList<Utterance>> uttCat = new ArrayList<>();
        for (Utterance utt : uttList) {
            int catIndex = Arrays.asList(categories).indexOf(utt.category);
            uttCat.get(catIndex).add(utt);
        }
        return uttCat;
    }

    private String[][][] readUtts(String folderPath) {
        String[] files = new String[0];
        // get file list from assets/utts
        try {
            files = am.list(folderPath);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: exception handling
        }
        String[][][] uttWB = new String[files.length][][];
        // get the files and parse them into a big 3d array
        for (int i = 0; i < files.length; i++) {
            try {
                String path = folderPath + File.separator + (i + 1) + ".csv";
                InputStream is = am.open(path);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                uttWB[i] = parseCSV(new String(buffer, "UTF-8"));
            }
            catch (IOException e) {
                e.printStackTrace();
                // TODO: exception handling
            }
        }
        return uttWB;
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
