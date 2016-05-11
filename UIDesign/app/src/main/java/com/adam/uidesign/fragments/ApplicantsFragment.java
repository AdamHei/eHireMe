package com.adam.uidesign.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adam.uidesign.activities.RegisterActivity;
import com.adam.uidesign.adapters.MyApplicantsRecyclerViewAdapter;
import com.adam.uidesign.R;
import com.adam.uidesign.contents.ApplicantsContent;
import com.adam.uidesign.singletons.MyJSONRequestSingleton;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ApplicantsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    public String job_id;

    private OnListFragmentInteractionListener mListener;
    MyApplicantsRecyclerViewAdapter myApplicantsRecyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ApplicantsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ApplicantsFragment newInstance(String job_id) {
        ApplicantsFragment fragment = new ApplicantsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, 1);
        fragment.setArguments(args);
        fragment.job_id = job_id;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_applicant_list, container, false);

        // Set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.applicants_list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        myApplicantsRecyclerViewAdapter = new MyApplicantsRecyclerViewAdapter(ApplicantsContent.APPLICANTS, mListener);
        myApplicantsRecyclerViewAdapter.job_id = job_id;
        recyclerView.setAdapter(myApplicantsRecyclerViewAdapter);

        //Added to constantly clear applicants
        ApplicantsContent.clearAll();
        if (ApplicantsContent.APPLICANTS.size() == 0){
            getApplicants();
        }
        return view;
    }

    private void getApplicants(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, RegisterActivity.HOSTURL +
                "jobs/getApplicants/" + job_id, new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray applicants = response.getJSONArray("applicants");
                            Log.e("applicants", applicants.toString());
                            for (int i = 0; i < applicants.length(); i += 1){
                                ApplicantsContent.addItem(new ApplicantsContent.ApplicantItem(applicants.getJSONObject(i)));
                            }
                            myApplicantsRecyclerViewAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getActivity(), "We couldn't fetch the applicants :(", Toast.LENGTH_LONG).show();
            }
        });
        MyJSONRequestSingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(ApplicantsContent.ApplicantItem applicant, String job_id);
    }
}
