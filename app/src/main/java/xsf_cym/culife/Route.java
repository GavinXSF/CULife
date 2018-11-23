package xsf_cym.culife;

public class Route{
    String startTime;
    String startPosition;
    String destination;
    boolean[] validBus = new boolean[12]; //考虑改成bool[]

    public Route(String inputTime, String departure, String dest){
        startTime = inputTime;
        startPosition = departure;
        destination = dest;
    }

    public Route(String inputTime, String departure, String dest, boolean[] lineNum){
        startTime = inputTime;
        startPosition = departure;
        destination = dest;
        validBus = lineNum;
    }
    public void computeLine(BusStop[] stops, Bus[] buses){//figure out which buses can be taken and store them in validBus
        boolean[] startStopLines,endStopLines,results;
        startStopLines = new boolean[12];
        endStopLines = new boolean[12];
        results = new boolean[12];
        for(BusStop elements:stops){   //search for the starting stop
            if(elements.stopName==startPosition)
                startStopLines = elements.passLines;
        }
        
        for(BusStop elements:stops){   //search for the destination
            if(elements.stopName==destination)
                endStopLines = elements.passLines;
        }
        for(int i = 0; i < 12; i++){
            results[i] = startStopLines[i] & endStopLines[i];
        }
        for(int i=0;i<12;i++){
            if (results[i]){
                if(buses[i].passStops.indexOf(startPosition) > buses[i].passStops.indexOf(destination))//check if the destination is after the start position in the route of that bus
                    results[i] = false;    //if not, set results[i] to false
            }
        }
        validBus = results;
    }

    public void storeRoute(){//store the route info to a local file

    }


}