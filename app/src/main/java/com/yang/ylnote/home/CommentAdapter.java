package com.yang.ylnote.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yang.ylnote.R;
import com.yang.ylnote.bean.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context context;
    private List<Comment> commentList;
    
    public CommentAdapter(Context context, List<Comment> items){
        commentList = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_detail_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.content.setText(commentList.get(position).getCommentContent());
        holder.username.setText(commentList.get(position).getCommentName()+": ");
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView content;
        public final TextView username;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            content = (TextView) mView.findViewById(R.id.comment_content);
            username = (TextView) mView.findViewById(R.id.comment_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + content.getText() + "'";
        }
    }
    
}
