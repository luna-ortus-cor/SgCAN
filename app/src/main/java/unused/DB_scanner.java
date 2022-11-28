package unused;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB_scanner extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Scanner.db";
    public static final String TABLE_NAME = "scanner_table";
    public static final String COL_0 = "ID";
    public static final String COL_1 = "scanner_username";
    public static final String COL_2 = "scanner_fn";
    public static final String COL_3 = "scanner_ln";
    public static final String COL_4 = "scanner_mobile";
    public static final String COL_5 = "scanner_pw";


    public DB_scanner(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, scanner_username TEXT,student_fn TEXT," +
                "student_ln TEXT, scanner_mobile TEXT, scanner_pw TEXT)");

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, "h1010000");
        contentValues.put(COL_2, "Jonas");
        contentValues.put(COL_3, "Tang");
        contentValues.put(COL_4, "M19400");
        contentValues.put(COL_5, "91238456");

        //update if events is modified
        long result = db.insert(TABLE_NAME,null ,contentValues);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);

    }
}
