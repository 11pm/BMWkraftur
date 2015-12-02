package is.tskoli.alexander.bmwkraftur;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class MainActivity extends AppCompatActivity {

    ListView threadList;
    int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DatabaseHandler db = DatabaseHandler.getInstance(this);

        threadList = (ListView) findViewById(R.id.threadList);

        threadList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {

                //create the intent we are moving to
                Intent intent = new Intent(MainActivity.this, ThreadActivity.class);

                //send the position of the clicked alarm item to the next activity
                intent.putExtra("position", position);

                List<ThreadManager> listss = db.getAllItems();

                boolean taken = false;

                for (ThreadManager al : listss) {
                    if (al.getLink().equals(Thread.find(position).threadNumber)) {
                        taken = true;
                        break;
                    }
                    else {
                            Log.wtf("wtf", " helvíti");
                    }
                }
                if (!taken){
                    db.addToList(Thread.find(position).threadNumber);
                }

                //open the new activity
                startActivity(intent);

            }

        });

        new ThreadTask().execute();

        threadList.setOnScrollListener(new EndlessScrollListener());

    }

    // finds the thread number in the link
    public static String getThreadNum(String params)
    {

        try {
            String t = null;
            Pattern p = Pattern.compile("(?<=t=).*?(?=&|$)");
            Matcher m = p.matcher(params);
            while (m.find()) {
                t = m.group();
            }

            return t;
        } catch (PatternSyntaxException ex) {
            // error handling
            return ex.toString();
        }
    }

    public class EndlessScrollListener implements AbsListView.OnScrollListener {

        private int visibleThreshold = 5;

        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessScrollListener() {
        }
        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    page++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                // load the next page and add to list
                Log.wtf("next page", String.valueOf(page));
                new ThreadTask().execute();
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }

    private class ThreadTask extends AsyncTask<Void, Void, Void> {

        private String title;
        private ProgressDialog dialog;


        @Override
        protected Void doInBackground(Void... params) {

            try {

                //start from thread 2 when page is 1
                int start = 2;



                if(page == 2) {
                    start = 35;
                }
                else if(page > 2){
                    start = (page-1) * 35;
                }

                //connect to the forum to scrape data
                Document doc = Jsoup.connect(Config.get_api_base() + "viewforum.php?f=5&start=" + start).get();

                //get each html thread element
                Elements threads = doc.select("#pagecontent table.tablebg tbody > tr");

                //go through each thread element and store the data from them
                for (Element entry : threads){

                    //get the actual link to the thread
                    String link = entry.select("a.topictitle").attr("href");

                    String threadNumber = getThreadNum(link);

                    //get the thread topic
                    String topic = entry.select("a.topictitle").text();

                    // checks if topic is empty
                    if(topic.length() > 0) {
                        //add the thread to the list to display
                        link = Config.get_api_base() + link.substring(2);
                        //Log.wtf("wtf", link.substring(2));
                        Thread.add(new ThreadItem(link, threadNumber, topic));
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
            dialog.setMessage("Sæki þræði...");
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();

        }

        @Override
        protected void onPostExecute(Void result){

            ArrayAdapter<ThreadItem> adapter = new ThreadAdapter();


            int index = threadList.getFirstVisiblePosition();
            View v = threadList.getChildAt(0);
            int top = (v == null) ? 0 : (v.getTop() - threadList.getPaddingTop());

            adapter.notifyDataSetChanged();

            threadList.setAdapter(adapter);

            // restore index and position
            threadList.setSelectionFromTop(index, top);


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

            DatabaseHandler db = DatabaseHandler.getInstance(getContext());

            //make sure we have a view
            if (threadView == null) {
                threadView = getLayoutInflater().inflate(R.layout.thread_list, parent, false);
            }

            //changes background color of listview items
            if (position % 2 == 1) {
                threadView.setBackgroundColor(Color.rgb(215,218,219));
            } else {
                threadView.setBackgroundColor(Color.WHITE);
            }

            View read = (View) threadView.findViewById(R.id.threadRead);

            if (!db.getItem(1).getLink().isEmpty()) {

                List<ThreadManager> listss = db.getAllItems();

                for (ThreadManager al : listss) {
                    if (al.getLink().equals(Thread.find(position).threadNumber)) {
                        Log.wtf(Thread.find(position).threadNumber, al.getLink());
                        read.setBackgroundColor(Color.rgb(53, 133, 81));
                        break;
                    }
                    else {
                        read.setBackgroundColor(Color.TRANSPARENT);
                    }
                }
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
