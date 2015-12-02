package is.tskoli.alexander.bmwkraftur;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by halldor32 on 1.12.2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static DatabaseHandler mInstance = null;

    private static final String DATABASE_NAME = "ThreadManager";

    private static final String TABLE_READ = "read";

    private static final String KEY_ID = "id";
    private static final String KEY_LINK = "link";

    public static DatabaseHandler getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new DatabaseHandler(ctx.getApplicationContext());
        }
        return mInstance;
    }

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //    create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ALARMS_TABLE = "CREATE TABLE " + TABLE_READ + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_LINK + " TEXT" + ")";
        db.execSQL(CREATE_ALARMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_READ);

        onCreate(db);
    }

    //    add to list
    public void addToList(String item) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LINK, item);


        db.insert(TABLE_READ, null, values);
        db.close();
    }

    //  get item
    ThreadManager getItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_READ, new String[] { KEY_ID,
                        KEY_LINK }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        ThreadManager read = new ThreadManager(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1));

        return read;
    }

    //  get all items
    public List<ThreadManager> getAllItems() {
        List<ThreadManager> threadManager = new ArrayList<ThreadManager>();

        String selectQuery = "SELECT  * FROM " + TABLE_READ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // goes through every row and adds to the list
        if (cursor.moveToFirst()) {
            do {
                ThreadManager thread = new ThreadManager();
                thread.setID(Integer.parseInt(cursor.getString(0)));
                thread.setLink(cursor.getString(1));

                // adds to list
                threadManager.add(thread);
            } while (cursor.moveToNext());
        }

        return threadManager;
    }

    //  get items count
    public int getItemsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_READ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    //  update item
    public int updateItem(ThreadManager Item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LINK, Item.getLink());

        // updating row
        return db.update(TABLE_READ, values, KEY_ID + " = ?",
                new String[] { String.valueOf(Item.GetID()) });
    }

    //  delete item
    public void deleteItem(ThreadManager Item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_READ, KEY_ID + " = ?",
                new String[]{String.valueOf(Item.GetID())});
        db.close();
    }
}
