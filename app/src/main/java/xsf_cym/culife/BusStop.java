package xsf_cym.culife;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class BusStop implements Serializable {

    boolean[] passLines=new boolean[12];
    String stopName;
    double[][] standardTime = new double[12][];
    int[] sizeOfPeriod = new int[12];
    ArrayList<String> lineNum = new ArrayList<>();
    Double Longitude;
    Double Latitude;


    public BusStop(String name, Bus[] buses){
        stopName = name;
        for(int i=0;i<12;i++){
            passLines[i]=buses[i].passStops.contains(stopName);
        }
        
    }
    public void calculateTime(Bus[] buses){
        for(int i = 0; i < 12; i++){  
            if(passLines[i]){
                lineNum.add(buses[i].busNum);
                double sumOfTime = 0;
                for(int j = 0; j < buses[i].passStops.indexOf(stopName); j++){
                    sumOfTime += buses[i].interval.get(j);
                }
                sizeOfPeriod[i] = buses[i].timePeriod.size();
                standardTime[i] = new double[sizeOfPeriod[i]];
                for(int j = 0; j < sizeOfPeriod[i]; j++){
                    double result = 0;
                    result = buses[i].timePeriod.get(j) + sumOfTime;
                    if(result > 60)
                        result = result % 60;
                    standardTime[i][j] = result;
                    
                }
            
            }
        }
    }
    public void setLocation(Double Lat, Double Lng){
        Latitude = Lat;
        Longitude = Lng;
    }
    public HashMap<String , Double> waitingTime(int startTime){
//        ArrayList<String> displayInfo = new ArrayList<String>();
        HashMap<String , Double> hashmap = new HashMap<String, Double>();
        int index = 0;
        for(int i = 0; i < 12; i++){
            if(passLines[i]){
                double nearestTime = 60.0;
                for(int j = 0; j < sizeOfPeriod[i]; j++){
                    double wait;
                    if((startTime % 100) > standardTime[i][j])
                        wait = standardTime[i][j] + 60 - (startTime % 100);
                    else
                        wait = standardTime[i][j] - (startTime % 100);
                    if(wait < nearestTime)
                        nearestTime = wait;
                }
                hashmap.put(lineNum.get(index),nearestTime);
//                displayInfo.add("Line "+ lineNum.get(index) + ": " + nearestTime + " mins\n" );
                index++;

            }
        }
        return(hashmap);
    }
    public HashMap<String , Double> waitingTime(int startTime, boolean[] filters){
//        ArrayList<String> displayInfo = new ArrayList<String>();
        HashMap<String , Double> hashmap = new HashMap<String, Double>();
        int index = 0;
        for(int i = 0; i < 12; i++){
            if(passLines[i]){
                double nearestTime = 60.0;
                for(int j = 0; j < sizeOfPeriod[i]; j++){
                    double wait = 0;
                    if((startTime % 100) > standardTime[i][j])
                        wait = standardTime[i][j] + 60 - (startTime % 100);
                    else
                        wait = standardTime[i][j] - (startTime % 100);
                    if(wait < nearestTime)
                        nearestTime = wait;
                }
                if(filters[i])
                    hashmap.put(lineNum.get(index),nearestTime);
//                    displayInfo.add("Line "+ lineNum.get(index) + ": " + nearestTime + " mins\nEstimated travel time: " );

                index++;

            }
        }
        return(hashmap);
    }
}