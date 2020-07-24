package com.aueb.podshare;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.aueb.podshare.Sessions.ImageSharedPreference;
import com.aueb.podshare.Sessions.PodsharerNameSharedPreference;
import com.aueb.podshare.adapter.Adapter;
import com.aueb.podshare.classes.Episode;
import com.aueb.podshare.classes.Podcast;
import com.aueb.podshare.classes.User;
import com.aueb.podshare.utils.BitmapUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PodsharerProfileFragment extends Fragment {

    private static final String TAG = "PODSHARER PROFILE FRAG";
    private User user;
    private ProgressDialog progressDialog;
    private byte[] userImage;

    public PodsharerProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        showLoading();
        getUserDetails();
        return inflater.inflate(R.layout.podsharer_profile_fragment, container, false);
    }

    private void getUserDetails() {
        final PodsharerNameSharedPreference podsharer = new PodsharerNameSharedPreference(getContext());
        String podsharerName = podsharer.getSession();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").whereEqualTo("username", podsharerName).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int j = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                user = new User(document.getString("email"),
                                        document.getString("username"), document.getString("description"), document.getString("uid"));
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                document.getReference().collection("podcasts").get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    int i = 0;
                                                    for (QueryDocumentSnapshot podcastDocument : task.getResult()) {
                                                        user.addPodcast(new Podcast(podcastDocument.getString("name"),
                                                                podcastDocument.getString("description"), podcastDocument.getTimestamp("pub_date").toDate()));
                                                        Log.d("adding podcast", podcastDocument.getString("name"));
                                                        final int finalI = i;
                                                        podcastDocument.getReference().collection("episodes").get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        if (task.isSuccessful()) {
                                                                            int k = 0;
                                                                            for (QueryDocumentSnapshot episodeDocument : task.getResult()) {
                                                                                // Possible issue here. How the
                                                                                Episode episode = new Episode(episodeDocument.getString("_name"), episodeDocument.getString("_description"));
                                                                                episode.set_privacy(episodeDocument.getBoolean("_privacy"));
                                                                                episode.setPub_date(episodeDocument.getDate("_pubDate"));
                                                                                user.getPodcasts().get(finalI).addEpisode(episode);
                                                                                Log.d(TAG, episode.get_name());
                                                                                if(k++ == task.getResult().size() - 1){
                                                                                    Log.d(TAG, "disconnecting inside iterator");
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                );
                                                        if(i++ == task.getResult().size() - 1){
                                                            Log.d(TAG, "disconnecting inside iterator");
                                                            setUpPodsharerInfoPodcasts();
                                                        }
                                                    }
                                                    if (i == 0) {
                                                        setUpPodsharerInfoPodcasts();
                                                        Log.d(TAG, " no podcasts");
                                                    }
                                                    // TO DO ADD ALSO EPISODES TO PODCASTS
                                                    FirebaseStorage storage = FirebaseStorage.getInstance();
                                                    StorageReference storageRef = storage.getReference();
                                                    StorageReference userImageRef = storageRef.child("users/" + user.getUid() + "/user.png");
                                                    final long ONE_MEGABYTE = 1024 * 1024;
                                                    userImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                        @Override
                                                        public void onSuccess(byte[] bytes) {
                                                            userImage = bytes;
                                                            setUpPodsharerInfoImage();
                                                            dismissLoading();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception exception) {
                                                            // Handle any errors
                                                            Toast.makeText(getActivity(), "Could not retrieve Podsharer image.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }
                                        });
                            }
                            if(j++ == task.getResult().size() - 1){
                                Log.d(TAG, "disconnecting inside iterator");
                                setUpPodsharerInfoUser();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


    }

    private void setUpPodsharerInfoImage() {
        ImageView podsharerimage = (ImageView) getView().findViewById(R.id.podsharer_image);
        Bitmap bitmap = BitmapFactory.decodeByteArray(userImage, 0, userImage.length);
        podsharerimage.setImageBitmap(bitmap);
        final ImageSharedPreference imageSharedPreference = new ImageSharedPreference(getContext());
        imageSharedPreference.saveSession(BitmapUtil.encodeToBase64(bitmap), "no file extension");
    }

    private void setUpPodsharerInfoUser() {
        TextView podsharerName = (TextView) getView().findViewById(R.id.podsharer_name);
        podsharerName.setText(user.getUsername());
        TextView podsharerDescription = (TextView) getView().findViewById(R.id.podsharer_description);
        podsharerDescription.setText(user.getDescription());

    }
    private void setUpPodsharerInfoPodcasts() {
        TextView numberOfPodcasts = (TextView) getView().findViewById(R.id.number_of_podcasts);
        if (!user.getPodcasts().isEmpty()) {
            numberOfPodcasts.setText(String.valueOf(user.getPodcasts().size()));
        }
        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) getView().findViewById(R.id.viewPagerPodsharer);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) getView().findViewById(R.id.tabLayout);
        tabs.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());
        // to do create fragments for recents and podcasts.
        if (user.getPodcasts().isEmpty()) {
            adapter.addFragment(new NullPodcasts(), "Podcasts");
        } else {
            adapter.addFragment(new PodcastsFragment(user), "Podcasts");
        }
        adapter.addFragment(new RecentsFragment(), "Recents");
        viewPager.setAdapter(adapter);
    }

    private void showLoading() {
        if (progressDialog == null)
            progressDialog = new ProgressDialog(getContext());

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
