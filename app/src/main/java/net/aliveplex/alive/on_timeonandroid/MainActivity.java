package net.aliveplex.alive.on_timeonandroid;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    NfcAdapter mAdapter;
    Button butBack;
    private PendingIntent mPendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resolveIntent(getIntent());
        butBack = (Button) findViewById(R.id.butBack);
        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goBack = new Intent(MainActivity.this,MenuActivity.class);
                startActivity(goBack);
            }
        });

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            finish();
            return;
        }

        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            IsoDep isoDep = IsoDep.get(tagFromIntent);

            new IsoDepProcessor().execute(isoDep);
        }
    }

    private class IsoDepProcessor extends AsyncTask<IsoDep, Void, Boolean> {

        @Override
        protected Boolean doInBackground(IsoDep... params) {
            if (params[0] == null) {
                return false;
            }

            IsoDep isoDep = params[0];
            try {
                isoDep.connect();
                byte[] selectAID = {
                        (byte)0x80, // proprietary class
                        (byte)0xa4, // select instruction
                        (byte)0x04, // select by DF - AID
                        (byte)0x0c, // First or only - no FCI
                        (byte)0x06, // Lc field - have 6 bytes of command data
                        (byte)0xF0, (byte)0x0E, (byte)0x85
                };

                byte[] result = isoDep.transceive(selectAID);
                isoDep.close();

                if (result[0] == (byte)0x90) {
                    return true;
                }
                else {
                    return false;
                }

            }
            catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean connectResult) {
            if (connectResult) {
                Toast.makeText(MainActivity.this,"NFC found.", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(MainActivity.this,"IO failure, operation cancel or can't select AID.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
