package com.yang.ylnote.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yang.ylnote.R;
import com.yang.ylnote.bean.Note;
import com.yang.ylnote.util.Config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MainFragment extends Fragment implements NoteFragment.OnListFragmentInteractionListener, CollectsFragment.OnCollectsFragmentInteractionListener{

    private OnFragmentInteractionListener mListener;

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 3;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;
    private NoteFragment noteFragment = new NoteFragment();
    private CollectsFragment collectsFragment = new CollectsFragment();
    private LikedFragment likedFragment = new LikedFragment();
    private List<Fragment> fragmentList = new ArrayList<>();

    private List<Note> noteList = new ArrayList<>();
    private List<Note> collectList = new ArrayList<>();
    private List<Note> likedList = new ArrayList<>();

    private TextView likedBtn, exploreBtn, collectBtn;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noteList = (List) getArguments().getSerializable(Config.NOTE_LIST);
        collectList = (List) getArguments().getSerializable(Config.COLLECT_LIST);
        likedList = (List) getArguments().getSerializable(Config.NOTE_LIST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        likedBtn = (TextView) view.findViewById(R.id.liked_btn);
        exploreBtn = (TextView) view.findViewById(R.id.explore_btn);
        collectBtn = (TextView) view.findViewById(R.id.collects_btn);
        likedBtn.setOnClickListener(listener);
        exploreBtn.setOnClickListener(listener);
        collectBtn.setOnClickListener(listener);
        fragmentList.add(likedFragment);
        Bundle bundle_note = new Bundle();
        bundle_note.putSerializable(Config.NOTE_LIST, (Serializable) noteList);
        noteFragment.setArguments(bundle_note);
        fragmentList.add(noteFragment);
        fragmentList.add(collectsFragment);
        // Inflate the layout for this fragment
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) view.findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getActivity().getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(1);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeFocusColor(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.liked_btn:
                    mPager.setCurrentItem(0);
                    break;
                case R.id.explore_btn:
                    mPager.setCurrentItem(1);
                    break;
                case R.id.collects_btn:
                    mPager.setCurrentItem(2);
                    break;
            }
        }
    };
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        noteList = (List) getArguments().getSerializable(Config.NOTE_LIST);
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
        void onFragmentInteraction(Uri uri);
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
//            changeFocusColor(position);
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    @Override
    public void onListFragmentInteraction(Note item) {

    }

    @Override
    public void onCollectsFragmentInteraction(Note item) {

    }

    private void changeFocusColor(int position){
        likedBtn.setTextColor(getResources().getColor(R.color.text_grey_3));
        exploreBtn.setTextColor(getResources().getColor(R.color.text_grey_3));
        collectBtn.setTextColor(getResources().getColor(R.color.text_grey_3));
        switch (position){
            case 0:
                likedBtn.setTextColor(getResources().getColor(R.color.black));
                break;
            case 1:
                exploreBtn.setTextColor(getResources().getColor(R.color.black));
                break;
            case 2:
                collectBtn.setTextColor(getResources().getColor(R.color.black));
                break;
        }
    }

    public void updateCollectList(){
        collectsFragment.updateList();
    }
    
    public void updateLikedList(){
        likedFragment.updateList();
    }

    public void updateNoteList(){
        noteList = (List) getArguments().getSerializable(Config.NOTE_LIST);
        noteFragment.getArguments().putSerializable(Config.NOTE_LIST, (Serializable) noteList);
        noteFragment.updateList();
    }
}
