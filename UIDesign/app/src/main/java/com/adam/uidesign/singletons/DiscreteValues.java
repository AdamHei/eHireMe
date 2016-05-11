package com.adam.uidesign.singletons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Adam on 4/25/2016.
 */
public class DiscreteValues {

    public final static HashMap<String, ArrayList<String>> FIELDS_TO_TITLES;
    public final static ArrayList<String> EXPERIENCE_LEVELS;

    static {
        EXPERIENCE_LEVELS = new ArrayList<>();
        EXPERIENCE_LEVELS.addAll(Arrays.asList("No experience","1-2 years","3-5 years","5-10 years","10-20 years","20+ years"));

        FIELDS_TO_TITLES = new HashMap<>();
        ArrayList<String> tempList;

        tempList = new ArrayList<>();
        tempList.addAll(Arrays.asList("Mechanic", "Repairman"));
        FIELDS_TO_TITLES.put("Industrial", tempList);

        tempList = new ArrayList<>();
        tempList.addAll(Arrays.asList("Cook", "Waiter", "Busser"));
        FIELDS_TO_TITLES.put("Food Service", tempList);

        tempList = new ArrayList<>();
        tempList.addAll(Arrays.asList("Nurse", "Receptionist", "Janitor"));
        FIELDS_TO_TITLES.put("Medical", tempList);

        tempList = new ArrayList<>();
        tempList.addAll(Arrays.asList("Software Developer", "Computer Engineer"));
        FIELDS_TO_TITLES.put("Programming", tempList);

        tempList = new ArrayList<>();
        tempList.addAll(Arrays.asList("Cashier", "Salesperson", "Manager"));
        FIELDS_TO_TITLES.put("Retail", tempList);
    }
}
