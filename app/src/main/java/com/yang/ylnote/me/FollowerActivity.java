package com.yang.ylnote.me;

import android.content.Context;
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
import com.yang.ylnote.bean.Follower;
import com.yang.ylnote.bean.Following;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FollowerActivity extends AppCompatActivity {
    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.setting_btn)
    TextView settingBtn;
    @BindView(R.id.follower_recyclerview)
    RecyclerView followerRecyclerview;
    private List<Follower> followerList = new ArrayList<>();
    private FollowerAdapter listAdapter;

    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_follower);
        username = getIntent().getStringExtra("name");
        ButterKnife.bind(this);
        title.setText(getResources().getText(R.string.follower_list));
        readDataFromDB();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        followerRecyclerview.setLayoutManager(layoutManager);
        listAdapter = new FollowerAdapter(followerList, this);
        followerRecyclerview.setAdapter(listAdapter);
    }

    private DatabaseReference myRef = MyApplication.getDBInstance().getReference();

    /**
     * read post list from database
     */
    private void readDataFromDB() {
        myRef.child("follower_list").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followerList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Follower following = new Follower();
                    following.setFollower_name(data.getKey());
                    following.setFollower_img(data.getValue().toString());
                    followerList.add(following);
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


