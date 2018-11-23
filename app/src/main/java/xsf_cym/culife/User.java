package xsf_cym.culife;
import java.util.*;

public class User{
    String userName;
    int credit;

    public User(String name){
        userName = name;
        credit = 30;
    }

    public void updateCredit(int change){
        credit += change;
    }

    public void storeInfo(){//store user newest info in local file

    }

}