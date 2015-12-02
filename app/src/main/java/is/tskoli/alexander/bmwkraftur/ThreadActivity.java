package is.tskoli.alexander.bmwkraftur;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;


public class ThreadActivity extends AppCompatActivity {

    ThreadItem currentThread;
    ListView list;
    int page = 1;

    Boolean stop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);

        Intent intent = getIntent();


        final int position = intent.getIntExtra("position", 0);

        ThreadItem thread = Thread.find(position);
        currentThread = thread;

        list = (ListView) findViewById(R.id.postList);



        setTitle(thread.topic);

        new ThreadTask().execute();
        list.setOnScrollListener(new EndlessScrollListener());



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

                int start = 0;
                int postCount = 0;

                //connect to the forum to scrape data
                Document doc = Jsoup.connect(currentThread.link + "&start=" + start).get();

                ListView l = (ListView) findViewById(R.id.threadList);

                Elements pageC = doc.select("#pagecontent table:first-child .gensmall");
                String postC = pageC.get(0).text();

                String[] parts = postC.split(" ");

                //Log.wtf("wtf", parts[1]);

                postCount = Integer.parseInt(parts[1]);

                //int pages = (int) Math.ceil((double) postCount / 15);

                //if we are on last page
                /*if(page == pages){
                    //list.setOnScrollListener();

                    stop = true;

                }*/
                if(page == 1){
                    Post.clear();
                }
                else if(page == 2) {
                    start = 15;
                }
                else if(page > 2){
                    start = (page-1) * 15;
                }


                //Post.clear();


                //get each html thread element
                Elements posts = doc.select("#pagecontent table.tablebg");

                //go through each thread element and store the data from them
                for (Element post : posts){

                    String author = post.select(".postauthor").text();
                    String content = post.select(".postbody:first-child").text();

                    if (author.length() > 0 && content.length() > 0){
                        Post.add(new PostItem(author, content));
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.wtf("wtf", "site not found");
            }

            return null;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            dialog = new ProgressDialog(ThreadActivity.this);
            dialog.setMessage("Loading...");
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();

        }

        @Override
        protected void onPostExecute(Void result){

            ListView list = (ListView)findViewById(R.id.postList);
            ArrayAdapter<PostItem> adapter = new PostAdapter();

            int index = list.getFirstVisiblePosition();
            View v = list.getChildAt(0);
            int top = (v == null) ? 0 : (v.getTop() - list.getPaddingTop());

            adapter.notifyDataSetChanged();

            list.setAdapter(adapter);

            list.setSelectionFromTop(index, top);


            dialog.dismiss();
        }


        protected void onPause(){
            dialog.dismiss();
        }

        //clear post when backed

        protected void OnBackPressed(){
            Post.clear();
        }

    }

    private class PostAdapter extends ArrayAdapter<PostItem> {

        public PostAdapter() {
            super(ThreadActivity.this, R.layout.post_list, Post.get());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View postView = convertView;

            //make sure we have a view
            if (postView == null) {
                postView = getLayoutInflater().inflate(R.layout.post_list, parent, false);
            }

            PostItem curPost = Post.find(position);

            TextView author = (TextView) postView.findViewById(R.id.threadPostAuthor);
            TextView content = (TextView) postView.findViewById(R.id.threadPostContent);

            author.setText(curPost.author);
            content.setText(curPost.post);

            return postView;

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_thread, menu);
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
