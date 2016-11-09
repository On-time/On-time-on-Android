package net.aliveplex.alive.on_timeonandroid;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resolveIntent(getIntent());

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
            }
            catch (IOException e) {

            }
        }
    }
}
