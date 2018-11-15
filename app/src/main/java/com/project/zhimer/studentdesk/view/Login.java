package com.project.zhimer.studentdesk.view;

import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.project.zhimer.studentdesk.MainActivity;
import com.project.zhimer.studentdesk.R;
import com.project.zhimer.studentdesk.SessionManager;
import com.rey.material.widget.ProgressView;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Login extends AppCompatActivity {

    String nim, password;

    EditText etNim, etPassword;
    Button toastFail, blogin;

    Animation fadein;
    Animation fadeout;
    ProgressView progress;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        sessionManager = new SessionManager(this);

        etNim = findViewById(R.id.etNim);
        etPassword = findViewById(R.id.etPassword);
        blogin = findViewById(R.id.bLogin);

        //animation toast
        toastFail = findViewById(R.id.toast_fail);
        toastFail.setAlpha(0);

        //set animation
        //animation
        fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);
        fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);

        progress = findViewById(R.id.circular);

        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                boolean checkNim = false, checkPassword = false;

                nim = etNim.getText().toString();
                password = etPassword.getText().toString();

                //set condition if user do not input data
                if (nim.isEmpty()) {
                    etNim.setError("Nim tidak boleh kosong");
                } else {
                    checkNim = true;
                }

                if (password.isEmpty()) {
                    etPassword.setError("Password tidak boleh kosong");
                } else {
                    checkPassword = true;
                }

                if (checkNim && checkPassword) {
                    progress.start();
                    sessionManager.setNim(nim);
                    sessionManager.setPassword(password);

                    //TODO Push data login
                    LoggingIn();

                    //Get Token,
                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {

                            String token = task.getResult().getToken();
                            String msg = getString(R.string.msg_token, token);

                            sessionManager.setToken(msg);
                            Log.d("token", msg);
                        }
                    });

                    FirebaseMessaging.getInstance().subscribeToTopic("Akademik");

                    //finish();
                } else {
                    toastFail.setAlpha(1);
                    toastFail.startAnimation(fadein);
                    new BackgroundTask().execute();
                }

                return false;
            }
        });

        blogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean checkNim = false, checkPassword = false;

                nim = etNim.getText().toString();
                password = etPassword.getText().toString();

                //set condition if user do not input data
                if (nim.isEmpty()) {
                    etNim.setError("Nim tidak boleh kosong");
                } else {
                    checkNim = true;
                }

                if (password.isEmpty()) {
                    etPassword.setError("Password tidak boleh kosong");
                } else {
                    checkPassword = true;
                }

                if (checkNim && checkPassword) {
                    progress.start();
                    sessionManager.setNim(nim);
                    sessionManager.setPassword(password);

                    //TODO Push data login
                    LoggingIn();

                    //Get Token,
                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {

                            String token = task.getResult().getToken();
                            String msg = getString(R.string.msg_token, token);

                            sessionManager.setToken(msg);
                            Log.d("token", msg);
                        }
                    });

                    FirebaseMessaging.getInstance().subscribeToTopic("Akademik");

                    //finish();
                } else {
                    toastFail.setAlpha(1);
                    toastFail.startAnimation(fadein);
                    new BackgroundTask().execute();
                }
            }
        });
    }

    private void LoggingIn() {
        String url = sessionManager.getUrl() + "/login/validasi/format/json";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.setBasicAuth(sessionManager.getAuthUsername(), sessionManager.getAuthPassword());
        params.put("uname", sessionManager.getNim());
        params.put("pwd", sessionManager.getPassword());

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    JSONObject object = new JSONObject(response.toString());
                    String logIn = object.getString("status");

                    if (logIn.equals("TRUE")) {
                        //jika true pindah activity
                        Intent login = new Intent(Login.this, MainActivity.class);
                        Login.this.startActivity(login);
                        sessionManager.setLogin(true);
                        progress.stop();
                        finish();
                    } else {
                        showMsgError();
                        sessionManager.setLogin(false);
                        progress.stop();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void showMsgError() {
        toastFail.setAlpha(1);
        toastFail.startAnimation(fadein);
        etNim.setError("");
        etPassword.setError("");
        new BackgroundTask().execute();//timer biar animasinya hilang
    }

    //asynchronus condition
    private class BackgroundTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            toastFail.startAnimation(fadeout);
            toastFail.setAlpha(0);
            etNim.setError(null);
            etPassword.setError(null);
        }
    }
}
