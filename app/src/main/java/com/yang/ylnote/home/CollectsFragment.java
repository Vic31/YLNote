package com.yang.ylnote.home;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.yang.ylnote.MyApplication;
import com.yang.ylnote.R;
import com.yang.ylnote.bean.Note;
import com.yang.ylnote.util.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class CollectsFragment extends Fragment {
    public CollectsFragment(){}
    private LinearLayout parentSearch;
    private SearchView searchView;
    private int mColumnCount = 1;
    private List<Note> noteList = new ArrayList<>();
    // store the original list for filter function
    private List<Note> originalList = new ArrayList<>();
    private CollectsFragment.OnCollectsFragmentInteractionListener mListener;
    private RecyclerView.Adapter<CollectsNoteListAdapter.ViewHolder> adapter;
    private RecyclerView recyclerView;

    private SharedPreferences sharedPre;
    private String username;
    private DatabaseReference myRef = MyApplication.getDBInstance().getReference();

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        noteList = (List) getArguments().getSerializable(Config.NOTE_LIST);
//        originalList.clear();
//        if (noteList != null) {
//            originalList.addAll(noteList);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_collects, container, false);
        // Set the adapter
        Context context = rootView.getContext();
        parentSearch = (LinearLayout) rootView.findViewById(R.id.search_parent);
        parentSearch.requestFocus();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        searchView = (SearchView) rootView.findViewById(R.id.search_view);
        searchView.onActionViewExpanded();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(mColumnCount, StaggeredGridLayoutManager.VERTICAL));
        }
        sharedPre = getActivity().getSharedPreferences("config", MODE_PRIVATE);
        username = sharedPre.getString("username", "");
        getCollectListFromDb();
        return rootView;
    }

    private void setComponentsAfterInfo(){
        adapter = new CollectsNoteListAdapter(noteList, mListener, getActivity());
        recyclerView.setAdapter(adapter);
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
//        noteList = (List) getArguments().getSerializable(Config.NOTE_LIST);
//        originalList.clear();
//        if (noteList!=null) {
//            originalList.addAll(noteList);
//        }
        if (context instanceof CollectsFragment.OnCollectsFragmentInteractionListener) {
            mListener = (CollectsFragment.OnCollectsFragmentInteractionListener) context;
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

    public interface OnCollectsFragmentInteractionListener{
        // TODO: Update argument type and name
        void onCollectsFragmentInteraction(Note item);
    }

    public void updateList(){
//        noteList = (List) getArguments().getSerializable(Config.NOTE_LIST);
        getCollectListFromDb();
        adapter.notifyDataSetChanged();
    }

    //get collect id list
    private void getCollectListFromDb(){
        myRef.child("collect_note_list").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String noteId = dataSnapshot.getValue().toString();
                getCollectFromDb(noteId.split(","));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("PostList", "get note list: onCancelled", databaseError.toException());
            }
        });
    }

    //according to id list, get all note data
    private void getCollectFromDb(final String[] noteIdArray){
        myRef.child("note").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noteList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (Arrays.asList(noteIdArray).contains(data.getKey())){
                        Note note = data.getValue(Note.class);
                        note.setNote_id(data.getKey());
                        noteList.add(note);
                    }
                }
                originalList.clear();
                if (noteList!=null) {
                    originalList.addAll(noteList);
                }
                setComponentsAfterInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("CollectList", "get collect list: onCancelled", databaseError.toException());
            }
        });
    }
}
