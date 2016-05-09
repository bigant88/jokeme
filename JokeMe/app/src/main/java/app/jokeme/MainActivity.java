package app.jokeme;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.startapp.android.publish.Ad;
import com.startapp.android.publish.AdDisplayListener;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String STARTAPP_ID = "204229082";
    private static final String TAG = MainActivity.class.getName();
    private static final String JOKE_LAST_POSITION = "JOKE_LAST_POSITION";
    private static final String QUIZ_LAST_POSITION = "QUIZ_LAST_POSITION";
    @Bind(R.id.id_setting_layout)
    View mViewSettingLayout;
    @Bind(R.id.id_view_pager_layout)
    View mViewPagerLayout;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    @Bind(R.id.view_pager_indicator)
    TitlePageIndicator mViewPagerIndicator;

    private MyDatabase database;
    private List<JokeModel> mJokeModelList;
    private List<JokeModel> mQuizModelList;
    private ArrayList<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();
    private AHBottomNavigation bottomNavigation;
    private FloatingActionButton floatingActionButton;
    private JokePagerAdapter jokePagerAdapter;
    private RawFileReader rawFileReader;
    private JokePagerPageChangeListener mJokePagerPageChangeListener;
    private QuizPagerPageChangeListener mQuizPagerPageChangeListener;
    private int selectedJokePageIndex, selectedQuizPageIndex;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private StartAppAd startAppAd;
    private int numberOfPageSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPref.edit();
        StartAppSDK.init(this, STARTAPP_ID, true);
        startAppAd = new StartAppAd(this);
        restorePostionOfViewPager();
        ButterKnife.bind(this);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        setOnFABClick();
        initUI();
        jokePagerAdapter = new JokePagerAdapter(getApplicationContext());
        mViewPager.setOffscreenPageLimit(10);
        mJokePagerPageChangeListener = new JokePagerPageChangeListener();
        mQuizPagerPageChangeListener = new QuizPagerPageChangeListener();
        new CopyDataTask().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAppAd.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        startAppAd.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        savePositionOfViewPager();
    }

    private void restorePostionOfViewPager() {
        selectedJokePageIndex = mPref.getInt(JOKE_LAST_POSITION, 0);
        selectedQuizPageIndex = mPref.getInt(QUIZ_LAST_POSITION, 0);
    }

    private void savePositionOfViewPager() {
        mEditor.putInt(JOKE_LAST_POSITION, selectedJokePageIndex);
        mEditor.putInt(QUIZ_LAST_POSITION, selectedQuizPageIndex);
        mEditor.commit();
    }

    private void setOnFABClick() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JokeModel quizModel = mQuizModelList.get(selectedQuizPageIndex);
                Snackbar.make(view, "" + quizModel.getAnswer(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    private void setupJokePager() {
        mViewPagerLayout.setVisibility(View.VISIBLE);
        mViewSettingLayout.setVisibility(View.GONE);
        jokePagerAdapter.setJokeModelList(mJokeModelList);
        mViewPager.setAdapter(jokePagerAdapter);
        mViewPagerIndicator.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(mJokePagerPageChangeListener);
        mViewPager.removeOnPageChangeListener(mQuizPagerPageChangeListener);
        mViewPager.setCurrentItem(selectedJokePageIndex, true);
    }

    private void setupQuizPager() {
        mViewPagerLayout.setVisibility(View.VISIBLE);
        mViewSettingLayout.setVisibility(View.GONE);
        jokePagerAdapter.setJokeModelList(mQuizModelList);
        mViewPager.setAdapter(jokePagerAdapter);
        mViewPagerIndicator.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(mQuizPagerPageChangeListener);
        mViewPager.removeOnPageChangeListener(mJokePagerPageChangeListener);
        mViewPager.setCurrentItem(selectedQuizPageIndex, true);
    }

    /**
     * Init UI
     */
    private void initUI() {
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.jokes, R.drawable.smiley, R.color.color_tab_1);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.quizzes, R.drawable.question_mark, R.color.color_tab_2);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.more, R.drawable.ic_apps_black_24dp, R.color.color_tab_3);
        //
        bottomNavigationItems.add(item1);
        bottomNavigationItems.add(item2);
        bottomNavigationItems.add(item3);
        //
        bottomNavigation.addItems(bottomNavigationItems);
        bottomNavigation.setAccentColor(Color.parseColor("#F63D2B"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));
        bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#F63D2B"));

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, boolean wasSelected) {
                Log.i(TAG, "onTabSelected " + position + "  " + wasSelected);
                switch (position) {
                    case 0:
                        if (!wasSelected) {
                            setupJokePager();
                        }
                        break;
                    case 1:
                        if (!wasSelected) {
                            setupQuizPager();
                        }
                        break;
                    case 2:
                        mViewPagerLayout.setVisibility(View.GONE);
                        mViewSettingLayout.setVisibility(View.VISIBLE);
                        break;
                }

                if (position == 1) {

                    if (!wasSelected) {
                        floatingActionButton.setVisibility(View.VISIBLE);
                        floatingActionButton.setAlpha(0f);
                        floatingActionButton.setScaleX(0f);
                        floatingActionButton.setScaleY(0f);
                        floatingActionButton.animate()
                                .alpha(1)
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(300)
                                .setInterpolator(new OvershootInterpolator())
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        floatingActionButton.animate()
                                                .setInterpolator(new LinearOutSlowInInterpolator())
                                                .start();
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                })
                                .start();
                    }
                } else {
                    if (floatingActionButton.getVisibility() == View.VISIBLE) {
                        floatingActionButton.animate()
                                .alpha(0)
                                .scaleX(0)
                                .scaleY(0)
                                .setDuration(300)
                                .setInterpolator(new LinearOutSlowInInterpolator())
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        floatingActionButton.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                        floatingActionButton.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                })
                                .start();
                    }
                }
            }
        });
    }

    private class CopyDataTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            database = new MyDatabase(MainActivity.this);
            mJokeModelList = database.getJokes();
            ////
            rawFileReader = RawFileReader.getInstance(getApplicationContext());
            mQuizModelList = rawFileReader.readQuizData();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            setupJokePager();
        }
    }

    private class JokePagerPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            selectedJokePageIndex = position;
            Log.i(TAG, "selectedJokePageIndex " + selectedJokePageIndex);
            checkToDisplayInterstitial();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private class QuizPagerPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            selectedQuizPageIndex = position;
            Log.i(TAG, "selectedQuizPageIndex " + selectedQuizPageIndex);
            checkToDisplayInterstitial();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    @OnClick(R.id.feedback_button)
    void onFeedbackButtonClick() {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));

        }
    }

    private void checkToDisplayInterstitial(){
        numberOfPageSelected++;
        if(numberOfPageSelected >= 6){
            displayInterstitial();
        }
    }
    public void displayInterstitial() {
        startAppAd.showAd(new AdDisplayListener() {

            /**
             * Callback when Ad has been hidden
             *
             * @param ad
             */
            @Override
            public void adHidden(Ad ad) {

            }

            /**
             * Callback when ad has been displayed
             *
             * @param ad
             */
            @Override
            public void adDisplayed(Ad ad) {
                numberOfPageSelected = 0;
                Log.i(TAG, "adDisplayed");
            }

            /**
             * Callback when ad has been clicked
             *
             * @param arg0
             */
            @Override
            public void adClicked(Ad arg0) {

            }

            @Override
            public void adNotDisplayed(Ad ad) {

            }
        });
    }
}
