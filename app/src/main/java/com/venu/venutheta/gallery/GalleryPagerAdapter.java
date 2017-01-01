package com.venu.venutheta.gallery;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.parse.ParseObject;

import java.util.List;


/**
 * Created by oracleen on 2016/7/4.
 */
public class GalleryPagerAdapter extends FragmentStatePagerAdapter {
    private List<ParseObject> mDatas;
    private Context mContext;

    public GalleryPagerAdapter(FragmentManager fm, List<ParseObject> data, Context context) {
        super(fm);
        mDatas = data;
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        ParseObject data = mDatas.get(position);

        if (data.getInt("typeV2") == 0) {
            return PhotoViewerFragment.newInstance(data.getParseUser("from").getUsername(), data.getParseUser("from").getParseFile("avatar").getUrl()
                    , data.getString("tag"), data.getString("url"), data.getObjectId(), data.getClassName());

        } else if(data.getInt("typev2")==1) {
            return VideoPlayerPagerFragment.newInstance(data.getParseUser("from").getUsername(), data.getParseUser("from").getParseFile("avatar").getUrl()
                    , data.getString("tag"), data.getString("url"), data.getObjectId(), data.getClassName());
        }else {
            return null;
        }

    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    public void update(List<ParseObject> data) {
        this.mDatas.addAll(data);
        notifyDataSetChanged();
    }

}

