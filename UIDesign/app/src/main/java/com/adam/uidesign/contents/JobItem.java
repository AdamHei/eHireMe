package com.adam.uidesign.contents;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Adam on 4/16/2016.
 */
public class JobItem {

    public final String id;
    public final String company_name;
    public final String employer_id;
    public final String title;
    public final String description;
    public final String starting_rate;
    public final String field;
    public final String title_experience;
    public final String field_experience;
    public final String city;
    public final String state;

    public JobItem(JSONObject job) throws JSONException {
        id = job.getString("_id");
        company_name = job.getString("company_name");
        employer_id = job.getString("employer_id");
        description = job.getString("description");
        title = job.getString("title");
        starting_rate = job.getString("starting_rate");
        field = job.getString("field");
        title_experience = job.getString("title_experience");
        field_experience = job.getString("field_experience");
        city = job.getString("city");
        state = job.getString("state");
    }

    @Override
    public String toString() {
        return title + ": " + description;
    }

}
