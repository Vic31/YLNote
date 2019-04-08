package com.yang.ylnote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yang.ylnote.bean.Comment;
import com.yang.ylnote.bean.NoteDetail;
import com.yang.ylnote.bean.User;
import com.yang.ylnote.home.CommentAdapter;
import com.yang.ylnote.util.Config;
import com.yang.ylnote.util.GlideApp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PostDetailActivity extends AppCompatActivity {
    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.setting_btn)
    TextView settingBtn;
    @BindView(R.id.user_icon)
    ImageView userIcon;
    @BindView(R.id.username_tv)
    TextView usernameTv;
    @BindView(R.id.follow_btn)
    TextView followBtn;
    @BindView(R.id.note_img)
    ImageView noteImg;
    @BindView(R.id.liked_img)
    ImageView likedImg;
    @BindView(R.id.liked_num)
    TextView likedNum;
    @BindView(R.id.liked_btn)
    LinearLayout likedBtn;
//    @BindView(R.id.comment_img)
//    ImageView commentImg;
//    @BindView(R.id.comment_num)
//    TextView commentNum;
//    @BindView(R.id.comment_btn)
//    LinearLayout commentBtn;
    @BindView(R.id.collect_img)
    ImageView collectImg;
    @BindView(R.id.collect_num)
    TextView collectNum;
    @BindView(R.id.coolect_btn)
    LinearLayout coolectBtn;
    @BindView(R.id.add_comment)
    EditText addComment;
    @BindView(R.id.list)
    RecyclerView list;
    @BindView(R.id.note_content)
    TextView noteContent;
    @BindView(R.id.add_comment_layout)
    LinearLayout addCommentLayout;
    @BindView(R.id.add_comment_btn)
    TextView addCommentBtn;

    private DatabaseReference myRef = MyApplication.getDBInstance().getReference();
    private FirebaseStorage storage = MyApplication.getStoreInstance();
    private String noteId;
    private NoteDetail noteDetail = new NoteDetail();
    private RecyclerView.Adapter<CommentAdapter.ViewHolder> mAdapter;
    private List<Comment> commentList = new ArrayList<>();

    private SharedPreferences sharedPre;
    private String username;
    private String tempComment = "";
    private boolean needUpdateList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        ButterKnife.bind(this);
        sharedPre = this.getSharedPreferences("config", MODE_PRIVATE);
        username = sharedPre.getString("username", "");

        list.setLayoutManager(new LinearLayoutManager(this));
        noteId = getIntent().getStringExtra("note_id");
        title.setText(getResources().getString(R.string.post_detail));
        readDataFromDB();
    }

    @OnClick({R.id.back_btn, R.id.follow_btn, R.id.liked_btn, R.id.coolect_btn, R.id.list, R.id.add_comment_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.follow_btn:
                addFollow();
                break;
            case R.id.liked_btn:
                addLikedNote();
                break;
            case R.id.coolect_btn:
                addCollectNote();
                break;
            case R.id.add_comment_btn:
                View currentView = PostDetailActivity.this.getCurrentFocus();
                if (currentView != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
                }
                updateCommentList();
                break;
        }
    }

    private void checkFollow() {
        myRef.child("follow_list").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.getKey().equals(noteDetail.getUsername())) {
                        followBtn.setClickable(false);
                        followBtn.setTextColor(getResources().getColor(R.color.text_grey_2));
                        followBtn.setBackground(null);
                        followBtn.setText(getResources().getString(R.string.followed));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("NoteDetail add follow", "onCancelled", databaseError.toException());
            }
        });
    }

    /**
     * add author of the note to following list
     */
    private void addFollow() {
        //add following list
        myRef.child("user").child(noteDetail.getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                myRef.child("follow_list").child(username).child(noteDetail.getUsername()).setValue(user.getUrl());
                myRef.child("user").child(noteDetail.getUsername()).child("followed_by").setValue(new Integer(user.getFollowed_by() + 1));
                followBtn.setClickable(false);
                followBtn.setTextColor(getResources().getColor(R.color.text_grey_2));
                followBtn.setBackground(null);
                followBtn.setText(getResources().getString(R.string.followed));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("NoteDetail add follow", "onCancelled", databaseError.toException());
            }
        });
        //opposite: add follower list
        myRef.child("user").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                myRef.child("user").child(username).child("follow").setValue(new Integer(user.getFollow()+1));
                myRef.child("follower_list").child(noteDetail.getUsername()).child(username).setValue(user.getUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("NoteDetail add follow", "onCancelled", databaseError.toException());
            }
        });
    }

    /**
     * check whether liked this note before
     */
    private void checkLikedNote() {
        myRef.child("liked_note_list").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String likedList = dataSnapshot.getValue().toString();
                String[] collectArray = likedList.split(",");
                if (Arrays.asList(collectArray).contains(noteDetail.getNote_id())) {
                    likedImg.setImageResource(R.mipmap.comment_heart_focus);
                } else {
                    likedImg.setImageResource(R.mipmap.comment_heart);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("NoteDetail liked", "onCancelled", databaseError.toException());
            }
        });
    }

    /**
     * check if collected this note before
     */
    private void checkCollectNote() {
        myRef.child("collect_note_list").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String collectIdList = dataSnapshot.getValue().toString();
                String[] collectArray = collectIdList.split(",");
                if (Arrays.asList(collectArray).contains(noteDetail.getNote_id())) {
                    collectImg.setImageResource(R.mipmap.comment_star_focus);
                } else {
                    collectImg.setImageResource(R.mipmap.comment_star);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("NoteDetail collect", "onCancelled", databaseError.toException());
            }
        });
    }

    /**
     * add liked note
     */
    private void addLikedNote() {
        final int likedNumInt = noteDetail.getLike_num();
        myRef.child("liked_note_list").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String likedIdList = dataSnapshot.getValue().toString();
                String[] likedArray = likedIdList.split(",");
                if (!Arrays.asList(likedArray).contains(noteDetail.getNote_id())) {
                    likedIdList += ("," + noteDetail.getNote_id());
                    noteDetail.setLike_num(likedNumInt + 1);
                    myRef.child("liked_note_list").child(username).setValue(likedIdList);
                    myRef.child("note_detail").child(noteId).setValue(noteDetail);
                    likedImg.setImageResource(R.mipmap.comment_heart_focus);
                    likedNum.setText(noteDetail.getLike_num() + "");
                } else {
                    noteDetail.setLike_num(likedNumInt - 1);
                    String noteIdList = "";
                    for (int i = 0; i < likedArray.length; i++) {
                        if (!likedArray[i].equals(noteDetail.getNote_id())) {
                            if (i < likedArray.length - 1) {
                                noteIdList += (likedArray[i] + ",");
                            } else {
                                noteIdList += likedArray[i];
                            }
                        }
                    }
                    myRef.child("liked_note_list").child(username).setValue(noteIdList);
                    myRef.child("note_detail").child(noteId).setValue(noteDetail);
                    likedImg.setImageResource(R.mipmap.comment_heart);
                    likedNum.setText(noteDetail.getLike_num() + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("NoteDetail liked", "onCancelled", databaseError.toException());
            }
        });
        needUpdateList = true;
    }

    /**
     * collect this note
     */
    private void addCollectNote() {
        final int collectNumInt = noteDetail.getCollect_num();
        myRef.child("collect_note_list").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String collectIdList = dataSnapshot.getValue().toString();
                String[] collectArray = collectIdList.split(",");
                if (!Arrays.asList(collectArray).contains(noteDetail.getNote_id())) {
                    collectIdList += ("," + noteDetail.getNote_id());
                    noteDetail.setCollect_num(collectNumInt + 1);
                    myRef.child("collect_note_list").child(username).setValue(collectIdList);
                    myRef.child("note_detail").child(noteId).setValue(noteDetail);
                    updateLayout(1);
                } else {
                    noteDetail.setCollect_num(collectNumInt - 1);
                    String noteIdList = "";
                    for (int i = 0; i < collectArray.length; i++) {
                        if (!collectArray[i].equals(noteDetail.getNote_id())) {
                            if (i < collectArray.length - 1) {
                                noteIdList += (collectArray[i] + ",");
                            } else {
                                noteIdList += collectArray[i];
                            }
                        }
                    }
                    myRef.child("collect_note_list").child(username).setValue(noteIdList);
                    myRef.child("note_detail").child(noteId).setValue(noteDetail);
                    updateLayout(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("NoteDetail collect", "onCancelled", databaseError.toException());
            }
        });
        needUpdateList = true;
    }

    private void updateLayout(int flag) {
        //minus 1 from collected number
        collectNum.setText(noteDetail.getCollect_num() + "");
        if (flag == 0) {
            collectImg.setImageResource(R.mipmap.comment_star);
        } else {
            collectImg.setImageResource(R.mipmap.comment_star_focus);
        }
    }

    /**
     * read post list from database
     */
    private void readDataFromDB() {
        myRef.child("note_detail").child(noteId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noteDetail = dataSnapshot.getValue(NoteDetail.class);
                noteDetail.setNote_id(dataSnapshot.getKey());
                setInfomationToComponents();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("NoteDetail", "onCancelled", databaseError.toException());
            }
        });
    }

    /**
     * set information to components
     */
    private void setInfomationToComponents() {
        checkCollectNote();
        checkLikedNote();
        checkFollow();
        usernameTv.setText(noteDetail.getUsername());
        noteContent.setText(noteDetail.getText());
        String urlStr = "images/" + noteDetail.getPic();
        StorageReference postRef = storage.getReference().child(urlStr);
        GlideApp.with(this).load(postRef).into(noteImg);
        likedNum.setText(noteDetail.getLike_num() + "");
        collectNum.setText(noteDetail.getCollect_num() + "");
        myRef.child("comment").child(noteDetail.getComment_id()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Comment comment = new Comment();
                    comment.setCommentName(data.getKey());
                    comment.setCommentContent(data.getValue().toString());
                    commentList.add(comment);
                }
                setComments();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setComments() {
        mAdapter = new CommentAdapter(this, commentList);
        list.setAdapter(mAdapter);
        checkCommentted();
    }

    private void updateCommentList() {
        tempComment = addComment.getText().toString();
        if (!TextUtils.isEmpty(tempComment)) {
            Comment comment = new Comment();
            comment.setCommentName(username);
            comment.setCommentContent(tempComment);
            commentList.add(comment);
            mAdapter.notifyDataSetChanged();
            myRef.child("comment").child(noteDetail.getComment_id()).child(username).setValue(tempComment);
            addCommentLayout.setVisibility(View.GONE);
        }
    }

    /**
     * one user could only comment once
     */
    private void checkCommentted() {
        if (commentted()) {
            addCommentLayout.setVisibility(View.GONE);
        } else {
            addCommentLayout.setVisibility(View.VISIBLE);
        }
    }

    private boolean commentted() {
        for (Comment comment : commentList) {
            if (comment.getCommentName().equals(username)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Config.UPDATE_COMMENT_LIST:
                username = sharedPre.getString("username", "");
                updateCommentList();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (needUpdateList) {
            setResult(Config.UPDATE_COLLECT_LIKE_LIST);
        }
        super.onDestroy();
    }
}
