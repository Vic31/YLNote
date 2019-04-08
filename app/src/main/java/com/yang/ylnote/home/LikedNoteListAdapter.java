package com.yang.ylnote.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yang.ylnote.MyApplication;
import com.yang.ylnote.R;
import com.yang.ylnote.bean.Note;
import com.yang.ylnote.util.GlideApp;

import java.util.List;

public class LikedNoteListAdapter extends RecyclerView.Adapter<LikedNoteListAdapter.ViewHolder> {

    private Context context;
    private final List<Note> mValues;
//    private LikedFragment.OnLikedFragmentInteractionListener mListener;
    // storage reference
    private FirebaseStorage storage = MyApplication.getStoreInstance();

//    public LikedNoteListAdapter(List<Note> items, LikedFragment.OnLikedFragmentInteractionListener listener, Context context) {
//        mValues = items;
//        mListener = listener;
//        this.context = context;
//    }
    public LikedNoteListAdapter(List<Note> items, Context context){
        mValues = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_note_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String url = mValues.get(position).getPic();
        if (!TextUtils.isEmpty(url)) {
            holder.noteImg.setVisibility(View.VISIBLE);
            String urlStr = "images/"+url;
            StorageReference postRef = storage.getReference().child(urlStr);
            GlideApp.with(context).load(postRef).into(holder.noteImg);
        }
        else {
            holder.noteImg.setVisibility(View.GONE);
        }
        holder.mItem = mValues.get(position);
        holder.content.setText(mValues.get(position).getText());
        holder.username.setText(mValues.get(position).getUsername());
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
        roundedBitmapDrawable.setCornerRadius(Math.min(bitmap.getWidth(), bitmap.getHeight())/2.0f);
        holder.userIcon.setImageDrawable(roundedBitmapDrawable);
//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onLikedFragmentInteraction(holder.mItem);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        if (mValues!=null) {
            return mValues.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView noteImg;
        public final TextView content;
        public final ImageView userIcon;
        public final TextView username;
        public Note mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            noteImg = (ImageView) mView.findViewById(R.id.note_img);
            content = (TextView) mView.findViewById(R.id.content);
            userIcon = (ImageView) mView.findViewById(R.id.user_icon);
            username = (TextView) mView.findViewById(R.id.username);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + content.getText() + "'";
        }
    }
}