package com.kaway.epic.db;


import static com.kaway.epic.EpicConstants.EPIC_TABLE_NAME;
import static com.kaway.epic.db.EpicDb.SHOWN_COL;
import static com.kaway.epic.db.EpicDb.VID_LIST_COL;
import static com.kaway.epic.db.EpicDb.VID_LIST_ID_COL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EpicDbDao {

    private static final Logger LOG = LoggerFactory.getLogger(EpicDbDao.class);

    private SQLiteDatabase database;
    private final EpicDb epicDb;

    public EpicDbDao(Context context) {
        epicDb = new EpicDb(context);
    }

    // Open the database for read/write operations
    public void open() throws SQLException {
        database = epicDb.getWritableDatabase();
    }

    // Close the database
    public void close() {
        epicDb.close();
    }

    public long insertVidList(Long vidListId, List<String> vidIds) {
        database = epicDb.getWritableDatabase();

        String vidListJson = new Gson().toJson(vidIds);

        ContentValues values = new ContentValues();
        values.put(VID_LIST_ID_COL, vidListId);
        values.put(VID_LIST_COL, vidListJson);
        values.put(SHOWN_COL, 0);

        long op = database.insert(EPIC_TABLE_NAME, null, values);
        epicDb.close();

        return op;
    }

    // Update an existing task in the database
    public int updateVidListAsShown(Long vidListId) {
        database = epicDb.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SHOWN_COL, 0);

        int op =  database.update(EPIC_TABLE_NAME, values, VID_LIST_ID_COL+" = ?", new String[]{String.valueOf(vidListId)});
        epicDb.close();

        return op;
    }

    public JSONArray getViIdList(Long vidListId){
        database = epicDb.getWritableDatabase();

        String[] columnNames = new String[] {VID_LIST_ID_COL, VID_LIST_COL, SHOWN_COL};
        String whereClause = VID_LIST_ID_COL+"="+vidListId;
        JSONArray jsonArray = new JSONArray();

        Cursor cursor = database.query(EPIC_TABLE_NAME, columnNames, whereClause, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String vidIdList = cursor.getString(cursor.getColumnIndexOrThrow(VID_LIST_COL));
                try {
                    JSONArray currJsonArray = new JSONArray(vidIdList);
                    for(int i=0;i<currJsonArray.length();i++){
                        jsonArray.put(currJsonArray.get(i));
                    }
                } catch (JSONException e) {
                    LOG.error(e.getMessage(),e);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }

        epicDb.close();
        return jsonArray;
    }
}
