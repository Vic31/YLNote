package com.yang.ylnote.me;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yang.ylnote.MyApplication;
import com.yang.ylnote.R;
import com.yang.ylnote.bean.Following;
import com.yang.ylnote.bean.Note;
import com.yang.ylnote.util.GlideApp;

import java.util.List;


public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.ViewHolder> {
    private List<Following> mDataList;
    private Context context;
    private FirebaseStorage storage = MyApplication.getStoreInstance();


    public  FollowingAdapter(List<Following> FollowingList,Context context){
        mDataList = FollowingList;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_following_item,
                parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        Following Following = mDataList.get(position);
//        holder.imageAvatar.setImageResource(Following.getFollowing_img());
//        holder.nameText.setText(Following.getFollowing_name());
//        holder.contentsText.setText(Following.getContentsText());
//    }

    public void onBindViewHolder(final ViewHolder holder, int position) {
        String url = mDataList.get(position).getfollowing_img();
        if (!TextUtils.isEmpty(url)) {
            String urlStr = "images/" + url;
            StorageReference postRef = storage.getReference().child(urlStr);
            GlideApp.with(context).load(postRef).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(holder.imageAvatar);
        }
//        else {
//            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round);
//            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
//            roundedBitmapDrawable.setCornerRadius(Math.min(bitmap.getWidth(), bitmap.getHeight())/2.0f);
//            holder.imageAvatar.setImageDrawable(roundedBitmapDrawable);
//        }
        Following Following = mDataList.get(position);

        //holder.imageAvatar.setImageResource(Following.getFollowing_img());
        holder.nameText.setText(Following.getFollowing_name());
        holder.contentsText.setText(Following.getContentsText());
    }
    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageAvatar;
        //TextView nameText;
        TextView contentsText;
        public final TextView nameText;

        public ViewHolder(View itemView) {
            super(itemView);
            imageAvatar = (ImageView)itemView.findViewById(R.id.following_img);
            nameText =(TextView) itemView.findViewById(R.id.following_name);
            contentsText = (TextView)itemView.findViewById(R.id.following_btn);

        }
    }

}




