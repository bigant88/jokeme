package app.jokeme;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thaodv on 5/4/16.
 */
public class MyDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "drawabe";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = MyDatabase.class.getName();

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // you can use an alternate constructor to specify a database location
        // (such as a folder on the sd card)
        // you must ensure that this folder is available and you have permission
        // to write to it
        //super(context, DATABASE_NAME, context.getExternalFilesDir(null).getAbsolutePath(), null, DATABASE_VERSION);

    }

    public List<JokeModel> getJokes() {
        List<JokeModel> jokeModels = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] sqlSelect = {"id", "content", "category"};
        String sqlTables = "atmall";
        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, null, null,
                null, null, null);
        Log.i(TAG, "size " + c.getCount());
        try {
            while (c.moveToNext()) {
                jokeModels.add(getDataFromCursor(c));
            }
        } finally {
            c.close();
        }
        return jokeModels;

    }

    private JokeModel getDataFromCursor(Cursor c) {
        long id = c.getLong(0);
        String content = c.getString(1);
        String category  = c.getString(2);
        JokeModel jokeModel = new JokeModel(id, content, category);
        return jokeModel;
    }

}
