package is.tskoli.alexander.bmwkraftur;

import android.app.ProgressDialog;
import android.content.Intent;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;

public class ThreadActivity extends AppCompatActivity {

    ThreadItem currentThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);

        Intent intent = getIntent();


        final int position = intent.getIntExtra("position", 0);

        ThreadItem thread = Thread.find(position);
        currentThread = thread;


        setTitle(thread.topic);

        new ThreadTask().execute();

    }

    private class ThreadTask extends AsyncTask<Void, Void, Void> {

        private String title;
        private ProgressDialog dialog;


        @Override
        protected Void doInBackground(Void... params) {

            try {
                //connect to the forum to scrape data
                Document doc = Jsoup.connect(currentThread.link).get();

                //get each html thread element
                Elements posts = doc.select(".pagecontent > .tablebg");

                //go through each thread element and store the data from them
                for (Element post : posts){

                    Log.wtf("wtf", post.toString());

                    //get post author
                    String author  = post.select("tr:nth-child(2).row1 .postauthor").text();
                    //
                    String content = post.select("tr:nth-child(3) td:nth-child(2) .postbody:first-child").text();

                    Log.wtf("wtf", author);
                    Log.wtf("wtf", content);

                    if (author.length() > 0 && content.length() > 0){
                        Post.add(new PostItem(author, content));
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
            dialog = new ProgressDialog(ThreadActivity.this);
            dialog.setMessage("Loading...");
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();

        }

        @Override
        protected void onPostExecute(Void result){

            ArrayAdapter<PostItem> adapter = new PostAdapter();

            adapter.notifyDataSetChanged();

            ListView list = (ListView)findViewById(R.id.postList);

            list.setAdapter(adapter);

            dialog.dismiss();
        }


        protected void onPause(){
            dialog.dismiss();
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
