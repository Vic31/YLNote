package com.yang.ylnote.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yang.ylnote.R;
import com.yang.ylnote.bean.Note;
import com.yang.ylnote.util.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class NoteFragment extends Fragment {

    private SearchView searchView;
    private LinearLayout parentSearch;
    private int mColumnCount = 2;
    private List<Note> noteList = new ArrayList<>();
    // store the original list for filter function
    private List<Note> originalList = new ArrayList<>();
    private OnListFragmentInteractionListener mListener;
    private RecyclerView.Adapter<NoteGridAdapter.ViewHolder> adapter;
    private StaggeredGridLayoutManager layoutManager;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NoteFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noteList = (List) getArguments().getSerializable(Config.NOTE_LIST);
        originalList.addAll(noteList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);
        // Set the adapter
        Context context = view.getContext();
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        parentSearch = (LinearLayout) view.findViewById(R.id.search_parent);
        parentSearch.requestFocus();
        searchView = (SearchView) view.findViewById(R.id.search_view);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            //防止item之间来回换位置
//            layoutManager = new StaggeredGridLayoutManager(mColumnCount, StaggeredGridLayoutManager.VERTICAL);
//            layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(mColumnCount, StaggeredGridLayoutManager.VERTICAL));
//            recyclerView.setLayoutManager(layoutManager);
        }
        adapter = new NoteGridAdapter(noteList, mListener, getActivity());
        recyclerView.setAdapter(adapter);
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                layoutManager.invalidateSpanAssignments();
//            }
//        });
        searchView.onActionViewExpanded();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Note> newList = filter(originalList, newText);
                noteList.clear();
                noteList.addAll(newList);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(0);
                return false;
            }
        });
        return view;
    }

    /**
     * filter searching content
     * @param data
     * @param query
     * @return
     */
    private static List<Note> filter(List<Note> data, String query){
        final String q = query.toLowerCase();
        final List<Note> filterList = new ArrayList<>();
        for (Note note: data){
            String text = note.getText().toLowerCase();
            String user = note.getUsername().toLowerCase();
            if (text.contains(q) || user.contains(q)){
                filterList.add(note);
            }
        }
        return filterList;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        noteList = (List) getArguments().getSerializable(Config.NOTE_LIST);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Note item);
    }

    public void updateList(){
        noteList = (List) getArguments().getSerializable(Config.NOTE_LIST);
        adapter.notifyDataSetChanged();
    }
}
