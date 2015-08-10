package com.andrewsh.rtog;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;


public class CategoryFragment extends Fragment {

    public Button[] buttons = new Button[ChiCoWoZ.BUTTONS_PER_PAGE];
    public int position;
    public static final String POS_ARG = "POSITION";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle b = getArguments();
        position = b.getInt(POS_ARG);
        View root = inflater.inflate(R.layout.fragment_category, container, false);
        LinearLayout linearRoot = (LinearLayout) root;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0, 1f);
        String textDef = "THIS BUTTON IS BLANK!";
        for (int i = 0; i < ChiCoWoZ.BUTTONS_PER_PAGE; i++) {
            buttons[i] = new Button(getActivity());
            buttons[i].setText(textDef.toCharArray(), 0, textDef.length());
            buttons[i].setLayoutParams(params);
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pressed = Arrays.asList(buttons).indexOf(v);
                    mCallback.onButton(position, pressed);
                }
            });
            linearRoot.addView(buttons[i]);
        }
        updateButtons();
        return root;
    }

    public void updateButtons() {
        Utterance[][] picked = ((ChiCoWoZ) getActivity()).pickedUtts;
        boolean isDia = ((ChiCoWoZ) getActivity()).isDia;
        assertEquals(picked[position].length, ChiCoWoZ.BUTTONS_PER_PAGE);
        String[] buttonTexts = new String[ChiCoWoZ.BUTTONS_PER_PAGE];
        for (int i = 0; i < ChiCoWoZ.BUTTONS_PER_PAGE; i++) {
            buttonTexts[i] = isDia ? picked[position][i].diaText : picked[position][i].stdText;
        }
        for (int i = 0; i < ChiCoWoZ.BUTTONS_PER_PAGE; i++) {
            buttons[i].setText(buttonTexts[i]);
        }
    }

    // this is for sending the button events back to the activity
    OnButtonListener mCallback;

    public interface OnButtonListener {
        public void onButton(int pane, int position);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnButtonListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnButtonListener");
        }
    }

    // this is all a little hacky but makes it so that the fragment is always
    // updated when it's selected
    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getUserVisibleHint()) {
            return;
        }

        updateButtons();
    }
}
