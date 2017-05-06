package org.whitings.gpxanalysis;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Vector;
import static org.whitings.gpxanalysis.GPXData.toMiles;
import static org.whitings.gpxanalysis.GPXData.toFeet;


/**
 * Created by WhitingPT on 4/19/2017.
 */

public class Segment {
    Vector<Grade> segment;
    static private double min_gain=5;
    static private double min_grade=.025;
    static private double min_length=25;

//    public String toMiles(double meters) {
//        return String.format("%.2f",meters * 0.000621371);
//    }
    private double feet2meters(double feet) { return feet/3.28084;}
    public Segment() {
        segment=new Vector<Grade>();
    }
//    public int size() { return segment.size();}
    public void add(Grade g) {
        if((segment.size()==0) || (int)(segment.lastElement().grade*100) != (int)(g.grade*100))
            segment.add(g);
    }
//    public double length() {
//        if(segment.size()==0) return 0;
//        return segment.lastElement().distance-segment.firstElement().distance;
//    }
   // public Grade last() { return segment.lastElement();}


    public void setParams(SharedPreferences sharedPrefs) {
        min_gain=feet2meters(Float.parseFloat(sharedPrefs.getString("min_gain","15")));
        min_grade=Float.parseFloat(sharedPrefs.getString("min_grade","2.5"))/100;
        min_length=feet2meters(Float.parseFloat(sharedPrefs.getString("min_length","50")));
    }
    public String toString() {
        if(segment.size()==0) return "";
        Grade start = segment.firstElement();
        Grade stop = segment.lastElement();
        double gain=stop.elevation-start.elevation;
        if(gain<min_gain) return "";
        double length=stop.distance-start.distance;
        if(length<min_length) return "";
        double grade=gain/length;
        if(grade<min_grade) return "";
        String rval= toFeet(gain) + " foot gain over " +
                toMiles(length) + " miles (" +
                String.format("%.1f",grade*100) + "%) at mile " +
                toMiles(start.distance) + ".\n";
        for(int i=0;i<segment.size()-1;i++)
            rval += "grade " + (int) (segment.elementAt(i).grade*100) + " for " +
                    toMiles(segment.elementAt(i + 1).distance - segment.elementAt(i).distance) +
                    " miles\n";
        return rval+"\n";
    }
}
