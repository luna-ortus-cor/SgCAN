package unused;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB_guardian extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Guardian.db";
    public static final String TABLE_NAME = "guardian_table";
    public static final String COL_0 = "ID";
    public static final String COL_1 = "guardian_username";
    public static final String COL_2 = "guardian_fn";
    public static final String COL_3 = "guardian_ln";
    public static final String COL_4 = "guardian_mobile";
    public static final String COL_5 = "guardian_address";
    public static final String COL_6 = "guardian_pw";
    public static final String COL_7 = "guardian_users"; //list of people registered under guardian


    public DB_guardian(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, guardian_username TEXT, guardian_fn TEXT,"+
                "guardian_ln TEXT, guardian_mobile TEXT, guardian_address TEXT, guardian_pw TEXT, guardian_users TEXT)" );

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, "testaccount");
        contentValues.put(COL_2, "Test");
        contentValues.put(COL_3, "Account");
        contentValues.put(COL_4, "91234567");
        contentValues.put(COL_5, "35 Orchard Road Singapore 238823");
        contentValues.put(COL_6, "tester");
        contentValues.put(COL_7, "");

        //update if events is modified
        long result = db.insert(TABLE_NAME,null ,contentValues);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);

    }

    public boolean insertData(String username,String fn,String ln, String mobile, String address, String pw) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,username);
        contentValues.put(COL_2,fn);
        contentValues.put(COL_3,ln);
        contentValues.put(COL_4,mobile);
        contentValues.put(COL_5,address);
        contentValues.put(COL_6,pw);
        contentValues.put(COL_7, -1);
        //update if events is modified
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }
}
