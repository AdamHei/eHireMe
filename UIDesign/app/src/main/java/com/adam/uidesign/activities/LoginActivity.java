package com.adam.uidesign.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.app.Activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.adam.uidesign.singletons.MyJSONRequestSingleton;
import com.adam.uidesign.singletons.MyUserSingleton;
import com.adam.uidesign.R;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {
    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private MyUserSingleton.UserType type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getApplicationContext().getSharedPreferences(getString(R.string.PREFERENCES_NAME), Context.MODE_PRIVATE);
        if (preferences.getString("_id", "").length() > 0)
        {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("Name", preferences.getString("name", "Name not found"));
            intent.putExtra("Email", preferences.getString("email", "Email not found"));
            intent.putExtra("type", preferences.getString("type", "Type not found").equals("APPLICANT") ? "Applicant" : "Employer");
            startActivity(intent);
        }
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.regemail);
        mPasswordView = (EditText) findViewById(R.id.password);
        type = MyUserSingleton.UserType.APPLICANT;

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1 && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            String[] perms = {"android.permission.GET_ACCOUNTS", "android.permission.READ_PROFILE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
            int permsRequestCode = 200;
            requestPermissions(perms, permsRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){
            case 200:
                boolean writeAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                Toast.makeText(LoginActivity.this, "Thank you", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void onRadioButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()){
            case R.id.login_app_btn:
                if (checked){
                    type = MyUserSingleton.UserType.APPLICANT;
                }
                break;
            case R.id.login_comp_btn:
                if (checked) {
                    type = MyUserSingleton.UserType.EMPLOYER;
                }
                break;
            case R.id.admin_btn:
                if (checked){
                    type = MyUserSingleton.UserType.ADMIN;
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            JSONObject json = new JSONObject();
            try {
                json.put("email", email);
                json.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = RegisterActivity.HOSTURL;
            switch (type){
                case APPLICANT:
                    url += "applicants/login";
                    break;
                case EMPLOYER:
                    url += "employers/login";
                    break;
                case ADMIN:
                    url += "admin/login";
                    break;
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    url, json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCES_NAME), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    try {
                        Log.e("response", response.toString());
                        editor.putString("_id", (String) response.get("_id"));
                        String name = "";
                        String firstTypeToPut = "";
                        String secondTypeToPut = "";
                        switch (type){
                            case APPLICANT:
                                name = "name";
                                firstTypeToPut = "APPLICANT";
                                secondTypeToPut = "Applicant";
                                break;
                            case EMPLOYER:
                                name = "company";
                                firstTypeToPut = "EMPLOYER";
                                secondTypeToPut = "Employer";
                                break;
                            case ADMIN:
                                name = "email";
                                firstTypeToPut = "ADMIN";
                                secondTypeToPut = "Admin";
                                break;
                        }
                        String email = "email";
                        editor.putString("name", (String) response.get(name));
                        editor.putString("email", (String) response.get(email));
                        editor.putString("type", firstTypeToPut);
                        editor.apply();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("Name", (String) response.get(name));
                        intent.putExtra("Email", (String) response.get(email));
                        intent.putExtra("type", secondTypeToPut);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Your credentials were incorrect", Toast.LENGTH_SHORT).show();
                        showProgress(false);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Your credentials were incorrect", Toast.LENGTH_SHORT).show();
                    showProgress(false);
                }
            });
            MyJSONRequestSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

