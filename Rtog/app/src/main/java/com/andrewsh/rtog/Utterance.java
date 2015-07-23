/**
 * Created by Andrew on 7/17/15.
 */

package com.andrewsh.rtog;

import java.util.HashMap;

public class Utterance {
    private static final boolean LONG_COMMAND_NAMES = true;

    public HashMap<String, String> baseDict = new HashMap<>();

    public String diaText;
    public String stdText;
    public String rootName;
    public String gesture;
    /*
    public String args;
    public String aaeArgs;

    public boolean feedbackTest;
    public boolean atTest;
    public boolean socialTest;
    public boolean taskIntroTest;
    public boolean preppingForPresentationTest;
    public boolean presentationModeTest;
    public boolean socialTalkTest;
    public boolean accountableTalkTest;
    public boolean onTaskSentencesTest;
    public boolean dialectUseTest;
    */

    public Utterance(String[] legend, String[] row) {
        // put everything in a hashmap, for debugging and convenience
        for (int i = 0; i < legend.length; i++) {
            baseDict.put(legend[i], row[i]);
        }

        // TODO: get all the other information out of the dict somehow?
        rootName = baseDict.get("RootName");
        stdText = baseDict.get("SBE_Utterance_Analysis");
        diaText = baseDict.get("AAVE_Utterance_BML");
        gesture = baseDict.get("Gesture");
        /*
        args = row[5];
        aaeArgs = row[6];

        feedbackTest = trueCell(row[7]);
        atTest = trueCell(row[8]);
        socialTest = trueCell(row[9]);
        taskIntroTest = trueCell(row[10]);
        preppingForPresentationTest = trueCell(row[11]);
        presentationModeTest = trueCell(row[12]);
        socialTalkTest = trueCell(row[13]);
        accountableTalkTest = trueCell(row[14]);
        onTaskSentencesTest = trueCell(row[15]);
        dialectUseTest = trueCell(row[16]);
        */

    }

    public String commandName(boolean isStd) {
        if(LONG_COMMAND_NAMES)
            return getLongName(stdText, !isStd);
        if(isStd)
            return rootName + "-s";
        return rootName + "-v";
    }

    private static String getLongName(String text, boolean aae) {
        return text.replaceAll("[^a-zA-Z ]", "").replace(' ', '_') +
                (aae ? "_aae" : "");
    }

    /*
    private boolean trueCell(String cell) {
        if (cell == "") {
            return false;
        }
        return true;
    }
    */
}
