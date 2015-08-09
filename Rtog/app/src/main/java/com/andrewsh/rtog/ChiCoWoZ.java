package com.andrewsh.rtog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Button;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;


public class ChiCoWoZ extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */

    private TabLayout tabLayout;
    private CategoryFragment[] frags;
    public Utterance[][] pickedUtts;
    private ArrayList<ArrayList<Utterance>> includedUtts;
    boolean isDia;
    private ViewPager vp;

    public static final int BUTTONS_PER_PAGE = 5;
    public static final String[] CATEGORIES =
        {"Questions", "Feedback", "Ideas"};
    private static final int[] INCLUDE_PAGES = {1};
    private static final boolean IS_DIA_INITIALLY = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // BACKGROUND LOGIC
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

        // VIEWS
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chicowoz);
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        final ActionBar ab = getSupportActionBar();

        vp = (ViewPager) findViewById(R.id.pager);
        if (vp != null) {
            setupViewPager(vp);
        }

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(vp);

        // initialize the buttons
        diaInitialize();
    }

    private void pickUtts() {
        // clean previous picked if any
        for (int i = 0; i < CATEGORIES.length; i++)
                pickedUtts[i] = new Utterance[BUTTONS_PER_PAGE];
        // check all categories to be sure there's enough
        for (int i = 0; i < CATEGORIES.length; i++) {
            if(includedUtts.get(i).size() < BUTTONS_PER_PAGE)
                new Exception().printStackTrace();
        }
        // pick new utts
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

    private void setupViewPager(ViewPager vp) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        for (int i = 0; i < CATEGORIES.length; i++) {
            frags[i] = new CategoryFragment();
            Bundle args = new Bundle(); args.putInt(CategoryFragment.POS_ARG, i);
            frags[i].setArguments(args);
            adapter.addFragment(frags[i], CATEGORIES[i]);
        }
        vp.setAdapter(adapter);

    }

    public void diaToggle(View view) {
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
        FragmentPagerAdapter adapter = (FragmentPagerAdapter)vp.getAdapter();
        ((CategoryFragment) adapter.getItem(vp.getCurrentItem())).updateButtons();
        /*
        for (int i = 0; i < frags.length; i++) {
            frags[i].updateButtons();
        }
        */
    }

    private void diaInitialize() {
        Button standardButton = (Button) findViewById(R.id.standard_button);
        Button dialectButton = (Button) findViewById(R.id.dialect_button);
        if (IS_DIA_INITIALLY) {
            dialectButton.setBackgroundColor(getResources().getColor(R.color.accent_material_dark));
            standardButton.setBackgroundColor(getResources().getColor(R.color.switch_thumb_disabled_material_dark));
        }
        else {
            standardButton.setBackgroundColor(getResources().getColor(R.color.accent_material_dark));
            dialectButton.setBackgroundColor(getResources().getColor(R.color.switch_thumb_disabled_material_dark));
        }
        isDia = IS_DIA_INITIALLY;
    }

    public class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            // Show total number of pages.
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            if (position < 0 || position >= CATEGORIES.length) {
                return null;
            }
            return mFragmentTitles.get(position).toUpperCase(l);
        }
    }

    /*
    public static class CategoryFragment extends Fragment {

        Button[] buttons;

        private static final String ARG_CATEGORY_NAME = "categoryName";

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
    */
}
