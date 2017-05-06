package org.whitings.gpxanalysis;

import android.location.Location;

/**
 * Created by whitingpt on 4/18/17.
 */

public class Grade {
    double distance;    // distance from start of ride to where this grade begins
    double grade;       // percent grade
    double length;      // length of segment
    double gain;        // gain (or loss) in meters between start and stop
    double elevation;
    public Grade () {
        distance = 0;
        length = 0;
        gain = 0;
        grade = Double.MIN_VALUE;
        elevation=Double.MIN_VALUE;
    }
    public Grade(double d, Location start, Location stop) {
        elevation=start.getAltitude();
        length = start.distanceTo(stop);
        distance=d;
        gain = stop.getAltitude() - start.getAltitude();
        if(length>0)grade = gain / length;
        else grade = Double.MIN_VALUE;
    }
}
