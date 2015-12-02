package is.tskoli.alexander.bmwkraftur;

/**
 * Created by halldor32 on 1.12.2015.
 */
public class ThreadManager {
    int _id;
    String _link;

    public ThreadManager() {

    }

    public ThreadManager(int id, String link) {
        this._id = id;
        this._link = link;
    }

    public ThreadManager(String link) {
        this._link = link;
    }

    public int GetID() {
        return this._id;
    }

    public void setID(int id){
        this._id = id;
    }

    public String getLink(){
        return this._link;
    }

    public void setLink(String link){
        this._link = link;
    }
}
