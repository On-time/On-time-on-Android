package net.aliveplex.alive.on_timeonandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MenuActivity extends AppCompatActivity {
    Button butNFC,butTable;
    String studentID;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = PreferenceManager.getDefaultSharedPreferences(MenuActivity.this);
        studentID = sp.getString("et_pr_id","000").toString();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int mu = item.getItemId();
        switch (mu){
            case R.id.menuSettting :
                Intent settingIntent = new Intent(MenuActivity.this,myPreference.class);
                startActivity(settingIntent);
                break;
            case R.id.menuExit :
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                break;
        }
        return true;
    }
}
