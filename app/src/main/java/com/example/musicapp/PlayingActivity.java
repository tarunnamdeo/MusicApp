package com.example.musicapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PlayingActivity extends AppCompatActivity {
    private String name;
    private TextView textView;
    private TextView stime, etime;
    private Button playpause;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private long currentlength;
    private ImageView imageView;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private int progress;
    private int mCurrentPosition;

    public static String convertDuration(long duration) {
        long minutes = (duration / 1000) / 60;
        long seconds = (duration / 1000) % 60;

        String converted = String.format("%d:%02d", minutes, seconds);//3:7
        return converted;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        textView = findViewById(R.id.song_name);
        textView.setText(name);
        preferences = getSharedPreferences("POS_PLAY", Context.MODE_PRIVATE);
        editor = preferences.edit();
        int resID = getResources().getIdentifier(name, "raw", getPackageName());
        mediaPlayer = MediaPlayer.create(this, resID);
        Log.d("idd", String.valueOf(resID));
        findViews();
        playpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playpause.setText(">");
                    Toast.makeText(PlayingActivity.this, "Music Pause", Toast.LENGTH_SHORT).show();
                } else {
                    mediaPlayer.start();
                    playpause.setText("||");
                    Toast.makeText(PlayingActivity.this, "Music Start", Toast.LENGTH_SHORT).show();
                }
            }
        });

        playermethod();

        Uri mediaPath = Uri.parse("android.resource://" + getPackageName() + "/" + resID);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(this, mediaPath);


        byte[] art = mmr.getEmbeddedPicture();

        if (art != null) {
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(art, 0, art.length));
        } else {
            imageView.setImageResource(R.mipmap.ic_launcher);
        }


        if (preferences.contains(("CurrentPosition"))) {
            progress = preferences.getInt("CurrentPosition", 0);
        }


    }

    private void findViews() {
        seekBar = findViewById(R.id.musicseek);
        playpause = findViewById(R.id.playpause);
        stime = findViewById(R.id.starttime);
        etime = findViewById(R.id.endtime);
        imageView = findViewById(R.id.art_song);
    }

    private void playermethod() {


        currentlength = mediaPlayer.getDuration();
        stime.setText(convertDuration(currentlength));
        Log.d("length", String.valueOf(currentlength));
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        double seekbarsize = seekBar.getMax();
                        double remain = seekbarsize / 100.0;
                        double val = remain * percent;
                        if (percent < currentlength) {
                            seekBar.setSecondaryProgress((int) val);
                        }


                    }
                });
                toggleplay(mp);


            }
        });

        handleSeekbar();
    }

    private void handleSeekbar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void toggleplay(MediaPlayer mp) {

        if (mp.isPlaying()) {
            mp.stop();
            mp.reset();
        } else {
            mp.start();
            try {
                mp.seekTo(progress * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            final Handler handler = new Handler();
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        seekBar.setMax((int) currentlength / 1000);
                        mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                        int time = mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition();
                        etime.setText(convertDuration(time));
                        handler.postDelayed(this, 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        mediaPlayer.stop();
        editor.putInt("CurrentPosition", mCurrentPosition);
        editor.commit();
        mediaPlayer.release();
        finish();
        super.onBackPressed();

    }
}
