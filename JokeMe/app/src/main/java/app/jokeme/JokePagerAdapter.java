package app.jokeme;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by thaodv on 5/4/16.
 */
public class JokePagerAdapter extends PagerAdapter {
    private static final String TAG = JokePagerAdapter.class.getName();
    private Context mContext;
    private List<JokeModel> mJokeModelList;

    public JokePagerAdapter(Context context) {
        mContext = context;
    }

    public void setJokeModelList(List<JokeModel> mJokeModelList) {
        this.mJokeModelList = mJokeModelList;
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        JokeModel jokeModel = mJokeModelList.get(position);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.joke_item, collection, false);
        TextView content = (TextView) layout.findViewById(R.id.id_joke_item_content);
        ///
        if (jokeModel.isJoke()) {
            content.setText(jokeModel.getContent());
        } else {
            content.setText(jokeModel.getQuestion());
        }
        //
        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        if(mJokeModelList == null ) return 0;
        return mJokeModelList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ((position + 1) + " of " + mJokeModelList.size());
    }

}

