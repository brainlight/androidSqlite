package com.github.bkhezry.demomapdrawingtools.sqlite_lib;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
//import android.util.Log;

public class DatabasehelperExt extends SQLiteOpenHelper {
    private SQLiteDatabase myDataBase;
    private final Context myContext;
    private static final String TAG = "Databasehelper";
    private String error = "";

    // Constructor
    public DatabasehelperExt(Context context) {
        super(context, DataConfig.DATABASE_NAME, null, DataConfig.DATABASE_VERSION);
        this.myContext = context;
    }

    // Create a empty database on the system
    public void createDatabase() throws IOException {
//        boolean dbExist = checkDataBase();

//		if (dbExist) {
//			Log.e("DB Exists", "db exists");
//		}

        boolean dbExist1 = checkDataBase();
        if (!dbExist1) {
            this.getReadableDatabase();
            try {
                this.close();
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database:" + e.toString());
            }
        }
    }

    // Check database already exist or not
    private boolean checkDataBase() {
        boolean checkDB = false;
        try {
            String myPath = DataConfig.DATABASE_PATH + DataConfig.DATABASE_NAME;
            File dbfile = new File(myPath);
            checkDB = dbfile.exists();
        } catch (SQLiteException e) {
            return false;
        }
        return checkDB;
    }

    // Copies your database from your local assets-folder to the just created
    // empty database in the system folder
    private void copyDataBase() throws IOException {
        String outFileName = DataConfig.DATABASE_PATH + DataConfig.DATABASE_NAME;
        System.out.println("Create FileOutputStream..." );
        OutputStream myOutput = new FileOutputStream(outFileName);
        System.out.println("End FileOutputStream..." );

//		InputStream myInput = myContext.getAssets().open(DataConfig.DATABASE_NAME);	
//		File m_currentDir = Environment.getExternalStorageDirectory();
        File m_currentDir = myContext.getCacheDir();
        boolean init = initDataFile(m_currentDir.getAbsoluteFile().toString());
        if (init) {
            String data_file_path = m_currentDir.toString() + "/" + DataConfig.DATABASE_NAME;
            File file = new File(data_file_path);
			System.out.println("Create file..." );
            FileInputStream myInput = new FileInputStream(file);
			System.out.println("FileInputStream..." );

            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myInput.close();
            file.delete();
//			System.out.println("copy data file to database is done." );

            File[] files = m_currentDir.listFiles();
            for (int i = 0; i < files.length; i++) {
                System.out.println("file in cache:" + files[i].getName());
            }

        } else {
            System.out.println("init data file is fail.");
        }

        myOutput.flush();
        myOutput.close();
    }

    // delete database
    public void db_delete() {
        File file = new File(DataConfig.DATABASE_PATH + DataConfig.DATABASE_NAME);
        if (file.exists()) {
            file.delete();
            System.out.println("delete database file.");
        }
    }

    // Open database
    public void openDatabase() throws SQLException {
        String myPath = DataConfig.DATABASE_PATH + DataConfig.DATABASE_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

    }

    public synchronized void closeDataBase() throws SQLException {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    public void onCreate(SQLiteDatabase db) {
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
//			Log.e("Database Upgrade", "Database version higher than old.");
            db_delete();
        }
    }

    // Format: SQLiteDatabase.query(String table, String[] columns, String
    // selection, String[] selectionArgs, String groupBy, String having, String
    // orderBy)
    // parameter: selectionArgs to replace ? in select string
    // parameter: String[] args = { "first string", "second@string.com" };
    // example: Cursor cursor = db.query("TABLE_NAME", null,
    // "name=? AND email=?", args, null);
    // Cursor android.database.sqlite.SQLiteQueryBuilder.query(SQLiteDatabase
    // db, String[] projectionIn, String selection, String[] selectionArgs,
    // String groupBy, String having, String sortOrder)
    // ex:
    // queryBuilder.setTables("t1 INNER JOIN t2 ON t1.ID=t2.ID");
    // Cursor c = queryBuilder.query(myDataBase, new String[]{"t1.att1",
    // "t2.att2"}, "t1.att1=? AND t2.att2=?", new String[]{"value1","value2"},
    // null, null, null);

    public Cursor getDatabaseBasic(String tableNames, String[] returnFields, String queryStr, String groupBy, String limit) {

        // returnFields.add("id _id");
        // Cursor c = myDataBase.query(tableNames, returnFields, queryStr, null,
        // groupBy, null, null, limit);
        // return c;

        System.out.println("queryStr:" + queryStr);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(tableNames);
        Cursor c = queryBuilder.query(myDataBase, returnFields, queryStr, null, groupBy, null, null, limit);
        return c;

    }

    public long insertDatabase(String tableName, ContentValues values) {
        long createSuccessful = -1;
        if (values.size() > 0) {
            try {
                createSuccessful = myDataBase.insert(tableName, null, values);
            } catch (Exception e) {
                createSuccessful = -1;
                error = e.getMessage();
            }
        }

        return createSuccessful;
    }

    public int updateDatabase(String tableName, ContentValues values, String id) {
        int createSuccessful = -1;
        if (values.size() > 0) {
            try {
                createSuccessful = myDataBase.update(tableName, values, "id = ?", new String[]{id});
            } catch (Exception e) {
                createSuccessful = -1;
                error = e.getMessage();
            }
        }

        return createSuccessful;
    }

    public String getErrorMessage() {
        return error;
    }

    public boolean deleteData(String table, String whereClause, String[] whereArgs) {
        return myDataBase.delete(table, whereClause, whereArgs) > 0;
    }

    // test extract zip file with password
    // https://stackoverflow.com/questions/11141321/how-to-unzip-a-password-protected-archive-created-by-linux-using-java
    public boolean openFile(String zipFilePath, String Dest, String password) {
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(zipFilePath);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password);
            }
            zipFile.extractAll(Dest);

//			System.out.println("Extracted file to:" + Dest);
//			File[] files = myContext.getCacheDir().listFiles();
//			for (int i=0; i<files.length; i++ ){
//				System.out.println("file in cache:" + files[i].getName());
//			}
            return true;
        } catch (ZipException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

    }

    //https://stackoverflow.com/questions/8474821/how-to-get-the-android-path-string-to-a-file-on-assets-folder
    //read file from assets
    public boolean initDataFile(String output_folder) {
        String output_file = myContext.getCacheDir() + "/" + DataConfig.DATABASE_ZIP;
//		System.out.println("output_file:" + output_file);
        File f = new File(output_file);
//		System.out.println("f.exists:" + f.exists());
        if (!f.exists())
            try {

                InputStream is = myContext.getAssets().open(DataConfig.DATABASE_ZIP);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();

                FileOutputStream fos = new FileOutputStream(f);
                fos.write(buffer);
                fos.close();
                System.out.println("write data successful");
            } catch (Exception e) {
                System.out.println("Exception:" + e.toString());
                return false;
            }

        boolean res = openFile(output_file, output_folder, DataConfig.DATABASE_PWD);
        f.delete();
//		System.out.println("openFile:" + res);
        return res;
    }
}