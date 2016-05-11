package com.adam.uidesign.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adam.uidesign.R;
import com.adam.uidesign.contents.ApplicantsContent;
import com.adam.uidesign.contents.JobItem;
import com.adam.uidesign.fragments.Applicant_Profile;
import com.adam.uidesign.fragments.ApplicantsFragment;
import com.adam.uidesign.fragments.JobDescription;
import com.adam.uidesign.fragments.JobListFragment;
import com.adam.uidesign.fragments.MatchesFragment;
import com.adam.uidesign.fragments.MyJobsFragment;
import com.adam.uidesign.fragments.NewJobFragment;
import com.adam.uidesign.fragments.ProfileFragment;
import com.adam.uidesign.fragments.TinderViewFragment;
import com.adam.uidesign.singletons.MyJSONRequestSingleton;
import com.adam.uidesign.singletons.MyUserSingleton;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        JobListFragment.OnListFragmentInteractionListener,
        TinderViewFragment.OnFragmentInteractionListener,
        ProfileFragment.OnProfileFragmentInteractionListener,
        JobDescription.OnFragmentInteractionListener,
        MatchesFragment.OnListFragmentInteractionListener,
        NewJobFragment.OnFragmentInteractionListener,
        MyJobsFragment.OnListFragmentInteractionListener,
        ApplicantsFragment.OnListFragmentInteractionListener,
        Applicant_Profile.OnFragmentInteractionListener{

    FrameLayout fragcon;
    TextView navName;
    TextView navEmail;
    TextView navType;
    ImageView navPic;
    MenuItem matches;
    MenuItem myJobs;
    FloatingActionButton addJob;
    MyUserSingleton.UserType type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragcon = (FrameLayout) findViewById(R.id.frag_container);
        addJob = (FloatingActionButton) findViewById(R.id.fab);
        type = MyUserSingleton.getInstance(this).type;
        if (type == MyUserSingleton.UserType.EMPLOYER){
            addJob.setVisibility(View.VISIBLE);
            addJob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewJobFragment newJobFragment = NewJobFragment.newInstance("", "");
                    android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frag_container, newJobFragment).addToBackStack(null).commit();
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        matches = navigationView.getMenu().getItem(1);
        myJobs = navigationView.getMenu().getItem(2);
        matches.setVisible(type == MyUserSingleton.UserType.APPLICANT);

        navigationView.setNavigationItemSelectedListener(this);
        navName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navName);
        navEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navEmail);
        navPic = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.navPic);
        navType = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navType);

        Bundle bundle = getIntent().getExtras();
        Toast.makeText(MainActivity.this, "Welcome, " + bundle.get("Name"), Toast.LENGTH_SHORT).show();
        if (type != MyUserSingleton.UserType.ADMIN){
            navName.setText((String) bundle.get("Name"));
        }
        navEmail.setText((String) bundle.get("Email"));
        navType.setText((String) bundle.get("type"));

        if (type != MyUserSingleton.UserType.ADMIN){
            downloadProfilePhoto();
        }

        if (findViewById(R.id.frag_container) != null)
        {
            if (savedInstanceState == null)
            {
                if (type == MyUserSingleton.UserType.APPLICANT){
                    TinderViewFragment tinderViewFragment = new TinderViewFragment();
                    getSupportFragmentManager().beginTransaction().add(R.id.frag_container, tinderViewFragment, "").commit();
                }
                else if (type == MyUserSingleton.UserType.EMPLOYER){
                    MyJobsFragment myJobsFragment = MyJobsFragment.newInstance(1);
                    getSupportFragmentManager().beginTransaction().add(R.id.frag_container, myJobsFragment, "").commit();
                }
                else{
                    JobListFragment jobListFragment = new JobListFragment();
                    getSupportFragmentManager().beginTransaction().add(R.id.frag_container, jobListFragment, "").commit();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void downloadProfilePhoto(){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(getString(R.string.PREFERENCES_NAME), Context.MODE_PRIVATE);
        final String id = preferences.getString("_id", "");
        if (id.length() == 0) {
            Toast.makeText(MainActivity.this, "We don't have your id on file", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = RegisterActivity.HOSTURL;
        url += (type == MyUserSingleton.UserType.APPLICANT) ? "applicants/getPhoto/" : "employers/getPhoto/";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                imgurHelper(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(MainActivity.this, "There was an error getting your imgur URL", Toast.LENGTH_SHORT).show();
            }
        });
        MyJSONRequestSingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void imgurHelper(final String url){
        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>(){
            @Override
            public void onResponse(Bitmap response) {
                navPic.setImageBitmap(response);
            }
        }, 0, 0, null, null, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(MainActivity.this, "We were unable to grab your profile photo", Toast.LENGTH_SHORT).show();
            }
        });
        MyJSONRequestSingleton.getInstance(this).addToRequestQueue(imageRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()){
            case R.id.nav_home:
                Fragment fragment = type == MyUserSingleton.UserType.APPLICANT ? new TinderViewFragment() : MyJobsFragment.newInstance(1);
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment).commit();
                break;
            case R.id.nav_profile:
                ProfileFragment profileFragment = new ProfileFragment();
                android.support.v4.app.FragmentTransaction profileTransaction = getSupportFragmentManager().beginTransaction();
                profileTransaction.replace(R.id.frag_container, profileFragment).commit();
                break;
            case R.id.nav_matches:
                MatchesFragment matchesFragment = MatchesFragment.newInstance(1);
                android.support.v4.app.FragmentTransaction matchTransaction = getSupportFragmentManager().beginTransaction();
                matchTransaction.replace(R.id.frag_container, matchesFragment).commit();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Open a new job description from the job list
     * @param item
     */
    @Override
    public void onListFragmentInteraction(JobItem item) {
        JobDescription job = JobDescription.newInstance(item);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frag_container, job).addToBackStack(null).commit();
    }

    /**
     * Open a new job description from the matches list
     * @param item
     */
    @Override
    public void onMatchFragmentInteraction(JobItem item) {
        onListFragmentInteraction(item);
    }

    /**
     * Swap from ListView to TinderView
     * @param fragment
     */
    @Override
    public void onToTinderPressed(Fragment fragment) {
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frag_container, fragment).commit();
    }

    /**
     * Swap from TinderView to ListView
     * @param fragment
     */
    @Override
    public void onToListInteraction(Fragment fragment, boolean addToBackStack) {
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (addToBackStack){
            fragmentTransaction.replace(R.id.frag_container, fragment).addToBackStack(null).commit();
        }
        else{
            fragmentTransaction.replace(R.id.frag_container, fragment).commit();
        }
    }

    @Override
    public void onCardPressedInteraction(JobItem jobItem) {
        JobDescription job = JobDescription.newInstance(jobItem);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frag_container, job).addToBackStack(null).commit();
    }

    //Profile Fragment > For logout
    @Override
    public void onFragmentInteraction() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onJobFragmentInteraction(Uri uri) {

    }

    @Override
    public void onAddJobFragmentInteraction() {
        MyJobsFragment myJobsFragment = MyJobsFragment.newInstance(1);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frag_container, myJobsFragment).commit();
    }

    /**
     * My jobs fragment interface
     * @param item
     */
    @Override
    public void onMyJobsFragmentInteraction(JobItem item) {
        ApplicantsFragment applicantsFragment = ApplicantsFragment.newInstance(item.id);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frag_container, applicantsFragment).addToBackStack(null).commit();
    }

    /**
     * List of applicants
     * @param applicant
     */
    @Override
    public void onListFragmentInteraction(ApplicantsContent.ApplicantItem applicant, String job_id) {
        Applicant_Profile applicant_profile = Applicant_Profile.newInstance(applicant.id);
        applicant_profile.job_id = job_id;
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frag_container, applicant_profile).addToBackStack(null).commit();
    }

    /**
     * Applicant Profile interaction
     * @param uri
     */
    @Override
    public void onApplicantProfileFragmentInteraction(Uri uri) {

    }
}
