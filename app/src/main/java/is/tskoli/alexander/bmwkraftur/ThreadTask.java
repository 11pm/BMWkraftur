package is.tskoli.alexander.bmwkraftur;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by alexander on 29.10.2015.
 */
public class ThreadTask extends AsyncTask<Void, Void, Void>{

    private String title;
    private ProgressDialog dialog;


    @Override
    protected Void doInBackground(Void... params) {

        try {
            Document doc = Jsoup.connect("http://bmwkraftur.is/spjall/viewforum.php?f=5").get();
            title = doc.title();
        } catch (IOException e) {
            e.printStackTrace();
            Log.wtf("wtf", "site not found");
        }

        return null;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();

        dialog.setMessage("Loading...");
        dialog.setIndeterminate(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();

    }

    @Override
    protected void onPostExecute(Void result){
        Log.wtf("wtf", this.title);
        dialog.dismiss();
    }


}
