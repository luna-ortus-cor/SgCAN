package unused;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB_user extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "User.db";
    public static final String TABLE_NAME = "user_table";
    public static final String COL_0 = "ID";
    public static final String COL_1 = "user_fn";
    public static final String COL_2 = "user_ln";
    public static final String COL_3 = "user_mobile";
    public static final String COL_4 = "user_address";
    public static final String COL_5 = "user_condition";
    public static final String COL_6 = "user_remarks";
    public static final String COL_7 = "user_status";
    public static final String COL_8 = "user_guardianID";

    public DB_user(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, user_fn TEXT, user_ln TEXT," +
                "user_mobile TEXT, user_address TEXT, user_condition TEXT, user_remarks TEXT, user_status INTEGER, user_guardianID INTEGER)");

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, "h1010000");
        contentValues.put(COL_2, "Jonas");
        contentValues.put(COL_3, "Tang");
        contentValues.put(COL_4, "M19400");
        contentValues.put(COL_5, "91238456");
        contentValues.put(COL_6, "h1010000");
        contentValues.put(COL_7, -1);
        contentValues.put(COL_8, 1);
        //update if events is modified
        long result = db.insert(TABLE_NAME,null ,contentValues);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);

    }
}
