package is.tskoli.alexander.bmwkraftur;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ListView threadList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        threadList = (ListView) findViewById(R.id.threadList);

        new ThreadTask().execute();

    }

    private class ThreadTask extends AsyncTask<Void, Void, Void> {

        private String title;
        private ProgressDialog dialog;


        @Override
        protected Void doInBackground(Void... params) {

            try {
                //connect to the forum to scrape data
                Document doc = Jsoup.connect(Config.get_api_base() + "viewforum.php?f=5").get();

                //get each html thread element
                Elements threads = doc.select("#pagecontent table.tablebg tbody > tr");

                //go through each thread element and store the data from them
                for (Element entry : threads){

                    //get the actual link to the thread
                    String link = Config.get_api_base() + entry.select("a.topictitle").attr("href");
                    //get the thread topic
                    String topic = entry.select("a.topictitle").text();

                    // checks if topic is empty
                    if(!topic.isEmpty()) {
                        //add the thread to the list to display
                        Thread.add(new ThreadItem(link, topic));
                    }
                }

                //connect the threads to the actual list


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
//            TextView te = (TextView) findViewById(R.id.textView);
//            te.setText(this.title);

            //setListData();

            ArrayAdapter<ThreadItem> adapter = new ThreadAdapter();

            adapter.notifyDataSetChanged();

            threadList.setAdapter(adapter);


            dialog.dismiss();
        }


        protected void onPause(){
            dialog.dismiss();
        }


    }

    //Adapter to covert the thread arraylist to a viewlist
    private class ThreadAdapter extends ArrayAdapter<ThreadItem> {

        public ThreadAdapter() {
            super(MainActivity.this, R.layout.thread_list, Thread.get());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View threadView = convertView;

            //make sure we have a view
            if (threadView == null) {
                threadView = getLayoutInflater().inflate(R.layout.thread_list, parent, false);
            }

            ThreadItem curThread = Thread.find(position);

            TextView topic = (TextView) threadView.findViewById(R.id.threadTopic);
            topic.setText(curThread.topic);

            return threadView;

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
