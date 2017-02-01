package net.aliveplex.alive.on_timeonandroid;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.aliveplex.alive.on_timeonandroid.Message.RegisterReturnStatus;
import net.aliveplex.alive.on_timeonandroid.Message.RegisterReturnStatusDeserializer;
import net.aliveplex.alive.on_timeonandroid.Message.SimpleStudent;
import net.aliveplex.alive.on_timeonandroid.Message.SimpleSubject;
import net.aliveplex.alive.on_timeonandroid.Message.SimpleSubjectStudent;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static android.R.id.list;
import static android.text.TextUtils.isEmpty;

public class MenuActivity extends AppCompatActivity {
    Dialog login;
    Button butLogin,butClear;
    EditText etUser,etPass;
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        login = new Dialog(MenuActivity.this);
        login.setContentView(R.layout.activity_login);
        login.setTitle("Login");
        login.setCancelable(true);

        etUser = (EditText) login.findViewById(R.id.etUser);
        etPass = (EditText) login.findViewById(R.id.etPass);
        butLogin = (Button) login.findViewById(R.id.butLogin);
        butClear = (Button) login.findViewById(R.id.butClear);
        lv = (ListView) findViewById(R.id.lv);

        final String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


        butLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                String username = etUser.getText().toString();
                String password = etPass.getText().toString();

                if (!isEmpty(username) && !isEmpty(password)) {
                    SendRegisterAsync regisTask = new SendRegisterAsync(MenuActivity.this);
                    regisTask.execute(new RegisterInfo(username, password, androidId));
                    login.dismiss();
                } else {
                    Toast.makeText(MenuActivity.this, "username or password empty", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(MenuActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            }
        });

        butClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etUser.setText("");
                etPass.setText("");
            }
        });

        login.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView text1 = (TextView) view.findViewById(R.id.text1);
                TextView text2 = (TextView) view.findViewById(R.id.text2);
                String subid = text1.getText().toString();
                int sec = Integer.parseInt(text2.getText().toString());
                Intent gonext = new Intent(MenuActivity.this,MainActivity.class);
                Log.d("list view item", "Id is " + subid + " section is " + sec);
                gonext.putExtra("ID",subid);
                gonext.putExtra("Sec",sec);
                startActivity(gonext);
            }
        });

        Realm.init(this);
        LoginCheck();
        shouldSetTable();
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

    protected void LoginCheck() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        if(sp.getInt(Constant.FIRSTTIMELOGIN, 0) == 0){
            login.show();
        }
    }

    private static class SendRegisterAsync extends AsyncTask<RegisterInfo, String, String> {
        private Context _context;
        private RegisterInfo _regisInfo;

        public SendRegisterAsync(Context context) {
            _context = context;
        }

        @Override
        protected String doInBackground(RegisterInfo... params) {
            if (params[0] == null) {
                throw new IllegalArgumentException("argument is null.");
            }

            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            RegisterInfo info = params[0];
            _regisInfo = info;
            HttpURLConnection urlConnection = null;

            try {
                Map<String, String> requestBody = new HashMap<>();
                requestBody.put("username", info.getUsername());
                requestBody.put("password", info.getPassword());
                requestBody.put("androidId", info.getAndroidId());
                byte[] bytesBody = getQuery(requestBody).getBytes("UTF-8");

                urlConnection = (HttpURLConnection)new URL(Constant.REGISTER_URL).openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setFixedLengthStreamingMode(bytesBody.length);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                out.write(bytesBody);
                out.flush();
                out.close();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                return CharStreams.toString(new InputStreamReader(in, "UTF-8"));
            }
            catch (IllegalStateException ill) {
                publishProgress("Error: " + ill.getMessage());
                return null;
            }
            catch (IOException io) {
                publishProgress("Error: " + io.getMessage());

                return null;
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String jsonString) {
            if (jsonString == null) {
                return;
            }

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(RegisterReturnStatus.class, new RegisterReturnStatusDeserializer());
            Gson gson = gsonBuilder.create();
            final RegisterReturnStatus result = gson.fromJson(jsonString, RegisterReturnStatus.class);

            if (result == null) {
                Toast.makeText(_context, "Error: can't process result", Toast.LENGTH_SHORT).show();
                return;
            }

            if (result.getStatus().equals("register lecturer completed")) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(_context);
                SharedPreferences.Editor spEditor = sp.edit();

                spEditor.putString(Constant.UsernameSpKey, _regisInfo.getUsername());
                spEditor.putString(Constant.PasswordSpKey, _regisInfo.getPassword());
                spEditor.putString(Constant.AndroidIdSpKey, _regisInfo.getAndroidId());
                spEditor.putInt(Constant.FIRSTTIMELOGIN, 1);
                spEditor.apply();
                // process result of register here
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        //Student student = realm.createObject(Student.class);
                        for (int i = 0; i < result.getStudents().length; i++) {
                            Student student = realm.createObject(Student.class, result.getStudents()[i].getId().toString());
                            //student.setID(result.getStudents()[i].getId().toString());
                            if (result.getStudents()[i].getAndroidId() != null) {
                                student.setAndroidID(result.getStudents()[i].getAndroidId().toString());
                            }
                        }
                        //Subject subject = realm.createObject(Subject.class);
                        for (int i = 0; i < result.getSubjects().length; i++) {
                            Subject subject = realm.createObject(Subject.class, result.getSubjects()[i].getId()+","+result.getSubjects()[i].getSection());
                            //subject.setID(result.getSubjects()[i].getId()+","+result.getSubjects()[i].getSection());
                        }
                        //SubjectStudent subjectstudent = realm.createObject(SubjectStudent.class);
                        for (int i = 0; i < result.getSubjectStudents().length; i++) {
                            SubjectStudent subjectstudent = realm.createObject(SubjectStudent.class, result.getSubjectStudents()[i].getStudentId()+","+result.getSubjectStudents()[i].getSubjectId()+","+result.getSubjectStudents()[i].getSubjectSection());
                            //subjectstudent.setID(result.getSubjectStudents()[i].getStudentId()+","+result.getSubjectStudents()[i].getSubjectId()+","+result.getSubjectStudents()[i].getSubjectSection());
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        ((MenuActivity)_context).setTable();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {

                    }
                });

                Toast.makeText(_context, "register completed", Toast.LENGTH_SHORT).show();
            }
            else if (result.getStatus().equals("username or password invalid")) {
                Toast.makeText(_context, "username or password invalid", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(_context, "student not found", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (values.length > 0) {
                Toast.makeText(_context, values[0], Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(_context, "Error when made request", Toast.LENGTH_SHORT).show();
            }
        }

        private String getQuery(Map<String, String> map)
        {
            String output = null;
            Uri.Builder builder = new Uri.Builder();

            if (map != null) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    builder.appendQueryParameter(entry.getKey(), entry.getValue());
                }

                output = builder.build().getEncodedQuery();
            }

            return output;
        }
    }
    private static class RegisterInfo {
        public RegisterInfo(String username, String password, String androidId) {
            this.androidId = androidId;
            this.password = password;
            this.username = username;
        }

        public String getAndroidId() {
            return androidId;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        private String username;
        private String password;
        private String androidId;
    }

    private void shouldSetTable() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        if(sp.getInt(Constant.FIRSTTIMELOGIN, 0) != 0){
            setTable();
            Log.d("Set table", "table is setting");
        }
    }

    private void setTable() {
        final QuerySubjectResult queryResult = new QuerySubjectResult();
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final RealmQuery<Subject> subject = realm.where(Subject.class);
                final RealmResults<Subject> result = subject.findAll();
                String[] subID = new String[result.size()];
                int[] subsec = new int[result.size()];
                String readdata = "";

                Log.i("List view", "executed ran");
                for (int i = 0; i < result.size(); i++) {
                    HashMap<String,String> myMap = new HashMap<String, String>();
                    readdata = result.get(i).getID();
                    String[] splitLine = readdata.split(",");
                    subID[i] = splitLine[0];
                    subsec[i] = Integer.parseInt(splitLine[1]);

                }
                
                queryResult.setSubId(subID);
                queryResult.setSubSection(subsec);
            }
        }, new OnSubjectQuerySuccess(queryResult) {
            @Override
            public void onSuccess() {
                ListAdapter adapter = new ListAdapter(getApplicationContext(), getResult().getSubId(), getResult().getSubSection());
                Log.i("List view", "reached setAdapter");
                lv.setAdapter(adapter);
                lv.invalidateViews();
                Log.d("Set table", "setting table complete");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d("Set table", "error when setting table: " + error.getMessage());
            }
        });
    }

    private static class QuerySubjectResult {
        private String[] subId;

        public int[] getSubSection() {
            return subSection;
        }

        public void setSubSection(int[] subSection) {
            this.subSection = subSection;
        }

        public String[] getSubId() {
            return subId;
        }

        public void setSubId(String[] subId) {
            this.subId = subId;
        }

        private int[] subSection;


    }

    private static abstract class OnSubjectQuerySuccess implements Realm.Transaction.OnSuccess {
        public QuerySubjectResult getResult() {
            return result;
        }

        private QuerySubjectResult result;

        public OnSubjectQuerySuccess(QuerySubjectResult result) {
            this.result = result;
        }
    }
}
