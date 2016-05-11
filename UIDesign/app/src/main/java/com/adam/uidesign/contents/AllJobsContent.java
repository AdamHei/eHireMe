package com.adam.uidesign.contents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 */
public class AllJobsContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<JobItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, JobItem> ITEM_MAP = new HashMap<>();

    public static void addItem(JobItem item) {
        ITEMS.add(item);
    }

    public static void clearAll() {ITEMS.clear();}

//    private static String makeDetails(int position) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("Details about Item: ").append(position);
//        for (int i = 0; i < position; i++) {
//            builder.append("\nMore details information here.");
//        }
//        return builder.toString();
//    }

    /**
     * A dummy item representing a piece of content.
     */
//    public static class JobItem {
//        public final String id;
//        public final String company_name;
//        public final String employer_id;
//        public final String title;
//        public final String description;
//        public final String starting_rate;
//        public final String field;
//        public final String title_experience;
//        public final String field_experience;
//        public final String city;
//        public final String state;
//
//        public JobItem(JSONObject job) throws JSONException{
//            id = job.getString("_id");
//            company_name = job.getString("company_name");
//            employer_id = job.getString("employer_id");
//            description = job.getString("description");
//            title = job.getString("title");
//            starting_rate = job.getString("starting_rate");
//            field = job.getString("field");
//            title_experience = job.getString("title_experience");
//            field_experience = job.getString("field_experience");
//            city = job.getString("city");
//            state = job.getString("state");
//        }
//
//        @Override
//        public String toString() {
//            return title + ": " + description;
//        }
//    }
}
