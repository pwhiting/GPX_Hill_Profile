package org.whitings.gpxanalysis;

import android.content.SharedPreferences;
import android.location.Location;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.Vector;

/**
 * Created by whitingpt on 4/18/17.
 */

public class GPXData {
    Vector<Location> points;
    Vector<Grade> grades;
    Vector<Segment> segments;
    Grade max_grade;
    double total_distance = 0;
    double total_loss = 0;
    double total_gain = 0;
    double lower = .02; // lower limit for steep segments

    public static String toMiles(double mtrs) {
        return String.format("%.2f", mtrs * 0.000621371);
    }

    public static String toFeet(double mtrs) {
        return String.format("%.0f", mtrs * 3.28084);
    }

    public GPXData(InputStream stream) {
        points = new Vector<Location>();
        grades = new Vector<Grade>();
        segments = new Vector<Segment>();
        if (readGPXData(stream)) {
            calculateGrades();
            calculateStats();
        }
    }

    private Boolean readGPXData(InputStream stream) {
        try {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser gpx = xmlFactoryObject.newPullParser();
            gpx.setInput(stream, null);
            for (int event = gpx.getEventType();
                 event != XmlPullParser.END_DOCUMENT;
                 event = gpx.next())
                if (event == XmlPullParser.START_TAG && gpx.getName() != null)
                    if (gpx.getName().equals("trkpt")) {
                        Location current = new Location("");
                        current.setLatitude(Double.valueOf(gpx.getAttributeValue(null, "lat")));
                        current.setLongitude(Double.valueOf(gpx.getAttributeValue(null, "lon")));
                        points.add(current);
                    } else if ("ele".equals(gpx.getName())) {
                        event = gpx.next();
                        points.lastElement().setAltitude(Double.valueOf(gpx.getText()));
                    }
        } catch (Exception e) {
            System.out.println("Failed to parse gpx file");
            e.printStackTrace();
            return false;
        }
        return true; // success
    }

    private void calculateGrades() {
        total_distance = 0;
        for (int i = 0; i < points.size() - 1; i++) {
            grades.add(new Grade(total_distance, points.get(i), points.get(i + 1)));
            total_distance += grades.lastElement().length;
        }
    }

    public void setParams(SharedPreferences sharedPrefs) {
        if (segments != null) segments.firstElement().setParams(sharedPrefs);
        double new_lower = Float.parseFloat(sharedPrefs.getString("start_grade", "2.0")) / 100;
        if(lower!=new_lower) {
            lower=new_lower;
            calculateStats();
        }
    }

    private void calculateStats() {
        if(segments.size()>0) segments.removeAllElements();
        max_grade = grades.firstElement();
        Segment s = null;
        for (Grade g : grades) {
            if (g.grade > max_grade.grade) max_grade = g;
            if (g.grade > 0) total_gain += g.gain;
            if (g.grade < 0) total_loss -= g.gain;
            if (g.grade >= lower) {
             /*   if (s == null) {
                    if (segments.size() > 0) {
                        Grade l = segments.lastElement().last();
                        if (l.distance + l.length > g.distance - 50) {
                            // if within 25 meters, join the segments
                            s = segments.lastElement();
                        }
                    }
                }*/
                if (s == null) segments.add(s = new Segment());
                s.add(g);
            } else if (s != null) {
                s.add(g); // terminate the segment
                s = null;
            }
        }
    }

    public String toString() {
        String rval = "Total distance: " + toMiles(total_distance) + " miles\n" +
                "Total gain: " + toFeet(total_gain) + " feet\n" +
                "Total loss: " + toFeet(total_loss) + " feet\n" +
                "Max grade: " + String.format("%.1f", max_grade.grade * 100) +
                " at mile " + toMiles(max_grade.distance) + "\n\n";
        for (Segment seg : segments) rval += seg.toString();
        return rval;
    }
}