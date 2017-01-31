package net.aliveplex.alive.on_timeonandroid;

import android.icu.util.TimeZone;
import android.net.Uri;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aliveplex on 30/1/2560.
 */

public class JWTUtil {
    private static JWTUtil _instance = new JWTUtil();
    private Calendar expire;
    private String token;

    public String getToken(String username, String password, boolean requestCheckingToken, boolean getNewToken) {
        HttpURLConnection urlConnection = null;

        try {
            if (expire != null && !getNewToken) {
                Calendar current = Calendar.getInstance();
                if (current.compareTo(expire) < 0) {
                    return token;
                }
            }

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("username", username);
            requestBody.put("password", password);
            requestBody.put("requestCheckingToken", String.valueOf(requestCheckingToken));
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

            String jsonString = CharStreams.toString(new InputStreamReader(in, "UTF-8"));
            Gson gson = new Gson();
            RequestTokenMessage message = gson.fromJson(jsonString, RequestTokenMessage.class);
            expire = Calendar.getInstance();
            expire.set(Calendar.SECOND, message.getExpires_in());
            token = message.getAccess_token();

            return token;
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

    public static JWTUtil getInstance() {
        return _instance;
    }

    private static class RequestTokenMessage {
        public String getAccess_token() {
            return access_token;
        }

        public int getExpires_in() {
            return expires_in;
        }

        private String access_token;
        private int expires_in;
    }
}
