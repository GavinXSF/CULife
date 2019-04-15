package xsf_cym.culife;
import java.io.Serializable;
import java.util.*;

public class Bus implements Serializable {
    String busNum;
    ArrayList<String> passStops = new ArrayList<String>();
    ArrayList<Double> interval = new ArrayList<Double>();//use data analysis to modify this
    boolean[] validDate = new boolean[7];
    int startTime, endTime;
    ArrayList<Integer> timePeriod = new ArrayList<Integer>();

    public Bus(String line){
        busNum = line;
        boolean[] regularDate = {true,true,true,true,true,true,false};
        switch (line){//initialize bus info
            case "1A":
                passStops.add("University MTR Station");
                interval.add(2.0);
                passStops.add("University Sports Centre");
                interval.add(3.0);
                passStops.add("Sir Run Run Shaw Hall");
                interval.add(2.0);
                passStops.add("University Administration Building");
                interval.add(2.0);
                passStops.add("S.H. Ho College");
                interval.add(2.0);
                passStops.add("University MTR Station(arrival)");
                timePeriod.add(20);
                timePeriod.add(40);
                validDate = regularDate;
                startTime = 740;
                endTime = 1840;
                break;
            case "1B":
                passStops.add("University MTR Station");
                interval.add(3.0);
                passStops.add("Jockey Club Postgraduate Hall");
                interval.add(3.0);
                passStops.add("University Sports Centre");
                interval.add(2.0);
                passStops.add("Sir Run Run Shaw Hall");
                interval.add(2.0);
                passStops.add("University Administration Building");
                interval.add(2.0);
                passStops.add("S.H. Ho College");
                interval.add(3.0);
                passStops.add("Jockey Club Postgraduate Hall(to MTR Station)");
                interval.add(3.0);
                passStops.add("University MTR Station(arrival)");
                timePeriod.add(00);
                validDate = regularDate;
                startTime = 800;
                endTime = 1800;
                break;
            case "2":
                passStops.add("University MTR Station");
                interval.add(2.0);
                passStops.add("University Sports Centre");
                interval.add(3.0);
                passStops.add("Sir Run Run Shaw Hall");
                interval.add(1.0);
                passStops.add("Fung King-hey Building");
                interval.add(2.0);
                passStops.add("United College");
                interval.add(1.0);
                passStops.add("New Asia College");
                interval.add(1.0);
                passStops.add("University Administration Building");
                interval.add(2.0);
                passStops.add("S.H. Ho College");
                interval.add(2.0);
                passStops.add("University MTR Station(arrival)");
                timePeriod.add(00);
                timePeriod.add(15);
                timePeriod.add(30);
                timePeriod.add(45);
                validDate = regularDate;
                startTime = 745;
                endTime = 1845;
                break;
            case "3":
                passStops.add("Yasumoto International Academic Park");
                interval.add(1.32);
                passStops.add("University Sports Centre");
                interval.add(1.65);
                passStops.add("Science Centre");
                interval.add(1.25);
                passStops.add("Fung King-hey Building");
                interval.add(0.58);
                passStops.add("Residences 3 & 4(to Shaw)");
                interval.add(1.30);
                passStops.add("Shaw College(to C.W. Chu College)");
                interval.add(1.61);
                passStops.add("C.W. Chu College(uphill)");
                interval.add(1.05);
                passStops.add("Residence 15");
                interval.add(0.3);
                passStops.add("United College Staff Residence");
                interval.add(1.57);
                passStops.add("Chan Chun Ha Hostel");
                interval.add(1.55);
                passStops.add("Shaw College(to Main Campus)");
                interval.add(1.0);
                passStops.add("Residences 3 & 4(to Main Campus)");
                interval.add(1.25);
                passStops.add("University Administration Building");
                interval.add(1.25);
                passStops.add("S.H. Ho College");
                interval.add(1.77);
                passStops.add("University MTR Station Piazza");
                timePeriod.add(00);
                timePeriod.add(20);
                timePeriod.add(40);
                validDate = regularDate;
                startTime = 900;
                endTime = 1840;
                break;
            case "4":
                passStops.add("Yasumoto International Academic Park");
                interval.add(3.0);
                passStops.add("Campus Circuit East");
                interval.add(1.0);
                passStops.add("C.W. Chu College");
                interval.add(2.0);
                passStops.add("Area 39");
                interval.add(2.0);
                passStops.add("C.W. Chu College(uphill)");
                interval.add(3.0);
                passStops.add("Residence 15");
                interval.add(2.0);
                passStops.add("United College Staff Residence");
                interval.add(1.0);
                passStops.add("Chan Chun Ha Hostel");
                interval.add(2.0);
                passStops.add("Shaw College(to Main Campus)");
                interval.add(2.0);
                passStops.add("Residences 3 & 4(to Main Campus)");
                interval.add(3.0);
                passStops.add("New Asia College");
                interval.add(1.0);
                passStops.add("United College");
                interval.add(3.0);
                passStops.add("University Administration Building");
                interval.add(2.0);
                passStops.add("S.H. Ho College");
                interval.add(2.0);
                passStops.add("University MTR Station");
                timePeriod.add(10);
                timePeriod.add(30);
                timePeriod.add(50);
                validDate = regularDate;
                startTime = 730;
                endTime = 1850;
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
    public boolean checkInService(int departureTime, int currentDate){ // check if the bus is in service at that time, return true or false accordingly
        if(departureTime<startTime || departureTime>endTime)
            return false;
        else if(!validDate[currentDate])   // check whether bus is available that day
            return false;

        return true;
        
    }

    public double estimateTime(String start, String end){
        int startIndex = passStops.indexOf(start);
        int endIndex = passStops.indexOf(end);
        double sum = 0;
        for(int i = startIndex; i < endIndex; i++){
            sum += interval.get(i);
        }
        return sum;
    }
}