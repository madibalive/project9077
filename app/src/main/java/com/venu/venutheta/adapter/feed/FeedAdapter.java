package com.venu.venutheta.adapter.feed;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.venu.venutheta.Actions.ActionLike;
import com.venu.venutheta.Actions.ActionStar;
import com.venu.venutheta.Actions.ActionThumbs;
import com.venu.venutheta.Actions.ActionUnStar;
import com.venu.venutheta.Actions.ActionUnThumbs;
import com.venu.venutheta.Actions.ActionUnlike;
import com.venu.venutheta.R;
import com.venu.venutheta.models.ModelFeedItem;
import com.wonderkiln.blurkit.BlurLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.venu.venutheta.utils.ImageUitls.tint;


/**
 * Created by Madiba on 9/12/2016.
 */

public class FeedAdapter extends BaseQuickAdapter<ModelFeedItem> {

    public FeedAdapter(int layoutResId, List<ModelFeedItem> data) {
        super(layoutResId, data);

    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, ModelFeedItem feedItem) {

    }

    @Override
    protected int getDefItemViewType(int position) {
        if (getItem(position).getType() == ModelFeedItem.TYPP_MEDIA_IMAGE)
            return ModelFeedItem.TYPP_MEDIA_IMAGE;

        else if (getItem(position).getType() == ModelFeedItem.TYPP_MEDIA_VIDEO)
            return ModelFeedItem.TYPP_MEDIA_VIDEO;

        else if (getItem(position).getType() == ModelFeedItem.TYPP_EVENT)
            return ModelFeedItem.TYPP_EVENT;

        else if (getItem(position).getType() == ModelFeedItem.TYPP_GOSSIP)
            return ModelFeedItem.TYPP_GOSSIP;

        return super.getDefItemViewType(position);
    }

