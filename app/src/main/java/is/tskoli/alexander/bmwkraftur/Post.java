package is.tskoli.alexander.bmwkraftur;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexander on 21.11.2015.
 */
public class Post {

        private static List<PostItem> posts = new ArrayList<PostItem>();

        public static void add(PostItem post){
            posts.add(post);
        }

        public static List<PostItem> get(){
            return posts;
        }

        public static PostItem find(int idx){
            return posts.get(idx);
        }


}
