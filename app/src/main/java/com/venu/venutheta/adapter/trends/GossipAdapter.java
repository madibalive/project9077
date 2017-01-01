package com.venu.venutheta.adapter.trends;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.parse.ParseObject;
import com.venu.venutheta.R;
import com.venu.venutheta.utils.TimeUitls;

import java.util.List;

/**
 * Created by Madiba on 9/25/2016.
 */

public class GossipAdapter extends BaseQuickAdapter<ParseObject> {
    public GossipAdapter(int layoutResId, List<ParseObject> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, ParseObject gossip) {
        holder
                .setText(R.id.gossip_name, gossip.getString("title"))
                .setText(R.id.gossip_title, gossip.getParseUser("from").getUsername())
                .setText(R.id.gossip_title, TimeUitls.elapseTime(gossip.getDate("createdAt")));
    }
}
