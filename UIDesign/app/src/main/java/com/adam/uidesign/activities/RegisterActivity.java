package com.adam.uidesign.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.adam.uidesign.R;
import com.adam.uidesign.singletons.DiscreteValues;
import com.adam.uidesign.singletons.MyJSONRequestSingleton;
import com.adam.uidesign.singletons.MyUserSingleton;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends Activity {

    //    public static final String HOSTURL = "http://ehireme.herokuapp.com/";
    public static final String HOSTURL = "http://10.27.235.202:3000/";
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private int targetH;
    private int targetW;

    private MyUserSingleton.UserType type;

    // UI references.
    private EditText mApplicantEmail;
    private EditText mApplicantName;
    private EditText mApplicantPassword;
    private View mProgressView;
    private View mWholeView;
    private View mApplicantView;
    private View mEmployerView;
    private EditText mApplicantDob;
    private EditText mApplicantAge;
    private EditText mApplicantCity;
    private EditText mApplicantState;
    private EditText mApplicantBio;
    private EditText mApplicantConfirmPass;
    private ImageView mApplicantImage;

    private EditText mCompanyName;
    private EditText mCompanyDescription;
    private EditText mCompanyEmail;
    private ImageView mCompanyImage;
    private EditText mCompanyPassword;
    private EditText mCompanyConfirmPass;

    private Spinner mApplicantTitle;
    private ArrayAdapter<String> mTitleAdapter;
    private String titleToSend;
    private Spinner mApplicantTitleExperience;
    private ArrayAdapter<String> mTitleExpAdapter;
    private String titleExpToSend;
    private Spinner mApplicantField;
    private ArrayAdapter<String> mFieldAdapter;
    private String fieldToSend;
    private Spinner mApplicantFieldExperience;
    private ArrayAdapter<String> mFieldExpAdapter;
    private String fieldExpToSend;

    private Bitmap profilePhoto;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set up the login form.
        mApplicantEmail = (EditText) findViewById(R.id.applicant_email);
        mApplicantName = (EditText) findViewById(R.id.applicant_name);
        mApplicantPassword = (EditText) findViewById(R.id.applicant_password);
        mApplicantDob = (EditText) findViewById(R.id.applicant_dob);
        mApplicantAge = (EditText) findViewById(R.id.applicant_age);
        mApplicantCity = (EditText) findViewById(R.id.applicant_city);
        mApplicantState = (EditText) findViewById(R.id.applicant_state);
        mApplicantBio = (EditText) findViewById(R.id.applicant_bio);
        mApplicantConfirmPass = (EditText) findViewById(R.id.applicant_confirm_pass);
        mApplicantImage = (ImageView) findViewById(R.id.applicant_photo);
        mCompanyName = (EditText) findViewById(R.id.company_name);
        mCompanyDescription = (EditText) findViewById(R.id.company_description);
        mCompanyEmail = (EditText) findViewById(R.id.business_email);
        mCompanyImage = (ImageView) findViewById(R.id.company_photo);
        mCompanyPassword = (EditText) findViewById(R.id.company_password);
        mCompanyConfirmPass = (EditText) findViewById(R.id.company_confirm_pass);
        mEmployerView = findViewById(R.id.employerview);
        mApplicantView = findViewById(R.id.applicantview);

        //Initialize spinners and adapters
        mApplicantTitle = (Spinner) findViewById(R.id.applicant_title);
        mApplicantTitleExperience = (Spinner) findViewById(R.id.applicant_title_experience);
        mApplicantField = (Spinner) findViewById(R.id.applicant_field);
        mApplicantFieldExperience = (Spinner) findViewById(R.id.applicant_field_experience);
        mTitleAdapter = new ArrayAdapter<>(this, R.layout.single_attribute, R.id.single_attr);
        mTitleAdapter.setDropDownViewResource(R.layout.single_attribute);
        mFieldAdapter = new ArrayAdapter<>(this, R.layout.single_attribute, R.id.single_attr);
        mFieldAdapter.setDropDownViewResource(R.layout.single_attribute);
        mTitleExpAdapter = new ArrayAdapter<>(this, R.layout.single_attribute, R.id.single_attr);
        mTitleExpAdapter.setDropDownViewResource(R.layout.single_attribute);
        mFieldExpAdapter = new ArrayAdapter<>(this, R.layout.single_attribute, R.id.single_attr);
        mFieldExpAdapter.setDropDownViewResource(R.layout.single_attribute);

        titleExpToSend = DiscreteValues.EXPERIENCE_LEVELS.get(0);
        mTitleExpAdapter.addAll(DiscreteValues.EXPERIENCE_LEVELS);
        mApplicantTitleExperience.setAdapter(mTitleExpAdapter);
        mApplicantTitleExperience.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                titleExpToSend = mTitleExpAdapter.getItem(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fieldExpToSend = DiscreteValues.EXPERIENCE_LEVELS.get(0);
        mFieldExpAdapter.addAll(DiscreteValues.EXPERIENCE_LEVELS);
        mApplicantFieldExperience.setAdapter(mFieldExpAdapter);
        mApplicantFieldExperience.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fieldExpToSend = mFieldExpAdapter.getItem(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mFieldAdapter.addAll(DiscreteValues.FIELDS_TO_TITLES.keySet());
        mApplicantField.setAdapter(mFieldAdapter);
        mApplicantField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fieldToSend = mFieldAdapter.getItem(position);
                mTitleAdapter.clear();
                mTitleAdapter.addAll(DiscreteValues.FIELDS_TO_TITLES.get(fieldToSend));
                mApplicantTitle.setAdapter(mTitleAdapter);
                mApplicantTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        titleToSend = mTitleAdapter.getItem(position);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        targetH = 0;
        targetW = 0;
        type = MyUserSingleton.UserType.APPLICANT;

        Button mEmailSignInButton = (Button) findViewById(R.id.register_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type == MyUserSingleton.UserType.APPLICANT) {attemptRegisterApplicant();}
                else {attemptRegisterEmployer();}
            }
        });

        Button mChooseAppPhoto = (Button) findViewById(R.id.applicant_upload_img_btn);
        mChooseAppPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        Button mChooseCompPhoto = (Button) findViewById(R.id.upload_company_img_btn);
        mChooseCompPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        mWholeView = findViewById(R.id.registerlayout);
        mProgressView = findViewById(R.id.login_progress);
    }

    public void onRadioButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()){
            case R.id.applicantBtn:
                if (checked){
                    type = MyUserSingleton.UserType.APPLICANT;
                    mEmployerView.setVisibility(View.GONE);
                    mApplicantView.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.employerBtn:
                if (checked) {
                    type = MyUserSingleton.UserType.EMPLOYER;
                    mApplicantView.setVisibility(View.GONE);
                    mEmployerView.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic();
        } else {
            Toast.makeText(RegisterActivity.this, "We had trouble getting to your camera :(", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timestamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    private void setPic() {
        if (targetH == 0 && targetW == 0) {
            targetH = mApplicantImage.getHeight();
            targetW = mApplicantImage.getWidth();
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        profilePhoto = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        profilePhoto = cropToSquare(profilePhoto);

        ExifInterface exifInterface;
        int orientation = 0;
        try {
            exifInterface = new ExifInterface(mCurrentPhotoPath);
            orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("Orientation:", orientation + "");
        if (orientation == 8) {
            Matrix matrix = new Matrix();
            matrix.postRotate(-90);
            profilePhoto = Bitmap.createBitmap(profilePhoto, 0, 0, profilePhoto.getWidth(), profilePhoto.getHeight(), matrix, true);
        }
        else if (orientation == 6)
        {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            profilePhoto = Bitmap.createBitmap(profilePhoto, 0, 0, profilePhoto.getWidth(), profilePhoto.getHeight(), matrix, true);
        }

        if (type == MyUserSingleton.UserType.APPLICANT) { mApplicantImage.setImageBitmap(profilePhoto); }
        else {mCompanyImage.setImageBitmap(profilePhoto);}
    }

    public static Bitmap cropToSquare(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width) ? height - (height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0) ? 0 : cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0) ? 0 : cropH;
        return Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegisterApplicant() {
        mApplicantEmail.setError(null);
        mApplicantPassword.setError(null);
        mApplicantName.setError(null);
        mApplicantDob.setError(null);
        mApplicantAge.setError(null);
        mApplicantCity.setError(null);
        mApplicantState.setError(null);
        mApplicantBio.setError(null);
        mApplicantConfirmPass.setError(null);

        // Store values at the time of the login attempt.
        JSONObject applicantInfo = new JSONObject();
        try {
            applicantInfo.put("email", mApplicantEmail.getText().toString().trim());
            applicantInfo.put("password", mApplicantPassword.getText().toString().trim());
            applicantInfo.put("confirmPass", mApplicantConfirmPass.getText().toString().trim());
            applicantInfo.put("name", mApplicantName.getText().toString().trim());
            applicantInfo.put("dob", mApplicantDob.getText().toString().trim());
            applicantInfo.put("age", mApplicantAge.getText().toString().trim());
            applicantInfo.put("city", mApplicantCity.getText().toString().trim());
            applicantInfo.put("state", mApplicantState.getText().toString().trim());
            applicantInfo.put("bio", mApplicantBio.getText().toString().trim());
            applicantInfo.put("title", titleToSend);
            applicantInfo.put("title_experience", titleExpToSend);
            applicantInfo.put("field", fieldToSend);
            applicantInfo.put("field_experience", fieldExpToSend);

            boolean cancel = false;
            View focusView = null;

            // Check for a valid password, if the user entered one.
            if (TextUtils.isEmpty((String) applicantInfo.get("password"))) {
                mApplicantPassword.setError(getString(R.string.error_invalid_password));
                focusView = mApplicantPassword;
                cancel = true;
            } else if (!(applicantInfo.get("password")).equals(applicantInfo.get("confirmPass"))) {
                mApplicantConfirmPass.setError(getString(R.string.matchpass));
                focusView = mApplicantConfirmPass;
                cancel = true;
            }

            // Check for a valid email address.
            if (TextUtils.isEmpty((String) applicantInfo.get("email"))) {
                mApplicantEmail.setError(getString(R.string.error_field_required));
                focusView = mApplicantEmail;
                cancel = true;
            } else if (!isEmailValid((String) applicantInfo.get("email"))) {
                mApplicantEmail.setError(getString(R.string.error_invalid_email));
                focusView = mApplicantEmail;
                cancel = true;
            }
            if (TextUtils.isEmpty((String) applicantInfo.get("name"))) {
                mApplicantName.setError(getString(R.string.error_field_required));
                focusView = mApplicantName;
                cancel = true;
            }
            if (TextUtils.isEmpty((String) applicantInfo.get("dob"))) {
                mApplicantDob.setError(getString(R.string.error_field_required));
                focusView = mApplicantDob;
                cancel = true;
            }
            if (TextUtils.isEmpty((String) applicantInfo.get("age"))) {
                mApplicantAge.setError(getString(R.string.error_field_required));
                focusView = mApplicantAge;
                cancel = true;
            }
            if (TextUtils.isEmpty((String) applicantInfo.get("city"))) {
                mApplicantCity.setError(getString(R.string.error_field_required));
                focusView = mApplicantCity;
                cancel = true;
            }
            if (TextUtils.isEmpty((String) applicantInfo.get("state"))) {
                mApplicantState.setError(getString(R.string.error_field_required));
                focusView = mApplicantState;
                cancel = true;
            }
            if (TextUtils.isEmpty((String) applicantInfo.get("bio"))) {
                mApplicantBio.setError(getString(R.string.error_field_required));
                focusView = mApplicantBio;
                cancel = true;
            }

            if (cancel) {
                focusView.requestFocus();
            } else {
                showProgress(true);
                final MyUserSingleton myUserSingleton = MyUserSingleton.getOneTimeInstance(applicantInfo, type);

                final SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCES_NAME), Context.MODE_PRIVATE);
                final Intent intent = new Intent(RegisterActivity.this, MainActivity.class);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                        RegisterActivity.HOSTURL + "applicants/register", applicantInfo, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        SharedPreferences.Editor editor = preferences.edit();
                        try {
                            Log.e("response", response.toString());
                            editor.putString(getString(R.string._id), (String) response.get("_id"));
                            myUserSingleton.addId(response.getString("_id"));
                            editor.putString("name", (String) response.get("name"));
                            editor.putString("email", (String) response.get("email"));
                            editor.putString("type", "APPLICANT");
                            editor.apply();
                            intent.putExtra("Name", (String) response.get("name"));
                            intent.putExtra("Email", (String) response.get("email"));
                            intent.putExtra("type", "Applicant");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        addPhoto(preferences, intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this, "Unable to register account", Toast.LENGTH_SHORT).show();
                        showProgress(false);
                        error.printStackTrace();
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                MyJSONRequestSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void attemptRegisterEmployer() {
        mCompanyDescription.setError(null);
        mCompanyPassword.setError(null);
        mCompanyName.setError(null);
        mCompanyConfirmPass.setError(null);
        mCompanyEmail.setError(null);

        JSONObject employerInfo = new JSONObject();
        try {
            employerInfo.put("company", mCompanyName.getText().toString());
            employerInfo.put("email", mCompanyEmail.getText().toString());
            employerInfo.put("password", mCompanyPassword.getText().toString());
            employerInfo.put("confirmPass", mCompanyConfirmPass.getText().toString());
            employerInfo.put("description", mCompanyDescription.getText().toString());

            boolean cancel = false;
            View focusView = null;
            // Check for a valid password, if the user entered one.
            if (TextUtils.isEmpty((String) employerInfo.get("password"))) {
                mCompanyPassword.setError(getString(R.string.error_invalid_password));
                focusView = mCompanyPassword;
                cancel = true;
            } else if (!(employerInfo.getString("password")).equals(employerInfo.getString("confirmPass"))) {
                Log.e("password", employerInfo.getString("password"));
                Log.e("confirmPass", employerInfo.getString("confirmPass"));
                mCompanyConfirmPass.setError(getString(R.string.matchpass));
                focusView = mCompanyConfirmPass;
                cancel = true;
            }
            if (TextUtils.isEmpty((String) employerInfo.get("company"))) {
                mCompanyName.setError(getString(R.string.error_field_required));
                focusView = mCompanyName;
                cancel = true;
            }
            if (TextUtils.isEmpty((String) employerInfo.get("email"))) {
                mCompanyEmail.setError(getString(R.string.error_field_required));
                focusView = mCompanyEmail;
                cancel = true;
            }
            if (TextUtils.isEmpty((String) employerInfo.get("description"))) {
                mCompanyDescription.setError(getString(R.string.error_field_required));
                focusView = mCompanyDescription;
                cancel = true;
            }

            if (cancel) {
                focusView.requestFocus();
            } else {
                showProgress(true);
                final MyUserSingleton myUserSingleton = MyUserSingleton.getOneTimeInstance(employerInfo, type);
                Log.e("employerinfo", employerInfo.toString());

                final SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCES_NAME), Context.MODE_PRIVATE);
                final Intent intent = new Intent(RegisterActivity.this, MainActivity.class);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                        RegisterActivity.HOSTURL + "employers/register", employerInfo, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        SharedPreferences.Editor editor = preferences.edit();
                        try {
                            Log.e("response", response.toString());
                            editor.putString(getString(R.string._id), (String) response.get("_id"));
                            myUserSingleton.addId(response.getString("_id"));
                            editor.putString("name", (String) response.get("company"));
                            editor.putString("email", (String) response.get("email"));
                            editor.putString("type", "EMPLOYER");
                            editor.apply();
                            intent.putExtra("Name", (String) response.get("company"));
                            intent.putExtra("Email", (String) response.get("email"));
                            intent.putExtra("type", "Employer");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        addPhoto(preferences, intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this, "Unable to register account", Toast.LENGTH_SHORT).show();
                        showProgress(false);
                        error.printStackTrace();
                    }
                });
                MyJSONRequestSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void addPhoto(SharedPreferences preferences, final Intent intent) {
        JSONObject imgInfo = new JSONObject();
        try {
            imgInfo.put("_id", preferences.getString("_id", ""));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            profilePhoto.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte[] arr = byteArrayOutputStream.toByteArray();
            String encodedImage = Base64.encodeToString(arr, Base64.DEFAULT);
            Log.v("_id", imgInfo.getString("_id"));
            imgInfo.put("image", encodedImage);
        } catch (JSONException e) {
            Toast.makeText(RegisterActivity.this, "Sorry, we can't make JSON objects", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        String url = RegisterActivity.HOSTURL;
        url += (type == MyUserSingleton.UserType.APPLICANT) ? "applicants/addPhoto" : "employers/addPhoto";
        Log.e("url", url);
        JsonObjectRequest imagePost = new JsonObjectRequest(Request.Method.POST,
                url, imgInfo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response.toString());
                startActivity(intent);
//                showProgress(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterActivity.this, "Unable to upload photo", Toast.LENGTH_SHORT).show();
                showProgress(false);
                error.printStackTrace();
            }
        });
        MyJSONRequestSingleton.getInstance(this).addToRequestQueue(imagePost);
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return true;
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

            mWholeView.setVisibility(show ? View.GONE : View.VISIBLE);
            mWholeView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mWholeView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mWholeView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

