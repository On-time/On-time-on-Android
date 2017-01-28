package net.aliveplex.alive.on_timeonandroid;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    NfcAdapter mAdapter;
    Button butBack;
    Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    MediaPlayer mediaPlayer = new MediaPlayer();
    private PendingIntent mPendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        butBack = (Button) findViewById(R.id.butBack);
        butBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goBack = new Intent(MainActivity.this, MenuActivity.class);
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
            IntentFilter techNfc = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
            String[][] techListArray = new String[][] { new String[] { IsoDep.class.getName() }};

            mAdapter.enableForegroundDispatch(this, mPendingIntent, new IntentFilter[] { techNfc }, techListArray);
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

    private class IsoDepProcessor extends AsyncTask<IsoDep, String, Boolean> {

        // This method is called in background thread. please read https://developer.android.com/reference/android/os/AsyncTask.html how to use AsyncTask, it very useful.
        @Override
        protected Boolean doInBackground(IsoDep... params) {
            if (params[0] == null) {
                return false;
            }

            IsoDep isoDep = params[0];
            try {
                // the connect() and transceive() method mustn't be called from UI thread so we call it from background thread
                isoDep.connect();
                // this command have some bytes that can't be explain, specifically, 0x04 and 0x05
                byte[] selectAID = {
                        (byte)0x00, // proprietary class
                        (byte)0xa4, // select instruction
                        (byte)0x04, // select by DF - AID
                        (byte)0x00, // First or only - no FCI
                        (byte)0x05, // Lc field - have 5 bytes of command data
                        (byte)0xF0, 0x00, 0x00, 0x0E, (byte)0x85
                };

                byte[] result = isoDep.transceive(selectAID);

                if (result[0] != (byte)0x90) {

                    return false;
                }

                result = isoDep.transceive("getid".getBytes("UTF-8"));
                byte[] unknowcommand = "unknowcommand".getBytes("UTF-8");

                if (Arrays.equals(result, unknowcommand)) {
                    return false;
                }
                else {
                    return true;
                }
            }
            catch (IOException e) {
                Log.d("On-time", "Error with exception" + e.getMessage());
                return false;
            }
            finally {
                try {
                    isoDep.close();
                }
                catch (IOException ignore){
                }
            }
        }

        // This method is called in MAIN thread aka. UI thread
        @Override
        protected void onPostExecute(Boolean connectResult) {
            if (connectResult) {
                // NFC was found and successfully communicated.
                /*try {
                    mediaPlayer.setDataSource(MainActivity.this, defaultRingtoneUri);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
                    mediaPlayer.prepare();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp)
                        {
                            mp.release();
                        }
                    });
                    mediaPlayer.start();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                Toast.makeText(MainActivity.this,"NFC found.", Toast.LENGTH_LONG).show();
            }
            else {
                // NFC was found but can't communicated with it or card move away from reader while it being read
                Toast.makeText(MainActivity.this,"IO failure, operation cancel or can't select AID.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (values.length > 0) {
                Toast.makeText(MainActivity.this, values[0], Toast.LENGTH_LONG).show();
            }
        }
    }


}
