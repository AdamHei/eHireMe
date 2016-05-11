package com.adam.uidesign.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adam.uidesign.R;
import com.adam.uidesign.activities.RegisterActivity;
import com.adam.uidesign.singletons.MyJSONRequestSingleton;
import com.adam.uidesign.singletons.MyUserSingleton;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Applicant_Profile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Applicant_Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Applicant_Profile extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String USER_ID = "param1";
    private static final String JOB_ID = "param2";

    public String job_id;

    private String mParam1;
    private String mParam2;

    private TextView blanksname;
    private TextView email;
    private TextView city;
    private TextView state;
    private TextView bio;
    private TextView field;
    private TextView title;
    private TextView field_experience;
    private TextView title_experience;
    private ImageView profilePic;
    private TextView accepted;

    private Button acceptbtn;

    private OnFragmentInteractionListener mListener;

    public Applicant_Profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Applicant_Profile.
     */
    public static Applicant_Profile newInstance(String user_id) {
        Applicant_Profile fragment = new Applicant_Profile();
        Bundle args = new Bundle();
        args.putString(USER_ID, user_id);
//        args.putString(JOB_ID, job_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_applicant__profile, container, false);

        blanksname = (TextView) view.findViewById(R.id.applicants_profile);
        email = (TextView) view.findViewById(R.id.app_email);
        city = (TextView) view.findViewById(R.id.app_city);
        state = (TextView) view.findViewById(R.id.app_state);
        bio = (TextView) view.findViewById(R.id.app_bio);
        field = (TextView) view.findViewById(R.id.app_field);
        title = (TextView) view.findViewById(R.id.app_title);
        field_experience = (TextView) view.findViewById(R.id.app_field_experience);
        title_experience = (TextView) view.findViewById(R.id.app_title_experience);
        profilePic = (ImageView) view.findViewById(R.id.applicant_pic);
        accepted = (TextView) view.findViewById(R.id.accepted);

        fetchApplicantData();

        acceptbtn = (Button) view.findViewById(R.id.accept);
        acceptbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accept();
                swapViews(true);
            }
        });

        didAccept();

        return view;
    }

    private void didAccept(){
        JSONObject ids = new JSONObject();
        JsonObjectRequest jsonObjectRequest = null;
        try {
            ids.put("user_id", MyUserSingleton.getInstance(getActivity()).getAttributes().get("_id"));
            ids.put("job_id", job_id);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, RegisterActivity.HOSTURL + "employers/didAccept",
                    ids, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getBoolean("truthity")){
                            Log.e("Truthity", response.getBoolean("truthity") + "");
                            swapViews(true);
                        }
                        else{
                            swapViews(false);
                            acceptbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    accept();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    swapViews(false);
                    error.printStackTrace();
                }
            });
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        MyJSONRequestSingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }

    private void swapViews(boolean isAccepted){
        if (isAccepted){
            acceptbtn.setVisibility(View.GONE);
            accepted.setVisibility(View.VISIBLE);
        }
        else{
            acceptbtn.setVisibility(View.VISIBLE);
            accepted.setVisibility(View.GONE);
        }
    }

    private void accept(){
        JSONObject ids = new JSONObject();
        JsonObjectRequest jsonObjectRequest = null;
        try {
            ids.put("user_id", getArguments().getString(USER_ID));
            ids.put("job_id", job_id);

            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, RegisterActivity.HOSTURL + "employers/approve",
                    ids, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("accepted response", response.toString());
                    swapViews(true);
                    Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(getActivity(), "We couldn't approve it :(", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyJSONRequestSingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);

    }

    private void fetchApplicantData(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, RegisterActivity.HOSTURL + "applicants/" + getArguments().getString(USER_ID),
                new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    bio.setText(response.getString("bio"));
                    blanksname.setText(response.getString("name") + "'s Profile");
                    state.setText(response.getString("state"));
                    city.setText(response.getString("city"));
                    email.setText(response.getString("email"));
                    field.setText(response.getString("field"));
                    title.setText(response.getString("title"));
                    field_experience.setText(response.getString("field_experience"));
                    title_experience.setText(response.getString("title_experience"));
                    getApplicantPic();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getActivity(), "We couldn't get their data :(", Toast.LENGTH_SHORT).show();
            }
        });
        MyJSONRequestSingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }


    private void getApplicantPic(){
        String url = RegisterActivity.HOSTURL + "applicants/getPhoto/" + getArguments().getString(USER_ID);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                imgurHelper(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getActivity(), "There was an error getting the imgur URL", Toast.LENGTH_SHORT).show();
            }
        });
        MyJSONRequestSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void imgurHelper(String url){
        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>(){
            @Override
            public void onResponse(Bitmap response) {
                profilePic.setImageBitmap(response);
            }
        }, 0, 0, null, null, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getActivity(), "We were unable to grab the applicant's photo", Toast.LENGTH_SHORT).show();
            }
        });
        MyJSONRequestSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(imageRequest);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onApplicantProfileFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onApplicantProfileFragmentInteraction(Uri uri);
    }
}
