package com.adam.uidesign.contents;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 4/4/2016.
 */
public class EmployersJobsContent {

    public static final List<JobItem> MY_JOBS = new ArrayList<>();

    public static void addItem(JobItem item) {MY_JOBS.add(item);}

    public static void clearAll() {MY_JOBS.clear();}
}
