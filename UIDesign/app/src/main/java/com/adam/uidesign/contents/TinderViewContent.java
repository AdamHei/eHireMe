package com.adam.uidesign.contents;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 4/16/2016.
 */
public class TinderViewContent {

    public static final List<JobItem> JOB_ITEMS = new ArrayList<>();

    public static void addItem(JobItem item) {
        JOB_ITEMS.add(item);
    }

    public static void clearAll() {
        JOB_ITEMS.clear();
    }
}
