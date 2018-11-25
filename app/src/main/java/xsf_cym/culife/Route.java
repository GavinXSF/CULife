package xsf_cym.culife;

import android.util.Log;

public class Route{
    int startTime;
    String startPosition;
    String destination;
    boolean[] validBus = new boolean[12];

    public Route(int inputTime, String departure, String dest){
        startTime = inputTime;
        startPosition = departure;
        destination = dest;
    }

    public void computeLine(BusStop[] stops, Bus[] buses){//figure out which buses can be taken and store them in validBus
        boolean[] startStopLines,endStopLines,results;
        startStopLines = new boolean[12];
        endStopLines = new boolean[12];
        results = new boolean[12];
        for(BusStop elements:stops){   //search for the starting stop
            if(elements.stopName.equals(startPosition)){
                startStopLines = elements.passLines;
              //  Log.d("Tsai", " " + startStopLines[2]);
                break;
            }
        }
        
        for(BusStop elements:stops){   //search for the destination
            if(elements.stopName.equals(destination)){
                endStopLines = elements.passLines;

                break;
            }
        }
        for(int i = 0; i < 12; i++){
            results[i] = startStopLines[i] & endStopLines[i];
          //  Log.d("Tsai", "computeLine: "+ i + " " + results[i]);
        }
        for(int i=0;i<12;i++){
            if (results[i]){
                if(buses[i].passStops.indexOf(startPosition) > buses[i].passStops.indexOf(destination))//check if the destination is after the start position in the route of that bus
                    results[i] = false;    //if not, set results[i] to false
            }
        }
        validBus = results;
    }




}