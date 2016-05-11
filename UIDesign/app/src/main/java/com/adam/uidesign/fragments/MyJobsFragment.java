package com.adam.uidesign.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adam.uidesign.contents.JobItem;
import com.adam.uidesign.singletons.MyJSONRequestSingleton;
import com.adam.uidesign.adapters.MyMyJobsRecyclerViewAdapter;
import com.adam.uidesign.singletons.MyUserSingleton;
import com.adam.uidesign.R;
import com.adam.uidesign.activities.RegisterActivity;
import com.adam.uidesign.contents.AllJobsContent;
import com.adam.uidesign.contents.EmployersJobsContent;
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
public class MyJobsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private MyMyJobsRecyclerViewAdapter myMyJobsRecyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MyJobsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MyJobsFragment newInstance(int columnCount) {
        MyJobsFragment fragment = new MyJobsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
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
        View view = inflater.inflate(R.layout.fragment_myjobs_list, container, false);

        // Set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_jobs_list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        myMyJobsRecyclerViewAdapter = new MyMyJobsRecyclerViewAdapter(EmployersJobsContent.MY_JOBS, mListener);
        recyclerView.setAdapter(myMyJobsRecyclerViewAdapter);
        EmployersJobsContent.clearAll();
        getMyJobs();

        return view;
    }

    private void getMyJobs(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, RegisterActivity.HOSTURL +
                "employers/getJobs/" + MyUserSingleton.getInstance(getActivity()).getAttributes().get("_id"), new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jobs = response.getJSONArray("jobs");
                            for (int i = 0; i < jobs.length(); i += 1){
                                EmployersJobsContent.addItem(new JobItem(jobs.getJSONObject(i)));
                            }
                            myMyJobsRecyclerViewAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getActivity(), "We couldn't fetch your jobs :(", Toast.LENGTH_LONG).show();
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
        void onMyJobsFragmentInteraction(JobItem item);
    }
}
