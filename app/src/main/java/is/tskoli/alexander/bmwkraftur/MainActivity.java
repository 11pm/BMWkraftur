package is.tskoli.alexander.bmwkraftur;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;

public class MainActivity extends AppCompatActivity {

    Scraper scrapy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }

    @Override
    protected void onResume(){
        super.onResume();

        new ThreadTask().execute();

    }


    private class ThreadTask extends AsyncTask<Void, Void, Void>{

        private String title;
        private ProgressDialog dialog;


        @Override
        protected Void doInBackground(Void... params) {

            try {
                Document doc = Jsoup.connect("http://bmwkraftur.is/spjall/viewforum.php?f=5").get();

                Elements thread = doc.select("#pagecontent table.tablebg tbody > tr");

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
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Loading...");
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();

        }

        @Override
        protected void onPostExecute(Void result){
            Log.wtf("wtf", this.title);
            TextView te = (TextView) findViewById(R.id.textView);
            te.setText(this.title);
            dialog.dismiss();
        }


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