    @Override
    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ModelFeedItem.TYPP_MEDIA_IMAGE)
            return new MediaViewHolder(getItemView(R.layout.main_media_feed, parent));

        if (viewType == ModelFeedItem.TYPP_MEDIA_VIDEO)
            return new MediaViewHolder(getItemView(R.layout.main_media_feed, parent));
        if (viewType == ModelFeedItem.TYPP_EVENT)
            return new EventViewHolder(getItemView(R.layout.main_event_feedv2, parent));
        if (viewType == ModelFeedItem.TYPP_GOSSIP)
            return new GossipViewHolder(getItemView(R.layout.gossip, parent));

        return super.onCreateDefViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Log.i(TAG, "onBindViewHolder: started creating rows");

        if (holder instanceof MediaViewHolder) {
            //initial item
            final ModelFeedItem data = getItem(holder.getAdapterPosition());

            // set text varribles
            ((MediaViewHolder) holder).setText(R.id.f_hashtag,"#"+data.getHashtag());
            ((MediaViewHolder) holder).setText(R.id.f_like,String.valueOf(data.getReactions()));
            ((MediaViewHolder) holder).setText(R.id.f_comment,String.valueOf(data.getComment()));
            ((MediaViewHolder) holder).setText(R.id.f_time_ago,data.getDateToString());
            ((MediaViewHolder) holder).setText(R.id.f_name,data.getName());

            ((MediaViewHolder) holder).setOnClickListener(R.id.f_avatar, new OnItemChildClickListener());
            ((MediaViewHolder) holder).setOnClickListener(R.id.f_hashtag, new OnItemChildClickListener());
            ((MediaViewHolder) holder).setOnClickListener(R.id.f_comment, new OnItemChildClickListener());


            // CHECK MY EVENT
            if (data.isOwnedByUser){
                ((MediaViewHolder) holder).setVisible(R.id.f_overflow_ic,true)
                        .setOnClickListener(R.id.f_overflow_ic, new OnItemChildClickListener());
            }

            // check if shared
            if (data.isShared){
                ((MediaViewHolder) holder).setVisible(R.id.f_shared,true);
            }

            // SET IMAGES HERE
            Glide.with(mContext).load(data.getUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
                    .crossFade().into((ImageView) ((MediaViewHolder) holder).getView(R.id.f10_image));

           // set avatar
            if (data.getAvatar()!=null){
                Glide.with(mContext).load(data.getAvatar())
                    .priority(Priority.LOW)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT).dontAnimate().into((ImageView)  ((MediaViewHolder) holder).getView(R.id.f_avatar));
            }



            //USE IF LIKE TO SET DRAWABLES EHRE
            TextView likes =((MediaViewHolder)holder).getView(R.id.f_like);
            Drawable icon ;
            if (data.getPpIsLike()){
                // set drawable ehrere
                icon = ContextCompat.getDrawable(mContext,R.drawable.ic_heart_active);
            }else {
                // set drawable  here
                icon = ContextCompat.getDrawable(mContext,R.drawable.ic_heart);
            }
            likes.setCompoundDrawables(icon,null,null,null);
            likes.setOnClickListener(view -> {
                if (data.getPpIsLike()){
                    EventBus.getDefault().post(new ActionUnlike(holder.getAdapterPosition()));

                }else {
                    EventBus.getDefault().post(new ActionLike(holder.getAdapterPosition()));

                }
            });


            //SET IS VIDEO HERE
            if (data.getType() == ModelFeedItem.TYPP_MEDIA_VIDEO){
                ImageView v=((MediaViewHolder) holder).getView(R.id.f10_image);
                v.setColorFilter(ContextCompat.getColor(mContext,R.color.venu_flat_color), android.graphics.PorterDuff.Mode.MULTIPLY);
                ((MediaViewHolder) holder).setVisible(R.id.f_video_thumb,true);
                ((MediaViewHolder) holder).setOnClickListener(R.id.f_video_thumb, new OnItemChildClickListener());

            }



        }

        if (holder instanceof EventViewHolder) {
            final ModelFeedItem data = getItem(holder.getAdapterPosition());

            ((EventViewHolder) holder).setText(R.id.f_hashtag,"#"+data.getHashtag());
            ((EventViewHolder) holder).setText(R.id.f_like,String.valueOf(data.getReactions()));
            ((EventViewHolder) holder).setText(R.id.f_comment,String.valueOf(data.getComment()));
            ((EventViewHolder) holder).setText(R.id.f_share,String.valueOf(data.getShare()));
            ((EventViewHolder) holder).setText(R.id.f_time_ago,data.getDateToString());
            ((EventViewHolder) holder).setText(R.id.f_name,data.getName());


            ((EventViewHolder) holder).setOnClickListener(R.id.f_avatar, new OnItemChildClickListener());
            ((EventViewHolder) holder).setOnClickListener(R.id.f_hashtag, new OnItemChildClickListener());
            ((EventViewHolder) holder).setOnClickListener(R.id.f_comment, new OnItemChildClickListener());

            // CHECK MY EVENT
            if (data.isOwnedByUser){
                ((MediaViewHolder) holder).setVisible(R.id.f_overflow_ic,true)
                        .setOnClickListener(R.id.f_overflow_ic, new OnItemChildClickListener());
            }


            TextView likes =((EventViewHolder)holder).getView(R.id.f_like);
            Drawable icon ;
            if (data.getEvIsInterest()){
                // set drawable ehrere
                icon = ContextCompat.getDrawable(mContext,R.drawable.ic_star_active);
            }else {
                // set drawable  here
                icon = ContextCompat.getDrawable(mContext,R.drawable.ic_star);
            }
            likes.setCompoundDrawables(icon,null,null,null);
            likes.setOnClickListener(view -> {
                if (data.getEvIsInterest()){
                    EventBus.getDefault().post(new ActionUnStar(holder.getAdapterPosition()));

                }else {
                    EventBus.getDefault().post(new ActionStar(holder.getAdapterPosition()));

                }
            });

            Glide.with(mContext).load(data.getUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
                    .crossFade().into((ImageView) ((EventViewHolder) holder).getView(R.id.main_event_image));
            Glide.with(mContext).load(data.getAvatar())
                    .priority(Priority.LOW)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT).dontAnimate().into((ImageView)  ((EventViewHolder) holder).getView(R.id.f_avatar));



            // EVENT DETAIL HERE ----------------------------->

            ((EventViewHolder) holder).setText(R.id.main_event_title,String.valueOf(data.getEvTitle()));
            ((EventViewHolder) holder).setText(R.id.main_event_time,String.valueOf(data.getEvDateToString()));
//            ((EventViewHolder) holder).setText(R.id.main_event_location,String.valueOf(data.getMeventlocation()));
//            ((EventViewHolder) holder).setText(R.id.main_event_category,data.getMeventdate());
            ((EventViewHolder) holder).setText(R.id.main_event_date,data.getEvDateToString());

            if (data.getShared()){
                ((EventViewHolder) holder).setVisible(R.id.f_share,true);

            }





//            ((EventViewHolder) holder).setOnClickListener(R.id.f10_image, new OnItemChildClickListener());
//
            ((EventViewHolder) holder).setOnClickListener(R.id.main_event_image, new OnItemChildClickListener());
//            ((EventViewHolder) holder).setOnClickListener(R.id.f4_hashtag, new OnItemChildClickListener());
//            ((EventViewHolder) holder).setOnClickListener(R.id.main_event_title, new OnItemChildClickListener());
//
//            ((EventViewHolder) holder).setOnClickListener(R.id.f5_like_ic, new OnItemChildClickListener());
//            ((EventViewHolder) holder).setOnClickListener(R.id.f5_share_ic, new OnItemChildClickListener());
//            ((EventViewHolder) holder).setOnClickListener(R.id.f5_comment_ic, new OnItemChildClickListener());

        }

        if (holder instanceof GossipViewHolder) {
            ModelFeedItem data = getItem(holder.getAdapterPosition());

            ((GossipViewHolder) holder).setText(R.id.f_like,String.valueOf(data.getReactions()));
            ((GossipViewHolder) holder).setText(R.id.f_share,String.valueOf(data.getShare()));
            ((GossipViewHolder) holder).setText(R.id.f_name,data.getName());


            ((GossipViewHolder) holder).setOnClickListener(R.id.main_gossip_title, new OnItemChildClickListener());
            ((GossipViewHolder) holder).setOnClickListener(R.id.f_avatar, new OnItemChildClickListener());
            // check if shared
            if (data.isShared){
                ((GossipViewHolder) holder).setVisible(R.id.f_shared,true);
            }


            // CHECK MY EVENT
            if (data.isOwnedByUser){
                ((GossipViewHolder) holder).setVisible(R.id.f_overflow_ic,true)
                        .setOnClickListener(R.id.f_overflow_ic, new OnItemChildClickListener());
            }

            Glide.with(mContext).load(data.getAvatar()).diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .priority(Priority.LOW)
                    .dontAnimate()
                    .into((ImageView)  ((GossipViewHolder) holder).getView(R.id.f_avatar));

            TextView likes =((GossipViewHolder)holder).getView(R.id.f_like);
            Drawable icon ;
            if (data.getGpIsThumbsUp()){
                // set drawable ehrere
                icon = ContextCompat.getDrawable(mContext,R.drawable.ic_thumb_active);
            }else {
                // set drawable  here
                icon = ContextCompat.getDrawable(mContext,R.drawable.ic_like);
            }
            likes.setCompoundDrawables(icon,null,null,null);
            likes.setOnClickListener(view -> {
                if (data.getGpIsThumbsUp()){
                    EventBus.getDefault().post(new ActionUnThumbs(holder.getAdapterPosition()));

                }else {
                    EventBus.getDefault().post(new ActionThumbs(holder.getAdapterPosition()));

                }
            });


            // GOSSIP DETAILS HERE -------------------------->

            ((GossipViewHolder) holder).setText(R.id.f_gossip_time,data.getGpElapseTimeToString());
            ((GossipViewHolder) holder).setText(R.id.main_gossip_title,data.getGpTitle());


        }

        super.onBindViewHolder(holder, holder.getAdapterPosition());
    }

    public static class SetBlurlayout extends ImageViewTarget<GlideDrawable> {
//    private final ColorStateList placeholderColor;
//    private final ColorStateList resultColor;
//    private final ColorStateList errorColor;
//    public SetBlurlayout(ImageView view,
//                      ColorStateList placeholderColor, ColorStateList resultColor, ColorStateList errorColor) {
//        super(view);
//        this.placeholderColor = placeholderColor;
//        this.resultColor = resultColor;
//        this.errorColor = errorColor;
//    }

        private BlurLayout blurLayout;

        public SetBlurlayout(ImageView view, BlurLayout blurLayout) {
            super(view);
            blurLayout = blurLayout;
        }

        @Override
        public void setDrawable(Drawable drawable) {
            // don't tint, this is called with a cross-fade drawable,
            // and we need the inner drawables tinted, but not this
            super.setDrawable(drawable);
        }

        @Override
        public void onLoadStarted(Drawable placeholder) {
            super.onLoadStarted(placeholder);
        }

        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
            super.onLoadFailed(e, tint(errorDrawable));
        }

        @Override
        public void onLoadCleared(Drawable placeholder) {
            super.onLoadCleared(placeholder);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation glideAnimation) {
            // animate works with drawable likely because it's accepting Drawables, but declaring GlideDrawable as generics
            if (glideAnimation == null || !glideAnimation.animate(resource, this)) {
                view.setImageDrawable(resource);
            }
            blurLayout.invalidate();
        }

        @Override
        protected void setResource(GlideDrawable resource) {
            throw new UnsupportedOperationException("onResourceReady is overridden, this shouldn't be called");
        }
    }

    public class MediaViewHolder extends BaseViewHolder {
        MediaViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class GossipViewHolder extends BaseViewHolder {
        GossipViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class EventViewHolder extends BaseViewHolder {
        EventViewHolder(View itemView) {
            super(itemView);
        }
    }
}
