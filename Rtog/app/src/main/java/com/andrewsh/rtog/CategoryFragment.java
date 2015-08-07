package com.andrewsh.rtog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import static junit.framework.Assert.assertTrue;


public class CategoryFragment extends Fragment {

    public Button[] buttons = new Button[ChiCoWoZ.BUTTONS_PER_PAGE];
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_category, container, false);
        LinearLayout linearRoot = (LinearLayout) root;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        String textDef = "THIS BUTTON IS BLANK!";
        for (int i = 0; i < ChiCoWoZ.BUTTONS_PER_PAGE; i++) {
            buttons[i] = new Button(getActivity());
            buttons[i].setText(textDef.toCharArray(), 0, textDef.length());
            buttons[i].setLayoutParams(params);
            linearRoot.addView(buttons[i]);
        }
        return root;
    }

    public void changeButtons(String[] buttonTexts) {
        assertTrue(buttonTexts.length == ChiCoWoZ.BUTTONS_PER_PAGE);
        for (int i = 0; i < ChiCoWoZ.BUTTONS_PER_PAGE; i++) {
            buttons[i].setText(buttonTexts[i]);
        }
    }
}
