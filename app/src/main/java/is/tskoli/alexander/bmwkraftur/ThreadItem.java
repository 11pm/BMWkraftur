package is.tskoli.alexander.bmwkraftur;

/**
 * Created by alexander on 7.11.2015.
 */
public class ThreadItem {

    protected String link;
    protected String threadNumber;
    protected String topic;

    public ThreadItem(String _link, String _threadNumber, String _topic){
        this.link  = _link;
        this.threadNumber = _threadNumber;
        this.topic = _topic;
    }

}
