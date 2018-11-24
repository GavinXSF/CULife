package xsf_cym.culife;

import java.io.Serializable;
import java.util.ArrayList;

public class BusStop implements Serializable {
    boolean[] passLines=new boolean[12];
    String stopName;
    int[][] standardTime = new int[12][];
    int[] sizeOfPeriod = new int[12];
    ArrayList<String> lineNum = new ArrayList<>();
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
                int sumOfTime = 0;
                for(int j = 0; j < buses[i].passStops.indexOf(stopName); j++){
                    sumOfTime += buses[i].interval.get(j);
                }
                sizeOfPeriod[i] = buses[i].timePeriod.size();
                standardTime[i] = new int[sizeOfPeriod[i]];
                for(int j = 0; j < sizeOfPeriod[i]; j++){
                    int result = 0;
                    result = buses[i].timePeriod.get(j) + sumOfTime;
                    if(result > 60)
                        result = result % 60;
                    standardTime[i][j] = result;
                    
                }
            
            }
        }
    }
    public ArrayList<String> waitingTime(int startTime){
        ArrayList<String> displayInfo = new ArrayList<String>();
        int index = 0;
        for(int i = 0; i < 12; i++){
            if(passLines[i]){
                int nearestTime = 60;
                for(int j = 0; j < sizeOfPeriod[i]; j++){
                    int wait = 0;
                    if((startTime % 100) > standardTime[i][j])
                        wait = standardTime[i][j] + 60 - (startTime % 100);
                    else
                        wait = standardTime[i][j] - (startTime % 100);
                    if(wait < nearestTime)
                        nearestTime = wait;
                }
                displayInfo.add("Line "+ lineNum.get(index) + ": " + nearestTime + " mins\n" );
                index++;

            }
        }
        return(displayInfo);
    }
}