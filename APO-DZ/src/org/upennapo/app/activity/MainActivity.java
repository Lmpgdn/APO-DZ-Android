package org.upennapo.app.activity;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.upennapo.app.R;
import org.upennapo.app.fragment.BrotherStatusFragment;
import org.upennapo.app.fragment.DirectoryFragment;
import org.upennapo.app.fragment.LinkListFragment;
import org.upennapo.app.fragment.WebFragment;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    public static final int NUM_TABS = 5;
    private static final int NUM_TAPS_ACTIVATE = 10;
    private static final String EASTER_EGG_UNLOCKED = "2048_UNLOCKED";
    private static final String TAG = "MainActivity";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    /**
     * Used to count the number of times the Helpful Links tab has been selected.
     * When mNumTaps == NUM_TAPS_ACTIVATE, trigger the easter egg activity.
     */
    private int mNumTaps = 0;

    /**
     * Get the corresponding unselected tab icon ID for a tab.
     *
     * @param position for which to get the tab icon
     * @return the unselected tab icon's resource ID for the tab at position
     */
    protected static int getPageIcon(int position) {
        int iconID = 0;
        switch (position) {
            case 0:
                // Brother Status
                iconID = R.drawable.ic_tab_user;
                break;
            case 1:
                // Calendar
                iconID = R.drawable.ic_tab_calendar_day;
                break;
            case 2:
                // Brother Directory
                iconID = R.drawable.ic_tab_people;
                break;
            case 3:
                // Pledge Directory
                iconID = R.drawable.ic_action_grow;
                break;
            case 4:
                // Helpful Links
                iconID = R.drawable.ic_tab_bookmark;
                break;
        }
        return iconID;
    }

    /**
     * Get the corresponding selected tab icon ID for a tab.
     *
     * @param position for which to get the tab icon
     * @return the selected tab icon's resource ID for the tab at position
     */
    protected static int getSelectedPageIcon(int position) {
        int iconID = 0;
        switch (position) {
            case 0:
                // Brother Status
                iconID = R.drawable.ic_tab_user_selected;
                break;
            case 1:
                // Calendar
                iconID = R.drawable.ic_tab_calendar_day_selected;
                break;
            case 2:
                // Brother Directory
                iconID = R.drawable.ic_tab_people_selected;
                break;
            case 3:
                // Pledge Directory
                iconID = R.drawable.ic_action_grow_selected;
                break;
            case 4:
                // Helpful Links
                iconID = R.drawable.ic_tab_bookmark_selected;
                break;
        }
        return iconID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(NUM_TABS - 1);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setIcon(getPageIcon(i))
                            .setTabListener(this)
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Enable the 2048 option if unlocked.
        SharedPreferences prefs = getSharedPreferences(getString(R.string.app_global_storage_key), MODE_PRIVATE);
        if (prefs.getBoolean(EASTER_EGG_UNLOCKED, false)) {
            menu.findItem(R.id.menu_play_2048).setVisible(true);
        }

        // Disable switch mode feature for active brothers.
        try {
            final String appVersion =
                    getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            boolean isTestingMode = appVersion.endsWith("-DEBUG") || appVersion.endsWith("-beta");
            if (!(isTestingMode || LoginActivity.isAlumLoggedIn(this))) {
                menu.findItem(R.id.menu_switch_mode).setVisible(false);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.v(TAG, e.getMessage());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.menu_play_2048:
                Intent play2048 = new Intent(this, EasterEggActivity.class);
                startActivity(play2048);
                return true;

            case R.id.menu_about_app:
                final String githubPageUrl = getString(R.string.menu_about_app_url);
                Intent openGithubPage = new Intent(Intent.ACTION_VIEW, Uri.parse(githubPageUrl));
                startActivity(openGithubPage);
                return true;

            case R.id.menu_switch_mode:
                Intent switchMode = new Intent(this, AlumModeActivity.class);
                startActivity(switchMode);
                finish();
                return true;

            case R.id.menu_switch_user:
                Intent openLoginScreen = new Intent(this, LoginActivity.class);
                openLoginScreen.putExtra(LoginActivity.LOGOUT_INTENT, true);
                startActivity(openLoginScreen);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        int position = tab.getPosition();

        // Whenever the Helpful Links tab is selected, we edge closer to activating the Easter Egg!
        if (position == 4) {
            updateEasterEggStatus();
        }

        setTitle(mSectionsPagerAdapter.getPageTitle(position));
        tab.setIcon(getSelectedPageIcon(position));

        mViewPager.setCurrentItem(position);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        int position = tab.getPosition();
        tab.setIcon(getPageIcon(position));
        mViewPager.setCurrentItem(position);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * Increments the number of times we've entered the last tab. If it reaches the requisite number,
     * we unlock the easter egg.
     */
    private void updateEasterEggStatus() {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.app_global_storage_key), MODE_PRIVATE);
        if (++mNumTaps == NUM_TAPS_ACTIVATE && !prefs.contains(EASTER_EGG_UNLOCKED)) {

            // Remember that we've unlocked the easter egg.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(EASTER_EGG_UNLOCKED, true);
            editor.apply();

            invalidateOptionsMenu();

            // Show user unlock message.
            Toast t = Toast.makeText(this, R.string.apo_2048_unlock_msg, Toast.LENGTH_LONG);
            t.show();
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        public DummySectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.work_in_progress_view, container, false);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        FragmentManager mManager;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            mManager = fm;
        }


        @Override
        public Fragment getItem(int position) {
            // For Fragments with AsyncTasks, attempt to retrieve the retained Fragment. If this
            // isn't possible, use the FragmentManager to attach it to a tag.
            switch (position) {
                case 0:
                    // Brother Status
                    return BrotherStatusFragment.newInstance(MainActivity.this);

                case 1:
                    // Calendar WebView
                    return WebFragment.newCalendarInstance(MainActivity.this);

                case 2:
                    // Brother Directory
                    return DirectoryFragment.newBrotherDirectoryInstance(MainActivity.this);

                case 3:
                    // Pledge Directory
                    return DirectoryFragment.newPledgeDirectoryInstance(MainActivity.this);

                case 4:
                    // Helpful Links
                    return LinkListFragment.newBrotherLinksInstance(MainActivity.this);

                default:
                    // getItem is called to instantiate the fragment for the given page.
                    // Return a DummySectionFragment (defined as a static inner class
                    // below) with the page number as its lone argument.
                    return new DummySectionFragment();
            }
        }

        @Override
        public int getCount() {
            // Show total number of pages.
            return NUM_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String sectionName = "";
            switch (position) {
                case 0:
                    // Brother Status
                    sectionName = getString(R.string.title_section1);
                    break;
                case 1:
                    // Calendar
                    sectionName = getString(R.string.title_section2);
                    break;
                case 2:
                    // Brother Directory
                    sectionName = getString(R.string.title_section3);
                    break;
                case 3:
                    // Pledge Directory
                    sectionName = getString(R.string.title_section4);
                    break;
                case 4:
                    // Helpful Links
                    sectionName = getString(R.string.title_section5);
                    break;
            }
            return sectionName;
        }
    }

}
