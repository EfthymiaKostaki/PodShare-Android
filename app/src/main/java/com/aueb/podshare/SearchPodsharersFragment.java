package com.aueb.podshare;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.aueb.podshare.Sessions.PodsharerNameSharedPreference;
import com.aueb.podshare.adapter.ValueAdapter;
import com.aueb.podshare.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchPodsharersFragment extends Fragment {

    private List<User> mUsers;
    EditText searchBar;
    View view;
    private ProgressDialog progressDialog;
    private ArrayList<String> users = new ArrayList<>();
    private static String TAG = "SEARCH PODSHARERS FRAGMENT";
    private ValueAdapter valueAdapter;
    private ListView mSearchNFilterLv;

    public SearchPodsharersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        showLoading();
        findUsers();
        valueAdapter=new ValueAdapter(users,getActivity());
        view = inflater.inflate(R.layout.search_podsharers_fragment, container, false);
        mSearchNFilterLv = view.findViewById(R.id.list_view);
        mSearchNFilterLv.setAdapter(valueAdapter);
        mSearchNFilterLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // selected item
                String selected = ((TextView) view.findViewById(R.id.txt_listitem)).getText().toString();

                Toast toast = Toast.makeText(getContext(), selected, Toast.LENGTH_SHORT);
                toast.show();
                PodsharerNameSharedPreference podsharer = new PodsharerNameSharedPreference(getContext());
                podsharer.saveSession(selected);
                loadFragment(new PodsharerProfileFragment());
            }
        });

        searchBar = view.findViewById(R.id.search_field_podsharers);
        mUsers = new ArrayList<>();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                valueAdapter.getFilter().filter(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    private void findUsers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").orderBy("username", com.google.firebase.firestore.Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    String username = document.getString("username");
                                    mUsers.add(new User(document.getString("email"),username));
                                    users.add(username);
                                    Log.d(TAG, username);
                                    if (i++ == task.getResult().size() - 1) {
                                        Log.d(TAG, "disconnecting inside iterator");
                                        dismissLoading();
                                    }
                                } catch (Exception e) {
                                    Log.d(TAG, e.toString());
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting users: ", task.getException());
                            Toast.makeText(getContext(), "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showLoading() {
        if (progressDialog == null)
            progressDialog = new ProgressDialog(getContext());

        progressDialog.setMessage(getString(R.string.preparing_results));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    private void dismissLoading() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDestroy() {
        users.clear();
        mUsers.clear();
        super.onDestroy();
    }

}