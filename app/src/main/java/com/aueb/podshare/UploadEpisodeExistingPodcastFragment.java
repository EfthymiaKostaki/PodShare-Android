package com.aueb.podshare;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.aueb.podshare.Sessions.AudioSharedPreference;
import com.aueb.podshare.Sessions.EpisodeDescriptionSharedPreference;
import com.aueb.podshare.Sessions.EpisodeNameSharedPreference;
import com.aueb.podshare.Sessions.ImageSharedPreference;
import com.aueb.podshare.Sessions.PodcastDescriptionSharedPreference;
import com.aueb.podshare.Sessions.PodcastNameSharedPreference;
import com.aueb.podshare.Sessions.PrivacySharedPreference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UploadEpisodeExistingPodcastFragment extends Fragment {
    private Button backButton;
    private Button next;
    private String podcast_name_chosen = "";
    private Button cancel;
    private FirebaseAuth mAuth;
    RadioGroup rgb;
    private static final String TAG = "GET_PODCASTS" ;
    private ArrayList<String> podcasts = new ArrayList<String>();
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        showLoading();
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.upload_episode_existing_podcast_fragment, container, false);
        backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUploadEpisodeActivity();
            }
        });
        next = view.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUploadEpisodeFileActivity();
            }
        });
        cancel = view.findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertUser();
            }
        });
        rgb = view.findViewById(R.id.podcast_choice);
        rgb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Check which radio button was clicked
                RadioGroup radioGroup = getView().findViewById(R.id.podcast_choice);
                int selectedId = radioGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                RadioButton radioButton = (RadioButton) getView().findViewById(selectedId);
                podcast_name_chosen = radioButton.getText().toString();
                Log.d("BUTTON_TEXT", podcast_name_chosen);
                Log.v("On clicked working", "clicado");
            }
        });
        getPodcasts();
        return view;
    }

    private void loadPodcasts() {
        if (podcasts.isEmpty()) {
            Toast.makeText(getActivity(), "No podcasts found. Create a new one instead",
                    Toast.LENGTH_SHORT).show();
            goToUploadEpisodeActivity();
            getActivity().finish();
        } else {
            for (String podcast : podcasts) {
                Log.d("PODCAST BUTTON", podcast);
                RadioButton rb = new RadioButton(getActivity());
                rb.setId(podcasts.indexOf(podcast));
                rb.setChecked(false);
                rb.setFocusable(false);
                rb.setText(podcast);
                rb.setTextColor(Color.WHITE);
                rb.setTextSize(getResources().getDimension(R.dimen.textSmall));
                rb.setButtonDrawable(R.drawable.radiobuttonstyle);
                ScrollView.LayoutParams layoutParams = new ScrollView.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.bottomMargin = 6;
                layoutParams.topMargin = 6;
                rb.setLayoutParams(layoutParams);
                rgb.addView(rb);
            }
            final PodcastNameSharedPreference podcastNameSharedPreference = new PodcastNameSharedPreference(getActivity());
            podcastNameSharedPreference.saveSession(podcast_name_chosen);
            Log.d("session podcast name", podcastNameSharedPreference.getSession());
        }
    }


    private void alertUser() {
        final EpisodeNameSharedPreference episodeNameSharedPreference = new EpisodeNameSharedPreference(getActivity());
        final EpisodeDescriptionSharedPreference episodeDescriptionSharedPreference = new EpisodeDescriptionSharedPreference(getActivity());
        final PodcastNameSharedPreference podcastNameSharedPreference = new PodcastNameSharedPreference(getActivity());
        final PodcastDescriptionSharedPreference podcastDescriptionSharedPreference = new PodcastDescriptionSharedPreference(getActivity());
        final ImageSharedPreference imageSharedPreference = new ImageSharedPreference(getActivity());
        final AudioSharedPreference audioSharedPreference = new AudioSharedPreference(getActivity());
        final PrivacySharedPreference privacySharedPreference = new PrivacySharedPreference(getActivity());
        new AlertDialog.Builder(getActivity())
                .setTitle("Disregard additions")
                .setMessage("Are you sure you want to disregard your additions?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        // DROP ALL SESSION OBJECTS UNTIL NOW FOR THE UPLOAD
                        episodeNameSharedPreference.terminateSession();
                        episodeDescriptionSharedPreference.terminateSession();
                        podcastNameSharedPreference.terminateSession();
                        if (podcastDescriptionSharedPreference != null) {
                            podcastDescriptionSharedPreference.terminateSession();
                            imageSharedPreference.terminateSession();
                        }
                        audioSharedPreference.terminateSession();
                        privacySharedPreference.terminateSession();
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().finish();
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void goToUploadEpisodeFileActivity() {
        PodcastNameSharedPreference podcastNameSharedPreference = new PodcastNameSharedPreference(getActivity());
        PodcastDescriptionSharedPreference podcastDescriptionSharedPreference = new PodcastDescriptionSharedPreference(getActivity());
        if (podcastDescriptionSharedPreference != null) {
            podcastDescriptionSharedPreference.terminateSession();
        }
        podcastNameSharedPreference.saveSession(podcast_name_chosen);
        startActivityForResult(new Intent(getActivity(), UploadEpisodeFileFragment.class), 101);
        getActivity().finish();
    }

    private void goToUploadEpisodeActivity() {
        PodcastNameSharedPreference podcastNameSharedPreference = new PodcastNameSharedPreference(getActivity());
        PodcastDescriptionSharedPreference podcastDescriptionSharedPreference = new PodcastDescriptionSharedPreference(getActivity());
        if (podcastDescriptionSharedPreference != null) {
            podcastDescriptionSharedPreference.terminateSession();
        }
        podcastNameSharedPreference.saveSession(podcast_name_chosen);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new UploadEpisodeFragment());
        fragmentTransaction.commit();
        getActivity().finish();
    }

    public void getPodcasts(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseuser = mAuth.getCurrentUser();
        // save to firebase at the correct folder and redirect to main activity.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("email", firebaseuser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().collection("podcasts")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                        int i = 0;
                                                        for (QueryDocumentSnapshot podcastDocument : task.getResult()) {
                                                            String name = podcastDocument.getString("name");
                                                            podcasts.add(name);
                                                            Log.d(TAG, name);
                                                            if(i++ == task.getResult().size() - 1){
                                                                Log.d(TAG, "disconnecting inside iterator");
                                                                dismissLoading();
                                                                loadPodcasts();
                                                            }
                                                        }
                                                } else {
                                                    Log.d(TAG, "disconnecting when task is not successful");
                                                    dismissLoading();
                                                    Log.d(TAG, "Error getting podcasts: ", task.getException());
                                                    Toast.makeText(getActivity(), "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(getActivity(), "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        //podcasts is null here why
        Log.d("in get podcasts", podcasts.toString());

    }

    private void showLoading() {
        if (progressDialog == null)
            progressDialog = new ProgressDialog(getActivity());

        progressDialog.setMessage(getString(R.string.fetching_podcats));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    private void dismissLoading() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

}
