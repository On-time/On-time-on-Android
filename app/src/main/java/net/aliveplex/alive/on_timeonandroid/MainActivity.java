package net.aliveplex.alive.on_timeonandroid;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.aliveplex.alive.on_timeonandroid.Message.CheckingMessage;
import net.aliveplex.alive.on_timeonandroid.Message.CheckingMessageSerializer;
import net.aliveplex.alive.on_timeonandroid.Message.CheckingStudent;

import org.joda.time.DateTime;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    NfcAdapter mAdapter;
    Button butBack;
    TextView tvSubID;
    String subjectId;
    int subjectSection;
    boolean isRotate = false;
    Uri defaultRingtoneUri;
    HashMap<String, String> currentStudents;
    MediaPlayer mediaPlayer = new MediaPlayer();
    private PendingIntent mPendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isRotate) {
            subjectId = getIntent().getStringExtra("ID");
            subjectSection = getIntent().getIntExtra("Sec", -1);
            isRotate = true;
        }

        Log.d("MainActivity", "section is " + subjectSection);
        defaultRingtoneUri =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        setContentView(R.layout.activity_main);
        tvSubID = (TextView) findViewById(R.id.tvSubID);
        tvSubID.setText(subjectId);
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
        Realm.init(this);

        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        loadStudentDataToMemory();
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

    private void loadStudentDataToMemory() {
        if (currentStudents != null) {
            return;
        }

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final RealmQuery<Student> studentQuery = realm.where(Student.class);
                final RealmResults<Student> result = studentQuery.findAll();
                currentStudents = new HashMap<>();

                for (int i = 0; i < result.size(); i++) {
                    Student student = result.get(i);
                    currentStudents.put(student.getID(), student.getAndroidID());
                }

                Log.i("Load students to memory", "load " + result.size() + " students completed, but current studentsize is " + currentStudents.size());
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this,"Reader is ready.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private class IsoDepProcessor extends AsyncTask<IsoDep, String, String> {

        // This method is called in background thread. please read https://developer.android.com/reference/android/os/AsyncTask.html how to use AsyncTask, it very useful.
        @Override
        protected String doInBackground(IsoDep... params) {
            if (params[0] == null) {
                return null;
            }

            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

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

                    return null;
                }

                result = isoDep.transceive("getid".getBytes("UTF-8"));
                byte[] unknowcommand = "unknowcommand".getBytes("UTF-8");

                if (Arrays.equals(result, unknowcommand)) {
                    return null;
                }
                else {
                    String msg = new String(result, "UTF-8");
                    String[] studentData = msg.split(",");
                    publishProgress("Message is " + msg);

                    if (currentStudents.containsKey(studentData[0])) {
                        Log.i("NFC reader", "List contain key, value is " + studentData[0]);
                        String androidId = currentStudents.get(studentData[0]);
                        if (androidId.equals(studentData[1])) {
                            Log.i("NFC reader", "Saved AnID is " + androidId + " and received AnID is " + studentData[1]);

                            return msg;
                        }
                        else {
                            Log.i("NFC reader", "Saved AnID is " + androidId + " but received AnID is " + studentData[1]);
                            return null;
                        }
                    }
                    else {
                        Log.i("NFC reader", "List doesn't contain key, value is " + studentData[0]);
                        return null;
                    }
                }
            }
            catch (IOException e) {
                Log.d("On-time", "Error with exception" + e.getMessage());
                return null;
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
        protected void onPostExecute(String connectResult) {
            if (connectResult != null) {
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
                String[] studentData = connectResult.split(",");
                DateTime now = new DateTime();
                CheckingStudent checkingStudent = new CheckingStudent(studentData[0], now);
                CheckingMessage checkingMessage = new CheckingMessage(new CheckingStudent[] { checkingStudent }, subjectId, subjectSection);
                SendCheckingData checking = new SendCheckingData(getApplicationContext());
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(CheckingMessage.class, new CheckingMessageSerializer());
                Gson gson = gsonBuilder.create();
                String jsonString = gson.toJson(checkingMessage);
                checking.execute(jsonString);

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

    private static class SendCheckingData extends AsyncTask<String, Void, String> {
        private Context _context;
        private String username;
        private String password;

        public SendCheckingData(Context context) {
            this._context = context;
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
            username = sharedPreferences.getString(Constant.UsernameSpKey, "");
            password = sharedPreferences.getString(Constant.PasswordSpKey, "");
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonString = params[0];

            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            HttpURLConnection urlConnection = null;

            try {
                byte[] bytesBody = jsonString.getBytes("UTF-8");

                urlConnection = (HttpURLConnection)new URL(Constant.CHECKINGSTUDENT_URL).openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                String token = JWTUtil.getInstance().getToken(username, password, true, false);
                urlConnection.setRequestProperty("Authorization", "Bearer " + token);
                urlConnection.setFixedLengthStreamingMode(bytesBody.length);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                out.write(bytesBody);
                out.flush();
                out.close();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                String jsonStringReturn = CharStreams.toString(new InputStreamReader(in, "UTF-8"));
                return jsonStringReturn;
            }
            catch (IllegalStateException ill) {
                return null;
            }
            catch (IOException io) {
                return null;
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }


}
