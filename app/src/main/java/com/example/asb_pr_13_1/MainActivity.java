package com.example.asb_pr_13_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listViewMP3;
    Button btnPlay, btnStop;
    TextView tvMP3, tvTime;
    SeekBar pbMP3;

    ArrayList<String> mp3List;
    String selectedMP3;

    String mp3Path = Environment.getExternalStorageDirectory().getPath()+"/Music/";

    MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("MP3");

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);

        mp3List = new ArrayList<String>();

        File[] listFiles = new File(mp3Path).listFiles();
        String fileName, extName;
        for(File file : listFiles){
            fileName = file.getName();
            Log.d("*******************", fileName);
            extName = fileName.substring(fileName.length() -3);
            if(extName.toLowerCase().equals("mp3")){
                mp3List.add(fileName);
            }
        }

        listViewMP3 = (ListView) findViewById(R.id.listViewMP3);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_single_choice, mp3List);
        listViewMP3.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewMP3.setAdapter(adapter);
        listViewMP3.setItemChecked(0,true);

        listViewMP3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedMP3 = mp3List.get(i);
            }
        });

        //mp3List.add("123");
        if(mp3List.size() != 0){
            selectedMP3 = mp3List.get(0);
        }

        btnPlay = (Button) findViewById(R.id.btnplay);
        btnStop = (Button) findViewById(R.id.btnStop);
        tvMP3 = (TextView) findViewById(R.id.tvMP3);
        tvTime = (TextView) findViewById(R.id.tvTime);

        pbMP3 = (SeekBar) findViewById(R.id.pbMP3);

        btnStop.setClickable(false);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mediaPlayer = new MediaPlayer();

                    //경로 설정
                    mediaPlayer.setDataSource(mp3Path+selectedMP3);
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    btnPlay.setClickable(false);
                    btnStop.setClickable(true);

                    tvMP3.setText("실행중인 음악 : " + selectedMP3);
                    pbMP3.setVisibility(View.VISIBLE);

                    T1 t1 = new T1(pbMP3, mediaPlayer, tvTime);
                    t1.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.reset();

                btnPlay.setClickable(true);
                btnStop.setClickable(false);

                tvMP3.setText("실행중인 음악 : ");
                pbMP3.setVisibility(View.INVISIBLE);

            }
        });

        pbMP3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b == true){
                    mediaPlayer.seekTo(i);
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

    class T1 extends Thread{
        ProgressBar pb;
        MediaPlayer mediaPlayer;
        TextView tvTime;

        SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");

        T1(ProgressBar pb, MediaPlayer mediaPlayer, TextView tvTime){
            this.pb = pb;
            this.mediaPlayer = mediaPlayer;
            this.tvTime = tvTime;
        }

        @Override
        public void run() {
            super.run();
            if(mediaPlayer == null){
                return;
            }else{
                pb.setMax(mediaPlayer.getDuration());
                while(mediaPlayer.isPlaying()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pb.setProgress(mediaPlayer.getCurrentPosition());
                            tvTime.setText("진행시간 : " + timeFormat.format(mediaPlayer.getCurrentPosition()));
                        }
                    });
                    SystemClock.sleep(200);
                }

            }


        }
    }

}



















