package com.aueb.podshare;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.aueb.podshare.Sessions.ImageSharedPreference;
import com.aueb.podshare.Sessions.PodcastNameSharedPreference;
import com.aueb.podshare.classes.Podcast;
import com.aueb.podshare.classes.User;
import com.aueb.podshare.utils.BitmapUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class PodcastProfileFragment extends Fragment {


    private ProgressDialog progressDialog;
    private User user;
    private static final String TAG = "PODCAST PROFILE FRAG";
    private View view;
    private byte[] userImage;

    public PodcastProfileFragment(User user) {
        // Required empty public constructor
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.podcast_profile_fragment, container, false);
        showLoading();
        getPodcastDetails();
        return view;
    }

    private void getPodcastDetails() {
        PodcastNameSharedPreference podcastSession = new PodcastNameSharedPreference(getContext());
        String podcastName = podcastSession.getSession();
        ArrayList<Podcast> podcasts = user.getPodcasts();
        Podcast podcast = null;
        for (Podcast pod: podcasts) {
            if (pod.getName().equals(podcastName)) {
                podcast = pod;
            }
        }
        final RelativeLayout relativeImage = view.findViewById(R.id.relative_image);
        TextView podcastNameText = view.findViewById(R.id.podcast_name);
        podcastNameText.setText(podcastName);
        final ImageSharedPreference imageSharedPreference = new ImageSharedPreference(getContext());
        Bitmap bitmap = BitmapUtil.decodeBase64(imageSharedPreference.getSession());
        ImageView imagePodsharer = view.findViewById(R.id.podcast_creator_picture);
        imagePodsharer.setImageBitmap(bitmap);
        TextView podsharerName = view.findViewById(R.id.podcast_creator_name);
        podsharerName.setText(user.getUsername());
        TextView numberEpisodes = view.findViewById(R.id.number_of_episodes);
        numberEpisodes.setText(String.valueOf(podcast.get_number_of_episodes()));
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference userImageRef = storageRef.child("users/" + user.getUid() + "/podcasts/" + podcastName + "/" +podcastName + ".png" );
        final long ONE_MEGABYTE = 1024 * 1024;
        userImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                userImage = bytes;
                Bitmap bitmapPodcast = BitmapFactory.decodeByteArray(userImage, 0, userImage.length);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmapPodcast);
                relativeImage.setBackground(bitmapDrawable);
                dismissLoading();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(getActivity(), "Could not retrieve Podsharer image.", Toast.LENGTH_SHORT).show();
            }
        });
        dismissLoading();
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
