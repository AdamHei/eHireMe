package com.adam.uidesign.singletons;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.adam.uidesign.R;
import com.adam.uidesign.activities.RegisterActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Adam on 3/22/2016.
 */
public class MyUserSingleton {
    private static MyUserSingleton mInstance;
    private static Context mContext;
    public enum UserType {APPLICANT, ADMIN, EMPLOYER};
    public UserType type;
    private HashMap<String, String> attributes = new HashMap<>();

    public void addId(String id){
        attributes.put("_id", id);
    }

    public void delete() {mInstance = null;}

    private MyUserSingleton(Context context){
        mContext = context;
        attributes = new HashMap<>();
        SharedPreferences preferences = mContext.getSharedPreferences(mContext.getString(R.string.PREFERENCES_NAME), Context.MODE_PRIVATE);
        String id = preferences.getString("_id", "");
        addId(id);
        String url = RegisterActivity.HOSTURL;
        switch (preferences.getString("type", "")){
            case "APPLICANT":
                type = UserType.APPLICANT;
                url += "applicants/";
                break;
            case "EMPLOYER":
                type = UserType.EMPLOYER;
                url += "employers/";
                break;
            case "ADMIN":
                type = UserType.ADMIN;
                url += "admin/";
                break;
        }
//        type = preferences.getString("type", "").equals("APPLICANT") ? UserType.APPLICANT : UserType.EMPLOYER;
//        url += (type == UserType.APPLICANT) ? "applicants/" : "employers/";
        url += id;
        Log.e("url", url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                setAttributes(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        MyJSONRequestSingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);
    }

    private MyUserSingleton(UserType type, JSONObject userInfo){
        this.type = type;
        try {
            switch (type){
                case APPLICANT:
                    attributes.put("name", userInfo.getString("name"));
                    attributes.put("dob", userInfo.getString("dob"));
                    attributes.put("city", userInfo.getString("city"));
                    attributes.put("state", userInfo.getString("state"));
                    attributes.put("bio", userInfo.getString("bio"));
                    attributes.put("age", userInfo.getString("age"));
                    attributes.put("title", userInfo.getString("title"));
                    attributes.put("field", userInfo.getString("field"));
                    attributes.put("title_experience", userInfo.getString("title_experience"));
                    attributes.put("field_experience", userInfo.getString("field_experience"));
                    break;
                case EMPLOYER:
                    attributes.put("company", userInfo.getString("company"));
                    attributes.put("business_email", userInfo.getString("email"));
                    attributes.put("description", userInfo.getString("description"));
                    break;
                case ADMIN:
                    attributes.put("email", userInfo.getString("email"));
                    break;
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static synchronized MyUserSingleton getOneTimeInstance(JSONObject toInput, UserType userType){
        if (mInstance == null){
            mInstance = new MyUserSingleton(userType, toInput);
        }
        return mInstance;
    }

    public static synchronized MyUserSingleton getInstance(Context context){
        if (mInstance == null){
            mInstance = new MyUserSingleton(context);
        }
        return mInstance;
    }

    private void setAttributes(JSONObject userInfo){
        try {
            switch (type){
                case APPLICANT:
                    attributes.put("name", userInfo.getString("name"));
                    attributes.put("dob", userInfo.getString("dob"));
                    attributes.put("city", userInfo.getString("city"));
                    attributes.put("state", userInfo.getString("state"));
                    attributes.put("bio", userInfo.getString("bio"));
                    attributes.put("age", userInfo.getString("age"));
                    attributes.put("title", userInfo.getString("title"));
                    attributes.put("field", userInfo.getString("field"));
                    attributes.put("title_experience", userInfo.getString("title_experience"));
                    attributes.put("field_experience", userInfo.getString("field_experience"));
                    break;
                case EMPLOYER:
                    attributes.put("company", userInfo.getString("company"));
                    attributes.put("business_email", userInfo.getString("email"));
                    attributes.put("description", userInfo.getString("description"));
                    break;
                case ADMIN:
                    attributes.put("email", userInfo.getString("email"));
                    break;
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }


    public HashMap<String, String> getAttributes(){
        return new HashMap<>(attributes);
    }
}
