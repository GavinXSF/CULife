package xsf_cym.culife;
import java.util.*;

public class Bus{
    String busNum;
    ArrayList<String> passStops = new ArrayList<String>();
    ArrayList<Integer> interval = new ArrayList<Integer>();//use data analysis to modify this
    boolean[] validDate = new boolean[7];
    int startTime, endTime;
    ArrayList<Integer> timePeriod = new ArrayList<Integer>();

    public Bus(String line){
        busNum = line;
        boolean[] regularDate = {true,true,true,true,true,true,false};
        switch (line){//initialize bus info
            case "1A":
                passStops.add("University MTR Station");
                interval.add(2);
                passStops.add("University Sports Centre");
                interval.add(3);
                passStops.add("Sir Run Run Shaw Hall");
                interval.add(2);
                passStops.add("University Administration Building");
                interval.add(2);
                passStops.add("S.H. Ho College");
                interval.add(2);
                passStops.add("University MTR Station");
                timePeriod.add(20);
                timePeriod.add(40);
                validDate = regularDate;
                startTime = 740;
                endTime = 1840;
                break;
            case "1B":
                passStops.add("University MTR Station");
                interval.add(3);
                passStops.add("Jockey Club Postgraduate Hall");
                interval.add(3);
                passStops.add("University Sports Centre");
                interval.add(2);
                passStops.add("Sir Run Run Shaw Hall");
                interval.add(2);
                passStops.add("University Administration Building");
                interval.add(2);
                passStops.add("S.H. Ho College");
                interval.add(3);
                passStops.add("Jockey Club Postgraduate Hall");
                interval.add(3);
                passStops.add("University MTR Station");
                timePeriod.add(00);
                validDate = regularDate;
                startTime = 800;
                endTime = 1800;
                break;
            case "2":
                break;
            case "3":
                passStops.add("Yasumoto International Academic Park");
                interval.add(2);
                passStops.add("University Sports Centre");
                interval.add(3);
                passStops.add("Science Centre");
                interval.add(1);
                passStops.add("Fung King-hey Building");
                interval.add(2);
                passStops.add("Residences 3 & 4");
                interval.add(2);
                passStops.add("Shaw College");
                interval.add(3);
                passStops.add("C.W. Chu College");
                interval.add(2);
                passStops.add("Residence 15");
                interval.add(2);
                passStops.add("United College Staff Residence");
                interval.add(2);
                passStops.add("Chan Chun Ha Hostel");
                interval.add(2);
                passStops.add("Shaw College");
                interval.add(2);
                passStops.add("Residences 3 & 4");
                interval.add(3);
                passStops.add("University Administration Building");
                interval.add(2);
                passStops.add("S.H. Ho College");
                interval.add(2);
                passStops.add("University MTR Station Piazza");
                timePeriod.add(00);
                timePeriod.add(20);
                timePeriod.add(40);
                validDate = regularDate;
                startTime = 900;
                endTime = 1840;
                break;
            case "4":
                break;
            case "5":
                break;
            case "6A":
                break;
            case "6B":
                break;
            case "7":
                break;
            case "8":
                break;
            case "N":
                break;
            case "H":
                break;
            }
    }
    
    /* may add some checking on date*/
    public boolean checkInService(int departureTime){ // check if the bus is in service at that time, return true or false accordingly
        if(departureTime<startTime || departureTime>endTime)
            return false;
        return true;
        
    }

    public int estimateTime(String start, String end){
        int startIndex = passStops.indexOf(start);
        int endIndex = passStops.indexOf(end);
        int sum = 0;
        for(int i = startIndex; i < endIndex; i++){
            sum += interval.get(i);
        }
        return sum;
    }
}