package com.example.smarthomegesturecontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.VideoView;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    private String item = "LightsOn";
    private boolean mFlgInit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createSpinner();
        createPractice();
    }

    private void createPractice() {
        Button btn = findViewById(R.id.practice);
        btn.setOnClickListener(v -> {

            /* Moving to VideoRecord */
            startActivity(new Intent(getApplicationContext(), RecordActivity.class).putExtra("item", item));
        });
    }

    private void createSpinner(){

        Spinner sp = findViewById(R.id.spnItems);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //Avoiding initial view.
                if (mFlgInit) {
                    mFlgInit = false;
                    return;
                }

                int rawid;
                switch (sp.getSelectedItem().toString()) {
                    case "Turn on lights":
                        rawid = R.raw.h_lighton;
                        item = "LightsOn";
                        break;
                    case "Turn off lights":
                        rawid = R.raw.h_lightoff;
                        item = "LightsOff";
                        break;
                    case "Turn on fan":
                        rawid = R.raw.h_fanon;
                        item = "FanOn";
                        break;
                    case "Turn off fan":
                        rawid = R.raw.h_fanoff;
                        item = "FanOff";
                        break;
                    case "Increase fan speed":
                        rawid = R.raw.h_increasefanspeed;
                        item = "FanUp";
                        break;
                    case "Decrease fan speed":
                        rawid = R.raw.h_decreasefanspeed;
                        item = "FanDown";
                        break;
                    case "Set Thermostat to specified temperature":
                        rawid = R.raw.h_setthermo;
                        item = "SetThermo";
                        break;
                    case "0":
                        rawid = R.raw.h_0;
                        item = "Num0";
                        break;
                    case "1":
                        rawid = R.raw.h_1;
                        item = "Num1";
                        break;
                    case "2":
                        rawid = R.raw.h_2;
                        item = "Num2";
                        break;
                    case "3":
                        rawid = R.raw.h_3;
                        item = "Num3";
                        break;
                    case "4":
                        rawid = R.raw.h_4;
                        item = "Num4";
                        break;
                    case "5":
                        rawid = R.raw.h_5;
                        item = "Num5";
                        break;
                    case "6":
                        rawid = R.raw.h_6;
                        item = "Num6";
                        break;
                    case "7":
                        rawid = R.raw.h_7;
                        item = "Num7";
                        break;
                    case "8":
                        rawid = R.raw.h_8;
                        item = "Num8";
                        break;
                    case "9":
                        rawid = R.raw.h_9;
                        item = "Num9";
                        break;
                    default:
                        return;
                }
                VideoView videoView = findViewById(R.id.videoView);
                videoView.setMediaController(new MediaController(MainActivity.this));
                File videoFile = new File("/storage/emulated/0/Android/data/com.example.smarthomegesturecontrol/files/GestureVideo/" + item + "_PRACTICE_1_MUNEKAGE.mp4");
                String fullPath = "";
                if (!videoFile.exists()) {
                    fullPath = "android.resource://" + getPackageName() + "/" + rawid;
                }else {
                    int i = 1;
                    do {
                        videoFile = new File("/storage/emulated/0/Android/data/com.example.smarthomegesturecontrol/files/GestureVideo/" + item + "_PRACTICE_" + i + "_MUNEKAGE.mp4");
                        if(!videoFile.exists()){
                            videoFile = new File("/storage/emulated/0/Android/data/com.example.smarthomegesturecontrol/files/GestureVideo/" + item + "_PRACTICE_" + (i - 1) + "_MUNEKAGE.mp4");
                            fullPath = videoFile.getAbsolutePath();
                            break;
                        }
                        i++;
                    } while (true);
                }
                videoView.setVideoURI(Uri.parse(fullPath));
                videoView.start();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        VideoView videoView = findViewById(R.id.videoView);
        videoView.setMediaController(new MediaController(MainActivity.this));
        File videoFile;
        int i = 1;
        do {
            videoFile = new File("/storage/emulated/0/Android/data/com.example.smarthomegesturecontrol/files/GestureVideo/" + item + "_PRACTICE_" + i + "_MUNEKAGE.mp4");
            if(!videoFile.exists()){
                videoFile = new File("/storage/emulated/0/Android/data/com.example.smarthomegesturecontrol/files/GestureVideo/" + item + "_PRACTICE_" + (i - 1) + "_MUNEKAGE.mp4");
                break;
            }
            i++;
        } while (true);
        videoView.setVideoURI(Uri.parse(videoFile.getAbsolutePath()));
        videoView.start();
    }
}