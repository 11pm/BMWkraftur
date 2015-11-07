package is.tskoli.alexander.bmwkraftur;

import android.util.Log;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by alexander on 26.10.2015.
 */
public class Scraper {

    private String website_url;

    public Scraper() throws MalformedURLException {
        this.website_url = "http://bmwkraftur.is/spjall/viewforum.php?f=5";
    }

    public void getThreads() throws IOException {

        Document doc = Jsoup.connect(this.website_url).get();

        Log.wtf("wtf", doc.title());

    }
}