package com.aueb.podshare;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.kofigyan.stateprogressbar.StateProgressBar;

public class UploadEpisodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_episode);
        StateProgressBar stateProgressBar = (StateProgressBar) findViewById(R.id.step_bar);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.new_podcast_choice_button:
                if (checked)
                    break;
            case R.id.existing_podcast_choice_button:
                if (checked)
                    break;
        }
    }
}
