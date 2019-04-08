package com.yang.ylnote.me;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.yang.ylnote.MyApplication;
import com.yang.ylnote.R;
import com.yang.ylnote.bean.Following;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FollowingActivity extends AppCompatActivity {
    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.setting_btn)
    TextView settingBtn;
    @BindView(R.id.following_recyclerview)
    RecyclerView followingRecyclerview;
    private List<Following> followingList = new ArrayList<>();
    FollowingAdapter listAdapter;
    private String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_following);
        ButterKnife.bind(this);
        username = getIntent().getStringExtra("name");
        title.setText(getResources().getText(R.string.following_list));
        readDataFromDB();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        followingRecyclerview.setLayoutManager(layoutManager);
        listAdapter = new FollowingAdapter(followingList, this);
        followingRecyclerview.setAdapter(listAdapter);
    }

    private DatabaseReference myRef = MyApplication.getDBInstance().getReference();

    /**
     * read post list from database
     */
    private void readDataFromDB() {
        myRef.child("follow_list").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Following following = new Following();
                    following.setFollowing_name(data.getKey());
                    following.setFollowing_img(data.getValue().toString());
                    followingList.add(following);
                }
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("PostList", "get note list: onCancelled", databaseError.toException());
            }
        });
    }


    @OnClick(R.id.back_btn)
    public void onViewClicked() {
        finish();
    }
}


