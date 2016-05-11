package com.adam.uidesign.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
 * {@link OnProfileFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String NAME = "name";
    private static final String ARG_PARAM2 = "param2";

    private String name;
    private String id;

    private TextView blanksname;
    private TextView email;
    private TextView dob;
    private TextView city;
    private TextView state;
    private TextView bio;
    private TextView age;
    private ImageView profPic;

    private OnProfileFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String name) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        SharedPreferences preferences = getActivity().getSharedPreferences(getString(R.string.PREFERENCES_NAME), Context.MODE_PRIVATE);
        id = preferences.getString("_id", "");

        blanksname = (TextView) view.findViewById(R.id.blanksprofile);
        bio = (TextView) view.findViewById(R.id.textBio);
        email = (TextView) view.findViewById(R.id.textEmail);
        city = (TextView) view.findViewById(R.id.textCity);
        dob = (TextView) view.findViewById(R.id.textDobDescription);
        age = (TextView) view.findViewById(R.id.textAge);
        state = (TextView) view.findViewById(R.id.textState);
        profPic = (ImageView) view.findViewById(R.id.profPic);

        Button button = (Button) view.findViewById(R.id.logout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getActivity().getSharedPreferences(getString(R.string.PREFERENCES_NAME), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                MyUserSingleton.getInstance(getActivity()).delete();
                mListener.onFragmentInteraction();
            }
        });

        if (MyUserSingleton.getInstance(getContext()).type != MyUserSingleton.UserType.ADMIN){
            MyJSONRequestSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(getUserInfo());
        }

        return view;
    }

    private JsonObjectRequest getUserInfo(){
        String url = RegisterActivity.HOSTURL;
        final MyUserSingleton myUserSingleton = MyUserSingleton.getInstance(getActivity().getApplicationContext());
        url += myUserSingleton.type == MyUserSingleton.UserType.APPLICANT ? "applicants/" : "employers/";
        url += myUserSingleton.getAttributes().get("_id");

        return new JsonObjectRequest(Request.Method.GET,
                url, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    switch (myUserSingleton.type){
                        case APPLICANT:
                            blanksname.setText(response.getString("name") + "'s Profile");
                            bio.setText(response.getString("bio"));
                            state.setText(response.getString("state"));
                            city.setText(response.getString("city"));
                            email.setText(response.getString("email"));
                            dob.setText(response.getString("dob"));
                            age.setText(response.getString("age"));
                            break;
                        case EMPLOYER:
                            blanksname.setText(response.getString("company") + "'s Profile");
                            dob.setText(response.getString("description"));
                            email.setText(response.getString("email"));
                            bio.setVisibility(View.GONE);
                            state.setVisibility(View.GONE);
                            city.setVisibility(View.GONE);
                            age.setVisibility(View.GONE);
                            break;
                    }
                    getUserPic(myUserSingleton);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getActivity(), "We couldn't get your info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserPic(MyUserSingleton mySingleTon){
        String url = RegisterActivity.HOSTURL;
        url += mySingleTon.type == MyUserSingleton.UserType.APPLICANT ? "applicants/getPhoto/" : "employers/getPhoto/";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                imgurHelper(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getActivity(), "There was an error getting your imgur URL", Toast.LENGTH_SHORT).show();
            }
        });
        MyJSONRequestSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void imgurHelper(String url){
        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>(){
            @Override
            public void onResponse(Bitmap response) {
                profPic.setImageBitmap(response);
            }
        }, 0, 0, null, null, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getActivity(), "We were unable to grab your profile photo", Toast.LENGTH_SHORT).show();
            }
        });
        MyJSONRequestSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(imageRequest);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnProfileFragmentInteractionListener) {
            mListener = (OnProfileFragmentInteractionListener) context;
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
    public interface OnProfileFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }
}
