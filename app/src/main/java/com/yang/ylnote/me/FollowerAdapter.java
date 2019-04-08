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
import com.yang.ylnote.bean.Follower;
import com.yang.ylnote.bean.Note;
import com.yang.ylnote.util.GlideApp;

import java.util.List;


public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.ViewHolder> {
    private List<Follower> mDataList;
    private Context context;
    private FirebaseStorage storage = MyApplication.getStoreInstance();

    public  FollowerAdapter(List<Follower> followerList,Context context){
        mDataList = followerList;
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
//        Follower follower = mDataList.get(position);
//        holder.imageAvatar.setImageResource(follower.getfollower_img());
//        holder.nameText.setText(follower.getfollower_name());
//        holder.contentsText.setText(follower.getContentsText());
//    }

    public void onBindViewHolder(final ViewHolder holder, int position) {
        String url = mDataList.get(position).getFollower_img();
        if (!TextUtils.isEmpty(url)) {
            String urlStr = "images/" + url;
            StorageReference postRef = storage.getReference().child(urlStr);
            GlideApp.with(context).load(postRef).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(holder.imageAvatar);
//            GlideApp.with(context).load(postRef).into(holder.imageAvatar);
        }
//        else {
//            holder.imageAvatar.setImageResource(R.mipmap.ic_launcher_round);
//        }
        Follower follower = mDataList.get(position);
        holder.nameText.setText(follower.getFollower_name());
        holder.contentsText.setVisibility(View.GONE);
//        holder.contentsText.setText(follower.getContentsText());
    }
    @Override
    public int getItemCount() {
        return mDataList.size();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageAvatar;
        TextView contentsText;
        public final TextView nameText;

        public ViewHolder(View itemView) {
            super(itemView);
            //注意这里可能需要import com.example.lenovo.myrecyclerview.R; 才能使用R.id
            imageAvatar = (ImageView)itemView.findViewById(R.id.following_img);
            nameText =(TextView) itemView.findViewById(R.id.following_name);
            contentsText = (TextView)itemView.findViewById(R.id.following_btn);
            contentsText.setBackground(null);
        }
    }

}




