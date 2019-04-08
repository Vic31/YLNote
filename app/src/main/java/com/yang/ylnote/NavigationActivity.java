package com.yang.ylnote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.yang.ylnote.bean.Note;
import com.yang.ylnote.bean.User;
import com.yang.ylnote.home.CollectsFragment;
import com.yang.ylnote.home.MainFragment;
import com.yang.ylnote.home.NoteFragment;
import com.yang.ylnote.util.Config;
import com.yang.ylnote.util.PermissionActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yang.ylnote.util.Config.UPDATE_POST_LIST;
//, PostFragment.OnPostFragmentListener
//

public class NavigationActivity extends FragmentActivity implements MainFragment.OnFragmentInteractionListener,
        NoteFragment.OnListFragmentInteractionListener, CollectsFragment.OnCollectsFragmentInteractionListener, MeFragment.OnFragmentInteractionListener{

    @BindView(R.id.fragment)
    FrameLayout fragment;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    @BindView(R.id.container)
    LinearLayout container;
    private TextView mTextMessage;
    private MainFragment mainFragment = null;
    private MeFragment meFragment = null;
    private List<Note> noteList = new ArrayList<>();

    private FragmentManager manager;
    private FragmentTransaction transaction;

//    private SharedPreferences sharedPre;
//    private String username;
    private DatabaseReference myRef = MyApplication.getDBInstance().getReference();


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setFragment(0);
                    return true;
                case R.id.navigation_post:
                    setFragment(1);
                    return true;
                case R.id.navigation_me:
                    setFragment(2);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ButterKnife.bind(this);
//        sharedPre = this.getSharedPreferences("config", MODE_PRIVATE);
//        username = sharedPre.getString("username", "");
        manager = getSupportFragmentManager();
        readDataFromDB();
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    /**
     * read post list from database
     */
    private void readDataFromDB() {
        myRef.child("note").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noteList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Note note = data.getValue(Note.class);
                    note.setNote_id(data.getKey());
                    noteList.add(note);
                }
                setFragment(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("PostList", "get note list: onCancelled", databaseError.toException());
            }
        });
    }

    /**
     * change displaying fragments
     *
     * @param where
     */
    public void setFragment(int where) {
        transaction = manager.beginTransaction();
        hideFragment();
        switch (where) {
            case 0:
                Bundle bundle = new Bundle();
                bundle.putSerializable(Config.NOTE_LIST, (Serializable) noteList);
                if (mainFragment == null) {
                    mainFragment = new MainFragment();
                    mainFragment.setArguments(bundle);
                    transaction.add(R.id.fragment, mainFragment);
                } else {
                    mainFragment.getArguments().putSerializable(Config.NOTE_LIST, (Serializable) noteList);
                    transaction.show(mainFragment);
                }
                break;
            case 1:
                Intent post = new Intent(NavigationActivity.this, PermissionActivity.class);
                startActivityForResult(post, UPDATE_POST_LIST);
                break;
            case 2:
                if (meFragment == null){
                    meFragment = new MeFragment();
                    transaction.add(R.id.fragment, meFragment);
                }
                else {
                    transaction.show(meFragment);
                }
                break;
        }
        transaction.commit();
    }

    /**
     * hide other fragments
     */
    private void hideFragment() {
        if (mainFragment != null) {
            transaction.hide(mainFragment);
        }
        if (meFragment != null){
            transaction.hide(meFragment);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Config.UPDATE_POST_LIST:
                readDataFromDB();
                mainFragment.getArguments().putSerializable(Config.NOTE_LIST, (Serializable) noteList);
                navigation.setSelectedItemId(R.id.navigation_home);
                mainFragment.updateNoteList();
                if (meFragment !=null) {
                    meFragment.getDataFromDb();
                }
                break;
            case Config.UPDATE_COLLECT_LIKE_LIST:
                mainFragment.updateCollectList();
                mainFragment.updateLikedList();
                if (meFragment !=null) {
                    meFragment.getDataFromDb();
                }
                break;
        }

    }

    @OnClick(R.id.navigation)
    public void onViewClicked() {
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentInteraction(User user) {

    }

    @Override
    public void onListFragmentInteraction(Note item) {
        Intent postDetail = new Intent(this, PostDetailActivity.class);
        postDetail.putExtra("note_id", item.getNote_id());
        startActivityForResult(postDetail, Config.UPDATE_COLLECT_LIKE_LIST);
    }

    @Override
    public void onCollectsFragmentInteraction(Note item) {
        Intent postDetail = new Intent(this, PostDetailActivity.class);
        postDetail.putExtra("note_id", item.getNote_id());
        startActivityForResult(postDetail, Config.UPDATE_COLLECT_LIKE_LIST);
    }

    private long firstClick;

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        // TODO Auto-generated method stub
//        if(keyCode== KeyEvent.KEYCODE_BACK){
//            if(System.currentTimeMillis()-firstClick>2000){
//                firstClick=System.currentTimeMillis();
//                Toast.makeText(this, "Click again to exit", Toast.LENGTH_SHORT).show();;
//            }else{
//                System.exit(0);
//            }
//            return true;
//        }
//        return false;
//    }
}
