package com.adam.uidesign.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.adam.uidesign.contents.AllJobsContent;
import com.adam.uidesign.contents.JobItem;
import com.adam.uidesign.singletons.MyJSONRequestSingleton;
import com.adam.uidesign.adapters.MyJobRecyclerViewAdapter;
import com.adam.uidesign.R;
import com.adam.uidesign.activities.RegisterActivity;
import com.adam.uidesign.singletons.MyUserSingleton;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class JobListFragment extends Fragment implements View.OnClickListener{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private MyJobRecyclerViewAdapter myJobRecyclerViewAdapter;

    private ArrayList<String> companys;
    private ArrayList<String> fields;
    private ArrayList<String> titles;
    private Spinner companySpinner;
    private Spinner titleSpinner;
    private Spinner fieldSpinner;
    private ArrayAdapter<String> companyAdapter;
    private ArrayAdapter<String> titleAdapter;
    private ArrayAdapter<String> fieldAdapter;
    private String companyToSearch;
    private String fieldToSearch;
    private String titleToSearch;

    private AlertDialog.Builder alert;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public JobListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static JobListFragment newInstance(int columnCount) {
        JobListFragment fragment = new JobListFragment();
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

        alert = null;
        (companys = new ArrayList<>()).add("");
        (fields = new ArrayList<>()).add("");
        (titles = new ArrayList<>()).add("");
        companyAdapter = new ArrayAdapter<>(getContext(), R.layout.single_attribute, R.id.single_attr);
        companyAdapter.setDropDownViewResource(R.layout.single_attribute);
        fieldAdapter = new ArrayAdapter<>(getContext(), R.layout.single_attribute, R.id.single_attr);
        fieldAdapter.setDropDownViewResource(R.layout.single_attribute);
        titleAdapter = new ArrayAdapter<>(getContext(), R.layout.single_attribute, R.id.single_attr);
        titleAdapter.setDropDownViewResource(R.layout.single_attribute);
        companyToSearch = "";
        titleToSearch = "";
        fieldToSearch = "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_job_list, container, false);

        // Set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        myJobRecyclerViewAdapter = new MyJobRecyclerViewAdapter(AllJobsContent.ITEMS, mListener);
        recyclerView.setAdapter(myJobRecyclerViewAdapter);
        getAllJobs();
        Button button = (Button) view.findViewById(R.id.toTinder);
        if (MyUserSingleton.getInstance(getContext()).type == MyUserSingleton.UserType.ADMIN){
            button.setVisibility(View.GONE);
        }
        else{
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TinderViewFragment tinderViewFragment = new TinderViewFragment();
                    mListener.onToTinderPressed(tinderViewFragment);
                }
            });
        }
        Button refresh = (Button) view.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllJobsContent.clearAll();
                getAllJobs();
            }
        });
        Button searchBtn = (Button) view.findViewById(R.id.searchbtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alert != null){
                    populateAlertDialog();
                    alert.show();
                }
            }
        });

        if (companyAdapter.getCount() == 0){
            populateArrayLists();
        }

        return view;
    }

//    private void reSearch(){
//        alert = new AlertDialog.Builder(getContext());
//        alert.setTitle("Search");
//        alert.setMessage("Choose any or all terms");
//        // Set an EditText view to get user input
//        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
//        View view = layoutInflater.inflate(R.layout.searchview, null);
//        alert.setView(view);
//    }

    private void populateAlertDialog(){
        alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Search");
        alert.setMessage("Choose any or all terms");
        // Set an EditText view to get user input
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.searchview, null);
        alert.setView(view);
        companySpinner = (Spinner) view.findViewById(R.id.searchCompany);
        companySpinner.setAdapter(companyAdapter);
        companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 companyToSearch = companyAdapter.getItem(position);
             }
             @Override
             public void onNothingSelected(AdapterView<?> parent) {

             }
         });
        titleSpinner = (Spinner) view.findViewById(R.id.searchTitle);
        titleSpinner.setAdapter(titleAdapter);
        titleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
               @Override
               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                   titleToSearch = titleAdapter.getItem(position);
               }
               @Override
               public void onNothingSelected(AdapterView<?> parent) {

               }
           });
        fieldSpinner = (Spinner) view.findViewById(R.id.searchField);
        fieldSpinner.setAdapter(fieldAdapter);
        fieldSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fieldToSearch = fieldAdapter.getItem(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        alert.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                search();
            }
        });
//        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                // Canceled.
//            }
//        });
    }

    private void search(){
        JSONObject searchInfo = new JSONObject();
        try {
            searchInfo.put("company_name", companyToSearch);
            searchInfo.put("title", titleToSearch);
            searchInfo.put("field", fieldToSearch);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, RegisterActivity.HOSTURL + "jobs/search",
                    searchInfo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        AllJobsContent.clearAll();
                        JSONArray jobs = response.getJSONArray("jobs");
                        for (int i = 0; i < jobs.length(); i += 1){
                            AllJobsContent.addItem(new JobItem(jobs.getJSONObject(i)));
                        }
                        myJobRecyclerViewAdapter.notifyDataSetChanged();
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
            MyJSONRequestSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void populateArrayLists(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, RegisterActivity.HOSTURL + "jobs/getThreeAttributes", new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray companyNamesArr = response.getJSONArray("company_names");
                            JSONArray fieldsArr = response.getJSONArray("fields");
                            JSONArray titlesArr = response.getJSONArray("titles");
                            for (int i = 0; i < companyNamesArr.length(); i += 1){
                                companys.add(companyNamesArr.getString(i));
                            }
                            for (int i = 0; i < fieldsArr.length(); i += 1){
                                fields.add(fieldsArr.getString(i));
                            }
                            for (int i = 0; i < titlesArr.length(); i += 1){
                                titles.add(titlesArr.getString(i));
                            }
                            companyAdapter.addAll(companys);
                            companyAdapter.notifyDataSetChanged();
                            fieldAdapter.addAll(fields);
                            fieldAdapter.notifyDataSetChanged();
                            titleAdapter.addAll(titles);
                            titleAdapter.notifyDataSetChanged();
                            populateAlertDialog();
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
        MyJSONRequestSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
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

    @Override
    public void onClick(View v) {

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
        void onListFragmentInteraction(JobItem item);
        void onToTinderPressed(android.support.v4.app.Fragment fragment);
    }

    private void getAllJobs(){
        AllJobsContent.clearAll();
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", MyUserSingleton.getInstance(getActivity()).getAttributes().get("_id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = RegisterActivity.HOSTURL + "jobs/";
        int method;
        if (MyUserSingleton.getInstance(getActivity()).type == MyUserSingleton.UserType.ADMIN){
            method = Request.Method.GET;
            url += "all";
        }
        else{
            method = Request.Method.POST;
            url += "match/";
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("response", response.getJSONArray("jobs").toString());
                            JSONArray jobs = response.getJSONArray("jobs");
                            for (int i = 0; i < jobs.length(); i += 1){
                                AllJobsContent.addItem(new JobItem(jobs.getJSONObject(i)));
                            }
                            myJobRecyclerViewAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(getActivity(), "We couldn't fetch the jobs :(", Toast.LENGTH_SHORT).show();
                }
        });
        MyJSONRequestSingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }
}
