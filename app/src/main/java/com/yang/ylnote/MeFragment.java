package com.yang.ylnote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yang.ylnote.bean.Note;
import com.yang.ylnote.bean.User;
import com.yang.ylnote.me.FollowerActivity;
import com.yang.ylnote.me.FollowingActivity;
import com.yang.ylnote.util.Config;
import com.yang.ylnote.util.GlideApp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MeFragment extends Fragment {

    @BindView(R.id.me_name)
    TextView meName;
    @BindView(R.id.user_icon)
    ImageView userIcon;
    @BindView(R.id.follow_num)
    TextView followNum;
    @BindView(R.id.follow_btn)
    LinearLayout followBtn;
    @BindView(R.id.follower_num)
    TextView followerNum;
    @BindView(R.id.follower_btn)
    LinearLayout followerBtn;
    Unbinder unbinder;
    @BindView(R.id.list)
    RecyclerView listView;
    private DatabaseReference myRef = MyApplication.getDBInstance().getReference();
    // storage reference
    private FirebaseStorage storage = MyApplication.getStoreInstance();
    private String username;
    //获取SharedPreferences对象
    private SharedPreferences sharedPre;
    private RecyclerView.Adapter<PostAdapter.ViewHolder> adapter;
    private List<Note> postList = new ArrayList<>();
    private OnFragmentInteractionListener mListener;

    public MeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        // Inflate the layout for this fragment
        sharedPre = getActivity().getSharedPreferences("config", getActivity().MODE_PRIVATE);
        username = sharedPre.getString("username", "");
        unbinder = ButterKnife.bind(this, view);
        meName.setText(username);
        listView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
//        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PostAdapter(getActivity(), postList);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDataFromDb();
    }

    public void getDataFromDb() {
        myRef.child("user").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                followerNum.setText(user.getFollowed_by() + "");
                followNum.setText(user.getFollow() + "");
                String url = user.getUrl();
                if (!TextUtils.isEmpty(url)) {
                    String urlStr = "images/" + url;
                    StorageReference postRef = storage.getReference().child(urlStr);
                    GlideApp.with(getActivity()).load(postRef).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(userIcon);
                }
                String[] postArray = user.getPost_list().split(",");
                getPostFromDb(postArray);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("NoteDetail liked", "onCancelled", databaseError.toException());
            }
        });
    }

    private void getPostFromDb(final String[] postArray) {
        myRef.child("note").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (Arrays.asList(postArray).contains(data.getKey())) {
                        Note note = data.getValue(Note.class);
                        note.setNote_id(data.getKey());
                        postList.add(note);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.follow_btn, R.id.follower_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.follow_btn:
                Intent follow = new Intent(getActivity(), FollowingActivity.class);
                follow.putExtra("name", username);
                startActivity(follow);
                break;
            case R.id.follower_btn:
                Intent follower = new Intent(getActivity(), FollowerActivity.class);
                follower.putExtra("name", username);
                startActivity(follower);
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(User user);
    }

    public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
        private Context context;
        private List<Note> noteList;

        public PostAdapter(Context context, List<Note> items) {
            noteList = items;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.fragment_me_item, null, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            String url = noteList.get(position).getPic();
            if (!TextUtils.isEmpty(url)) {
                String urlStr = "images/" + url;
                StorageReference postRef = storage.getReference().child(urlStr);
                GlideApp.with(context).load(postRef).into(holder.postImg);
            }
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent postDetail = new Intent(getActivity(), PostDetailActivity.class);
                    postDetail.putExtra("note_id", noteList.get(position).getNote_id());
                    startActivityForResult(postDetail, Config.UPDATE_COLLECT_LIKE_LIST);
                }
            });
        }

        @Override
        public int getItemCount() {
            return noteList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView postImg;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                postImg = (ImageView) mView.findViewById(R.id.post_item_img);
            }

            @Override
            public String toString() {
                return super.toString();
            }
        }
    }


}
