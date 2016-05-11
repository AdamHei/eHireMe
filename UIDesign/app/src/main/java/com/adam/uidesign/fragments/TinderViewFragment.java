package com.adam.uidesign.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adam.uidesign.R;
import com.adam.uidesign.activities.RegisterActivity;
import com.adam.uidesign.contents.JobItem;
import com.adam.uidesign.contents.TinderViewContent;
import com.adam.uidesign.singletons.MyJSONRequestSingleton;
import com.adam.uidesign.singletons.MyUserSingleton;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.andtinder.model.CardModel;
import com.andtinder.model.Orientations;
import com.andtinder.view.CardContainer;
import com.andtinder.view.SimpleCardStackAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Stack;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TinderViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TinderViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TinderViewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private CardContainer mCardContainer;
    private SimpleCardStackAdapter adapter;

    private ProgressBar progressBar;

    public TinderViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TinderViewFragment.
     */
    public static TinderViewFragment newInstance(String param1, String param2) {
        TinderViewFragment fragment = new TinderViewFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tinder_view, container, false);

        mCardContainer = (CardContainer) view.findViewById(R.id.tinderLayout);
        mCardContainer.setOrientation(Orientations.Orientation.Disordered);
        progressBar = (ProgressBar) view.findViewById(R.id.tinder_progress);
        adapter = new SimpleCardStackAdapter(getContext());
        mCardContainer.setAdapter(adapter);

        showProgress(true);
        TinderViewContent.clearAll();
        fetchPotentialMatches();

        Button button = (Button) view.findViewById(R.id.toList);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobListFragment jobFragment = new JobListFragment();
                mListener.onToListInteraction(jobFragment, false);
            }
        });
        return view;
    }

    private void fetchPotentialMatches(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", MyUserSingleton.getInstance(getActivity()).getAttributes().get("_id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, RegisterActivity.HOSTURL + "jobs/match/",
                obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            filterOutMatches(response.getJSONArray("jobs"));
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

    private void filterOutMatches(JSONArray allJobs){
        final JSONArray newAllJobs = allJobs;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, RegisterActivity.HOSTURL +
                "jobs/getMatches/" + MyUserSingleton.getInstance(getActivity()).getAttributes().get("_id"), new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ArrayList<String> ids = new ArrayList<>();
                            JSONArray matchedJobs = response.getJSONArray("jobs");
                            for (int i = 0; i < matchedJobs.length(); i += 1){
                                ids.add(matchedJobs.getJSONObject(i).getString("_id"));
                            }
                            for (int j = 0; j < newAllJobs.length(); j += 1){
                                if (ids.contains(newAllJobs.getJSONObject(j).getString("_id"))){
                                    newAllJobs.remove(j);
                                    j--;
                                  //^^Cheeky bugger
                                }
                            }
                            for (int k = 0; k < newAllJobs.length(); k += 1){
                                TinderViewContent.addItem(new JobItem(newAllJobs.getJSONObject(k)));
                            }
                            populateCards();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getActivity(), "We couldn't fetch the matches :(", Toast.LENGTH_LONG).show();
            }
        });
        MyJSONRequestSingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }

    public void populateCards(){
        final Stack<CardModel> jobs = new Stack<>();
        for (int i = 0; i < TinderViewContent.JOB_ITEMS.size(); i += 1){
            final JobItem job = TinderViewContent.JOB_ITEMS.get(i);
            final int position = TinderViewContent.JOB_ITEMS.size() - 1 - i;
            int id = 0;
            switch (job.title){
                case "Mechanic":
                    id = R.drawable.mechanic;
                    break;
                case "Repairman":
                    id = R.drawable.repairman;
                    break;
                case "Cook":
                    id = R.drawable.cook;
                    break;
                case "Waiter":
                    id = R.drawable.waiter;
                    break;
                case "Busser":
                    id = R.drawable.busser;
                    break;
                case "Nurse":
                    id = R.drawable.nurse;
                    break;
                case "Receptionist":
                    id = R.drawable.receptionist;
                    break;
                case "Janitor":
                    id = R.drawable.janitor;
                    break;
                case "Software Developer":
                    id = R.drawable.developer;
                    break;
                case "Computer Engineer":
                    id = R.drawable.engineer;
                    break;
                case "Cashier":
                    id = R.drawable.cashier;
                    break;
                case "Salesperson":
                    id = R.drawable.salesperson;
                    break;
                case "Manager":
                    id = R.drawable.manager;
                    break;
            }

//            CardModel card = new CardModel(job.title, job.description, decodeSampledBitmapFromResource(getResources(), id, 350, 350));

            BitmapWorkerTask task = new BitmapWorkerTask(jobs, job,
                    i == TinderViewContent.JOB_ITEMS.size() - 1, position);
            task.execute(id);

//            card.setOnCardDimissedListener(new CardModel.OnCardDimissedListener() {
//                @Override
//                public void onLike() {
//                }
//                @Override
//                public void onDislike() {
//                    applyToJob(position);
//                }
//            });
//            jobs.push(card);
        }
//        while (jobs.size() > 0){
//            adapter.add(jobs.pop());
//        }
//        adapter.notifyDataSetChanged();
//        mCardContainer.setAdapter(adapter);
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        Stack<CardModel> jobs;
        private JobItem job;
        private int data = 0;
        private boolean isLast;
        private int position;

        public BitmapWorkerTask(Stack<CardModel> jobs, JobItem job, boolean isLast, int position){
            this.jobs = jobs;
            this.job = job;
            this.isLast = isLast;
            this.position = position;
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            return decodeSampledBitmapFromResource(getResources(), data, 250, 250);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap){
            CardModel card = new CardModel(job.title, job.description, bitmap);
            card.setOnCardDimissedListener(new CardModel.OnCardDimissedListener() {
                @Override
                public void onLike() {
                }
                @Override
                public void onDislike() {
                    applyToJob(position);
                }
            });
            jobs.push(card);
            if (isLast){
                reverseAndPopulate(jobs);
            }
        }
    }

    private void reverseAndPopulate(Stack<CardModel> jobs){
        while (jobs.size() > 0){
            adapter.add(jobs.pop());
        }
        adapter.notifyDataSetChanged();
        mCardContainer.setAdapter(adapter);
        showProgress(false);
    }

    private static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private void applyToJob(int position){
        String jobid = TinderViewContent.JOB_ITEMS.get(position).id;
        JSONObject ids = new JSONObject();
        JsonObjectRequest jsonObjectRequest = null;
        try {
            ids.put("user_id", MyUserSingleton.getInstance(getActivity()).getAttributes().get("_id"));
            ids.put("job_id", jobid);
            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, RegisterActivity.HOSTURL + "applicants/makeMatch",
                    ids, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("response", response.toString());
                    Toast.makeText(getActivity(), "Applied!", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(getActivity(), "We couldn't connect you :(", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyJSONRequestSingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }




    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mCardContainer.setVisibility(show ? View.GONE : View.VISIBLE);
            mCardContainer.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCardContainer.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mCardContainer.setVisibility(show ? View.GONE : View.VISIBLE);
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
        void onToListInteraction(android.support.v4.app.Fragment fragment, boolean addToBackStack);
        void onCardPressedInteraction(JobItem jobItem);
    }
}
