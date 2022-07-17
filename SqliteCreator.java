package com.github.bkhezry.demomapdrawingtools.sqlite_lib;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
//import android.util.Log;

public class SqliteCreator {

    private static final String TAG = "SqliteCreator";
    DatabasehelperExt myDbHelper;
    ArrayList<String> returnFields = new ArrayList<String>();
    String mSqlQuery = "";
    String tableName = "";
    Context context;
    ContentValues mValues = new ContentValues();
    String groupBy = "";
    String limit = "";


    public SqliteCreator(Context context) {
        this.context = context;
        boolean init = accessDatabase();
    }

    //open data file
    private boolean accessDatabase() {
        boolean out = true;

        myDbHelper = new DatabasehelperExt(context);
        try {
            myDbHelper.createDatabase();
//			Log.e(TAG, "createDatabase done");
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            myDbHelper.openDatabase();
            /*out =updateDataListView(tableName, data);*/
        } catch (SQLException sqle) {
            throw sqle;
        }
        return out;
    }

    public void init(String tableName) {
        this.tableName = tableName;

        //clear for init data
        returnFields = new ArrayList<String>();
        mSqlQuery = "";
        mValues = new ContentValues();
    }

    public void addReturnFields(String returnField) {
        returnFields.add(returnField);
    }


    public void addQuery(String query) {
        mSqlQuery += " " + query + " ";
    }

    public void addAndQuery(String query) {
        if (mSqlQuery.isEmpty()) {
            mSqlQuery = query;
        } else {
            mSqlQuery += " AND " + query + " ";
        }
    }

    public void addOrQuery(String query) {
        if (mSqlQuery.isEmpty()) {
            mSqlQuery = query;
        } else {
            mSqlQuery += " OR " + query + " ";
        }
    }

    public void addGroupBy(String query) {
        groupBy = " " + query + " ";
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    @SuppressLint("NewApi")
    public ArrayList<ContentValues> excuteSelect() {
        String f_queryStr = null;
        if (!mSqlQuery.isEmpty()) {
            f_queryStr = mSqlQuery;
        }

        if (groupBy != null && groupBy.isEmpty()) {
            groupBy = null;
        }

        String[] f_returnFields = null;
        if (returnFields.size() > 0) {
            f_returnFields = ArrayListToArrayString(returnFields);
        }

        if (limit != null && limit.isEmpty()) {
            limit = null;
        }

        Cursor cursor = myDbHelper.getDatabaseBasic(tableName, f_returnFields, f_queryStr, groupBy, limit);

        //convert data to arrayList
        ArrayList<ContentValues> list = new ArrayList<ContentValues>();
        while (cursor.moveToNext()) {
            ContentValues lContentValues = new ContentValues();

            int colCount = cursor.getColumnCount();
            for (int i = 0; i < colCount; i++) {
                int type = cursor.getType(i);
                String column_name = cursor.getColumnName(i);
                if (type == 1) {
                    String data = cursor.getString(i);
                    Log.e("Data", "column_name:"+ column_name+ " type:"+type+" data:"+data);
                    lContentValues.put(column_name, data);
                }else if (type == 2) {
                    Double data = cursor.getDouble(i);
                    String fstr = String.format("column_name: %s type: %d data: %.6f", column_name, type, data);
                    Log.e("Data", fstr);
                    lContentValues.put(column_name, data);
                }else if (type == 3) {
                    String data = cursor.getString(i);
                    Log.e("Data", "column_name:"+ column_name+ " type:"+type+" data:"+data);
                    lContentValues.put(column_name, data);
                }

            }

            list.add(lContentValues);

        }

        return list;
    }

    private String[] ArrayListToArrayString(ArrayList<String> arraylist) {
        String[] stringArr = new String[arraylist.size()];
        stringArr = arraylist.toArray(stringArr);
        return stringArr;
    }

    //cac kiểu dữ liệu khi insert
    public void addData(String key, String value) {
        mValues.put(key, value);
    }

    public void addData(String key, boolean value) {
        mValues.put(key, value);
    }

    public void addData(String key, int value) { mValues.put(key, value); }

    public void addData(String key, Double value) { mValues.put(key, value); }
    public void addData(String key, long value) { mValues.put(key, value); }

    public long executeInsert() {
        long result = myDbHelper.insertDatabase(tableName, mValues);
        return result;
    }

    public String getError() {
        String r = myDbHelper.getErrorMessage();
        return r;
    }

    public boolean executeDelete() {
        boolean result = myDbHelper.deleteData(tableName, mSqlQuery, null);
        return result;
    }

    public void closeData() {
        myDbHelper.close();
    }

    public int executeUpdate(String id) {
        int result = myDbHelper.updateDatabase(tableName, mValues, id);
        return result;
    }

}
