package com.adam.uidesign.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.adam.uidesign.R;
import com.adam.uidesign.activities.RegisterActivity;
import com.adam.uidesign.contents.JobItem;
import com.adam.uidesign.singletons.MyJSONRequestSingleton;
import com.adam.uidesign.singletons.MyUserSingleton;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JobDescription.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link JobDescription#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobDescription extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String employer_id;
    private TextView title;
    private TextView company;
    private TextView description;
    private TextView starting_rate;
    private TextView field;
    private TextView title_experience;
    private TextView field_experience;
    private TextView city;
    private TextView state;
    private TextView appliedText;

    private boolean applied;
    private Button applybtn;
    private JobItem job;

    private OnFragmentInteractionListener mListener;

    public JobDescription() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment JobDescription.
     */
    // TODO: Rename and change types and number of parameters
    public static JobDescription newInstance(JobItem item) {
        JobDescription fragment = new JobDescription();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setItem(item);
        return fragment;
    }

    private void setItem(JobItem item){
        job = item;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_job_description, container, false);
        employer_id = job.employer_id;
        (title = (TextView) view.findViewById(R.id.title)).setText(job.title);
        (company = (TextView) view.findViewById(R.id.company)).setText(job.company_name);
        (description = (TextView) view.findViewById(R.id.description)).setText(job.description);
        (starting_rate = (TextView) view.findViewById(R.id.starting_rate)).setText(job.starting_rate);
        (field = (TextView) view.findViewById(R.id.field)).setText(job.field);
        (field_experience = (TextView) view.findViewById(R.id.field_experience)).setText(job.field_experience);
        (title_experience = (TextView) view.findViewById(R.id.title_experience)).setText(job.title_experience);
        (city = (TextView) view.findViewById(R.id.city)).setText(job.city);
        (state = (TextView) view.findViewById(R.id.state)).setText(job.state);
        applybtn = (Button) view.findViewById(R.id.apply);


        if (MyUserSingleton.getInstance(getContext()).type == MyUserSingleton.UserType.ADMIN){
            applybtn.setText("Delete Job");
            applybtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("jobId", job.id);
                        String url = RegisterActivity.HOSTURL + "admin/removeJob";
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            if (response.getBoolean("truthity")){
                                                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                                applybtn.setVisibility(View.GONE);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                Toast.makeText(getContext(), "Unable to delete :(", Toast.LENGTH_SHORT).show();
                            }
                        });
                        MyJSONRequestSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else{
            checkApplied();
        }
        return view;
    }

    private void checkApplied(){
        JSONObject ids = new JSONObject();
        JsonObjectRequest jsonObjectRequest = null;
        try {
            ids.put("user_id", MyUserSingleton.getInstance(getActivity()).getAttributes().get("_id"));
            ids.put("job_id", job.id);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, RegisterActivity.HOSTURL + "applicants/exists", ids,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getBoolean("truthity")){
                                    Log.e("truthity", response.getBoolean("truthity") + "");
                                    applied = true;
                                    applybtn.setText(getString(R.string.unapply));
                                    applybtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            toApplyOrNotToApply();
                                        }
                                    });
                                }
                                else{
                                    applied = false;
                                    applybtn.setText(getString(R.string.apply));
                                    applybtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            toApplyOrNotToApply();
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
                    error.printStackTrace();
                }
            });
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        MyJSONRequestSingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }

    private void toApplyOrNotToApply(){
        JSONObject ids = new JSONObject();
        JsonObjectRequest jsonObjectRequest = null;
        try {
            ids.put("user_id", MyUserSingleton.getInstance(getActivity()).getAttributes().get("_id"));
            ids.put("job_id", job.id);
            String url = RegisterActivity.HOSTURL;
            url += applied ? "applicants/removeMatch" : "applicants/makeMatch";
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                    ids, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("response", response.toString());
                    applybtn.setText(applied ? getString(R.string.apply) : getString(R.string.unapply));
                    applied = !applied;
                    Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(getActivity(), "We couldn't connect/disconnect you :(", Toast.LENGTH_LONG).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyJSONRequestSingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onJobFragmentInteraction(uri);
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
        // TODO: Update argument type and name
        void onJobFragmentInteraction(Uri uri);
    }
}
