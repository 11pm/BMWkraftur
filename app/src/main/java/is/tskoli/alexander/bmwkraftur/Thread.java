package is.tskoli.alexander.bmwkraftur;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexander on 8.11.2015.
 */


public class Thread {

    private static List<ThreadItem> threads = new ArrayList<ThreadItem>();

    public static void add(ThreadItem thread){
        threads.add(thread);
    }

    public static List<ThreadItem> get(){
        return threads;
    }

    public static ThreadItem find(int idx){
        return threads.get(idx);
    }
}
