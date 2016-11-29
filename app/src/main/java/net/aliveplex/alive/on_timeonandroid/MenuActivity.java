package net.aliveplex.alive.on_timeonandroid;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MenuActivity extends AppCompatActivity {
    Button butNFC,butTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        butNFC = (Button) findViewById(R.id.butNFC);
        butNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goNFC = new Intent(MenuActivity.this,MainActivity.class);
                startActivity(goNFC);
            }
        });

        butTable = (Button) findViewById(R.id.butTable);
        butTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }




}
