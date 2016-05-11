package com.adam.uidesign.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.adam.uidesign.R;
import com.adam.uidesign.activities.RegisterActivity;
import com.adam.uidesign.singletons.DiscreteValues;
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
 * {@link NewJobFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewJobFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewJobFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Spinner title;
    private ArrayAdapter<String> titleAdapter;
    private String titleToSend;

    private EditText description;
    private EditText rate;
    private ArrayAdapter<String> fieldAdapter;
    private Spinner field;
    private String fieldToSend;

    private Spinner titleExp;
    private ArrayAdapter<String> titleExpAdapter;
    private String titleExpToSend;

    private Spinner fieldExp;
    private ArrayAdapter<String> fieldExpAdapter;
    private String fieldExpToSend;

    private EditText city;
    private EditText state;
    private ScrollView addJobView;
    private ProgressBar mProgressView;

    private OnFragmentInteractionListener mListener;

    public NewJobFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewJobFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewJobFragment newInstance(String param1, String param2) {
        NewJobFragment fragment = new NewJobFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        titleAdapter = new ArrayAdapter<>(getContext(), R.layout.single_attribute, R.id.single_attr);
        titleAdapter.setDropDownViewResource(R.layout.single_attribute);
        fieldAdapter = new ArrayAdapter<>(getContext(), R.layout.single_attribute, R.id.single_attr);
        fieldAdapter.setDropDownViewResource(R.layout.single_attribute);
        fieldExpAdapter = new ArrayAdapter<>(getContext(), R.layout.single_attribute, R.id.single_attr);
        fieldExpAdapter.setDropDownViewResource(R.layout.single_attribute);
        titleExpAdapter = new ArrayAdapter<>(getContext(), R.layout.single_attribute, R.id.single_attr);
        titleExpAdapter.setDropDownViewResource(R.layout.single_attribute);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_job, container, false);
        title = (Spinner) view.findViewById(R.id.addTitle);
        description = (EditText) view.findViewById(R.id.addDescription);
        rate = (EditText) view.findViewById(R.id.addRate);
        field = (Spinner) view.findViewById(R.id.addField);
        fieldExp = (Spinner) view.findViewById(R.id.addFieldExp);
        titleExp = (Spinner) view.findViewById(R.id.addTitleExp);
        city = (EditText) view.findViewById(R.id.addCity);
        state = (EditText) view.findViewById(R.id.addState);
        addJobView = (ScrollView) view.findViewById(R.id.addJobList);
        mProgressView = (ProgressBar) view.findViewById(R.id.add_job_progress);

        Button pushJob = (Button) view.findViewById(R.id.addJobBtn);
        pushJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAddJob();
            }
        });

        titleExpToSend = DiscreteValues.EXPERIENCE_LEVELS.get(0);
        titleExpAdapter.addAll(DiscreteValues.EXPERIENCE_LEVELS);
        titleExp.setAdapter(titleExpAdapter);
        titleExp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                titleExpToSend = titleExpAdapter.getItem(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fieldExpToSend = DiscreteValues.EXPERIENCE_LEVELS.get(0);
        fieldExpAdapter.addAll(DiscreteValues.EXPERIENCE_LEVELS);
        fieldExp.setAdapter(fieldExpAdapter);
        fieldExp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fieldExpToSend = fieldExpAdapter.getItem(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fieldAdapter.addAll(DiscreteValues.FIELDS_TO_TITLES.keySet());
        fieldToSend = fieldAdapter.getItem(0);
        titleToSend = DiscreteValues.FIELDS_TO_TITLES.get(fieldToSend).get(0);
        field.setAdapter(fieldAdapter);
        field.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fieldToSend = fieldAdapter.getItem(position);
                titleAdapter.clear();
                titleAdapter.addAll(DiscreteValues.FIELDS_TO_TITLES.get(fieldToSend));
                titleToSend = titleAdapter.getItem(0);
                title.setAdapter(titleAdapter);
                title.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        titleToSend = titleAdapter.getItem(position);
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


        return view;
    }

    private void attemptAddJob(){
        description.setError(null);
        city.setError(null);
        state.setError(null);
        rate.setError(null);

        JSONObject jobInfo = new JSONObject();
        MyUserSingleton myUserSingleton = MyUserSingleton.getInstance(getActivity());
        try{
            jobInfo.put("company_name", myUserSingleton.getAttributes().get("company"));
            jobInfo.put("employer_id", myUserSingleton.getAttributes().get("_id"));
            jobInfo.put("title", titleToSend);
            jobInfo.put("description", description.getText().toString().trim());
            jobInfo.put("field_experience", fieldExpToSend);
            jobInfo.put("field", fieldToSend);
            jobInfo.put("title_experience", titleExpToSend);
            jobInfo.put("city", city.getText().toString().trim());
            jobInfo.put("starting_rate", rate.getText().toString().trim());
            jobInfo.put("state", state.getText().toString().trim());

            boolean cancel = false;
            View focusView = null;

            if (TextUtils.isEmpty(jobInfo.getString("description"))){
                description.setError(getString(R.string.error_field_required));
                focusView = description;
                cancel = true;
            }
            if (TextUtils.isEmpty(jobInfo.getString("city"))){
                city.setError(getString(R.string.error_field_required));
                focusView = city;
                cancel = true;
            }
            if (TextUtils.isEmpty(jobInfo.getString("state"))){
                state.setError(getString(R.string.error_field_required));
                focusView = state;
                cancel = true;
            }
            if (TextUtils.isEmpty(jobInfo.getString("starting_rate"))){
                rate.setError(getString(R.string.error_field_required));
                focusView = rate;
                cancel = true;
            }

            if (cancel){focusView.requestFocus();}
            else{
                showProgress(true);
                JsonObjectRequest pushJob = new JsonObjectRequest(Request.Method.POST, RegisterActivity.HOSTURL + "jobs/create",
                        jobInfo, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response", response.toString());
                        Toast.makeText(getActivity(), "Job created!", Toast.LENGTH_LONG).show();
                        showProgress(false);
                        mListener.onAddJobFragmentInteraction();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

                MyJSONRequestSingleton.getInstance(getActivity()).addToRequestQueue(pushJob);
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onAddJobFragmentInteraction();
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


    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            addJobView.setVisibility(show ? View.GONE : View.VISIBLE);
            addJobView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    addJobView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            addJobView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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
        void onAddJobFragmentInteraction();
    }
}
