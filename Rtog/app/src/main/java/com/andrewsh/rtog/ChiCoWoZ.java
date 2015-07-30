package com.andrewsh.rtog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Button;
import android.support.design.widget.TabLayout;


public class ChiCoWoZ extends Activity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    TabLayout tabLayout;
    CategoryFragment[] frags;
    Utterance[][] pickedUtts;
    ArrayList<ArrayList<Utterance>> includedUtts;

    private static final int BUTTONS_PER_PAGE = 5;
    private static final String[] CATEGORIES =
        {"Questions", "Feedback", "Ideas"};
    private static final int[] INCLUDE_PAGES = {1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        frags = new CategoryFragment[CATEGORIES.length];
        pickedUtts = new Utterance[CATEGORIES.length][BUTTONS_PER_PAGE];
        includedUtts = new ArrayList<>();
        for (int i = 0; i < CATEGORIES.length; i++)
            includedUtts.add(new ArrayList<Utterance>());
        SheetReader sr = new SheetReader(this.getAssets(), "utts");
        ArrayList<ArrayList<Utterance>> pageUtts;
        for ( int pageNum : INCLUDE_PAGES ) {
            pageUtts = sr.readSheet(pageNum - 1, CATEGORIES);
            for ( int i = 0; i < CATEGORIES.length; i++ ) {
                includedUtts.get(i).addAll(pageUtts.get(i));
            }
        }
        pickUtts();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chicowoz);


        tabLayout = new TabLayout(this);
        for ( String category : CATEGORIES ) {
            tabLayout.addTab(tabLayout.newTab().setText(category));
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            tabLayout.addTab(
                    tabLayout.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i)));
        }
        diaToggle(findViewById(R.id.standard_button));
    }

    private void pickUtts() {
        // first, check all categories to be sure there's enough
        for (int i = 0; i < CATEGORIES.length; i++) {
            if(includedUtts.get(i).size() < BUTTONS_PER_PAGE)
                new Exception().printStackTrace();
        }
        Random ran = new Random();
        for (int i = 0; i < CATEGORIES.length; i++) {
            Utterance pick;
            for ( int j = 0; j < BUTTONS_PER_PAGE; j++ ) {
                do {
                    pick = includedUtts.get(i).get(ran.nextInt(includedUtts.get(i).size()));
                } while (Arrays.asList(pickedUtts[i]).contains(pick));
                pickedUtts[i][j] = pick;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chicowoz, menu);
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

    public void diaToggle(View view) {
        boolean isDia;
        Button standardButton = (Button) findViewById(R.id.standard_button);
        Button dialectButton = (Button) findViewById(R.id.dialect_button);
        if (view.getId() == R.id.standard_button) {
            standardButton.setBackgroundColor(getResources().getColor(R.color.accent_material_dark));
            dialectButton.setBackgroundColor(getResources().getColor(R.color.switch_thumb_disabled_material_dark));
            isDia = false;
        }
        else {
            dialectButton.setBackgroundColor(getResources().getColor(R.color.accent_material_dark));
            standardButton.setBackgroundColor(getResources().getColor(R.color.switch_thumb_disabled_material_dark));
            isDia = true;
        }
        for (int i = 0; i < frags.length; i++) {
            frags[i].changeButtons(isDia, pickedUtts[i]);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment newFrag = CategoryFragment.newInstance(CATEGORIES[position]);
            frags[position] = (CategoryFragment) newFrag;
            return newFrag;
        }

        @Override
        public int getCount() {
            // Show total number of pages.
            return CATEGORIES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            if (position < 0 || position >= CATEGORIES.length) {
                return null;
            }
            return CATEGORIES[position].toUpperCase(l);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class CategoryFragment extends Fragment {

        Button[] buttons;

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_CATEGORY_NAME = "categoryName";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static CategoryFragment newInstance(String categoryName) {
            CategoryFragment fragment = new CategoryFragment();
            Bundle args = new Bundle();
            args.putString(ARG_CATEGORY_NAME, categoryName);
            fragment.setArguments(args);
            return fragment;
        }

        public CategoryFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_chicowoz, container, false);
            LinearLayout root = (LinearLayout) rootView.findViewById(R.id.fragmentRoot);
            buttons = new Button[BUTTONS_PER_PAGE];
            String textDef = "THIS BUTTON IS BLANK!";
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            for ( Button button : buttons ) {
                button = new Button(getActivity());
                button.setText(textDef.toCharArray(), 0, textDef.length());
                button.setLayoutParams(params);
                // TODO: set onClick listener
                root.addView(button);
            }
            return rootView;
        }

        public void changeButtons(boolean isDia, Utterance[] picked) {
            String utterance;
            for (int i = 0; i < buttons.length; i++) {
                if (isDia) {
                    utterance = picked[i].diaText;
                }
                else {
                    utterance = picked[i].stdText;
                }
                buttons[i].setText(utterance.toCharArray(), 0, utterance.length());
            }
        }
    }
}
