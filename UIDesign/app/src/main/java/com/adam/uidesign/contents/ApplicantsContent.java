package com.adam.uidesign.contents;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 4/4/2016.
 */
public class ApplicantsContent {

    public static final List<ApplicantItem> APPLICANTS = new ArrayList<>();

    public static void addItem(ApplicantItem item) {
        APPLICANTS.add(item);
    }

    public static void clearAll() {APPLICANTS.clear();}


    public static class ApplicantItem{
        public final String id;
        public final String name;
        public final String email;
        public final String bio;
        public final String city;
        public final String state;
        public final String profPic;
        public final String title;
        public final String field;
        public final String title_experience;
        public final String field_experience;

        public ApplicantItem(JSONObject applicant) throws JSONException {
            id = applicant.getString("_id");
            name = applicant.getString("name");
            email = applicant.getString("email");
            bio = applicant.getString("bio");
            title = applicant.getString("title");
            field = applicant.getString("field");
            title_experience = applicant.getString("title_experience");
            field_experience = applicant.getString("field_experience");
            city = applicant.getString("city");
            state = applicant.getString("state");
            profPic = applicant.getString("profPic");
        }
    }
}
