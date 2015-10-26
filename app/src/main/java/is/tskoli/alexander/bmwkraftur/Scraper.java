package is.tskoli.alexander.bmwkraftur;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by alexander on 26.10.2015.
 */
public class Scraper {

    private String website_url;

    public Scraper (){
        this.website_url = "http://bmwkraftur.is/spjall/viewforum.php?f=5";
    }

    public void getThreads(){

        Document doc = (Document) Jsoup.connect(this.website_url);

        Elements a = doc.select("#pagecontent .tablebg tr");
        
    }


}
