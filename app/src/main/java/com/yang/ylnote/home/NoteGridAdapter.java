package com.yang.ylnote.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yang.ylnote.MyApplication;
import com.yang.ylnote.R;
import com.yang.ylnote.home.NoteFragment.OnListFragmentInteractionListener;
import com.yang.ylnote.bean.Note;
import com.yang.ylnote.util.GlideApp;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Note} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class NoteGridAdapter extends RecyclerView.Adapter<NoteGridAdapter.ViewHolder> {

    private Context context;
    private final List<Note> mValues;
    private OnListFragmentInteractionListener mListener;
    // storage reference
    private FirebaseStorage storage = MyApplication.getStoreInstance();

    public NoteGridAdapter(List<Note> items, OnListFragmentInteractionListener listener, Context context) {
        mValues = items;
        mListener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //parent.getContext()
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_note_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String url = mValues.get(position).getPic();
        Log.i("url", url);
        if (!TextUtils.isEmpty(url)) {
            holder.noteImg.setVisibility(View.VISIBLE);
//            StorageReference postRef = storage.getReferenceFromUrl(url);
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
//        GlideApp.with(this /* context */)
//                .load(postRef)
//                .into(holder.noteImg);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
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
