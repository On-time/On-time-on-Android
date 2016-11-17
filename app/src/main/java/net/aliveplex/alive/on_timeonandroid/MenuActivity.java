package net.aliveplex.alive.on_timeonandroid;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {
    Button butNFC;

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
    }



}
