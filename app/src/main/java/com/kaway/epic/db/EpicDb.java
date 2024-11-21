package com.kaway.epic.db;

import static com.kaway.epic.EpicConstants.EPIC_LOG_TAG;
import static com.kaway.epic.EpicConstants.EPIC_TABLE_NAME;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class EpicDb  extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "epic.db";
    private static final int DATABASE_VERSION = 1;

    public static String VID_LIST_ID_COL ="VID_LIST_ID";
    public static String VID_LIST_COL ="VID_LIST";
    public static String SHOWN_COL ="SHOWN";

    // SQL statement to create the tasks table
    private static final String TABLE_CREATE =
            "CREATE TABLE "+EPIC_TABLE_NAME+" ("+
                    VID_LIST_ID_COL + " INTEGER PRIMARY KEY, " +
                    VID_LIST_COL+ " TEXT, " +
                    SHOWN_COL+ " INTEGER );";

    public EpicDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if necessary
        Log.i(EPIC_LOG_TAG,"EpidDb onUpgrade called");
        db.execSQL("DROP TABLE IF EXISTS " + EPIC_TABLE_NAME);
        onCreate(db);
        // Add more if any.
    }
}
