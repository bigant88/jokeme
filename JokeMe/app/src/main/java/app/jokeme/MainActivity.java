package app.jokeme;

import android.animation.Animator;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = MainActivity.class.getName();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        initUI();
        jokePagerAdapter = new JokePagerAdapter(getApplicationContext());
        new CopyDataTask().execute();
    }

    private void setupJokePager() {
        mViewPagerLayout.setVisibility(View.VISIBLE);
        mViewSettingLayout.setVisibility(View.GONE);
        jokePagerAdapter.setJokeModelList(mJokeModelList);
        mViewPager.setAdapter(jokePagerAdapter);
        mViewPager.setOffscreenPageLimit(10);
        //
        mViewPagerIndicator.setViewPager(mViewPager);
    }

    private void setupQuizPager() {
        mViewPagerLayout.setVisibility(View.VISIBLE);
        mViewSettingLayout.setVisibility(View.GONE);
        jokePagerAdapter.setJokeModelList(mQuizModelList);
        mViewPager.setAdapter(jokePagerAdapter);
        mViewPager.setOffscreenPageLimit(10);
        //
        mViewPagerIndicator.setViewPager(mViewPager);
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
                Log.i(TAG, "onTabSelected "+ position + "  " + wasSelected);
                switch (position) {
                    case 0:
                        if(!wasSelected){
                            setupJokePager();
                        }
                        break;
                    case 1:
                        if(!wasSelected){
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

    private class JokePagerPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private class QuizPagerPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
