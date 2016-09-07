package com.dostavkagruzov.calculating;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Anton on 02.06.2015.
 */
public class Cities {
    public static List<List<Double>> pricesValue = new ArrayList<>();
    public static List<List<Double>> pricesWeight = new ArrayList<>();
    public static List<String> names = new ArrayList<>();
    public static Integer from = -1;
    public static Integer in = -1;
    public static List<Integer> withoutPrices = new LinkedList();
    public static boolean isInitializedMainActivity = false;
    public static String value = "";
    public static String weight = "";
}
