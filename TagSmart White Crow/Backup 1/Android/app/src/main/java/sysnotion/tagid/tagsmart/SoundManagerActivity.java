package sysnotion.tagid.tagsmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import sysnotion.tagid.tagsmart.utils.Constants;

import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

public class SoundManagerActivity extends AppCompatActivity {
    private AudioManager mAudioManager;
    private SeekBar soundVolumeSB;
    private Switch switchSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_manager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        Constants.mSoundVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int media_max_volume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        soundVolumeSB=(SeekBar) findViewById(R.id.soundVolumeSB);
        soundVolumeSB.setMax(media_max_volume);
        soundVolumeSB.setProgress((int) Constants.mSoundVolume);
        soundVolumeSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                Constants.mSoundVolume = progress;
                mAudioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC, // Stream type
                        progress, // Index
                        AudioManager.FLAG_SHOW_UI // Flags
                );
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(SoundManagerActivity.this, "Current Sound Volume :" + progressChangedValue,
                        Toast.LENGTH_SHORT).show();
            }
        });

        switchSound = (Switch) findViewById(R.id.switchSound);
        if(Constants.mSoundPlay == true)
        {
            switchSound.setChecked(true);
        }
        else
        {
            switchSound.setChecked(false);
        }
        switchSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    Constants.mSoundPlay = true;
                    Toast.makeText(SoundManagerActivity.this, "Sound is enable",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // The toggle is disabled
                    Constants.mSoundPlay = false;
                    Toast.makeText(SoundManagerActivity.this, "Sound is mute",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}