package com.adam.uidesign.contents;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 4/3/2016.
 */
public class MatchesContent {

    public static final List<JobItem> MATCHES = new ArrayList<>();

    public static void addItem(JobItem item) {
        MATCHES.add(item);
    }

    public static void clearAll() {MATCHES.clear();}

}
