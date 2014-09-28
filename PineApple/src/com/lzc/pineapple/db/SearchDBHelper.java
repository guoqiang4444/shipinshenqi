package com.lzc.pineapple.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SearchDBHelper {
    Context context;

    private static final String DATABASE_NAME = "local_record.db";


    public static final String TABLE_VIDEO_LOCAL_RECORDS = "videoLocalRecords";

    private static final int DATABASE_VERSION = 1;

    private static final String LOCAL_RECORD_ID = "_id";

    private static final String LOCAL_RECORD_NAME = "name";

    private static final String LOCAL_RECORD_CREATE_TIME = "createTime";

    private static final String LOCAL_RECORD_TYPE = "recordType";


    private static final String CREATE_TABLE_VIDEO_LOCAL_RECORDS = "create table videoLocalRecords("
            + LOCAL_RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // rowID
            LOCAL_RECORD_NAME + " TEXT," + // 姓名 1

            LOCAL_RECORD_CREATE_TIME + " TEXT," + // 创建时间
            LOCAL_RECORD_TYPE + " INTEGER" + // 数据类型

            ");";

    private DatabaseHelper dbHelper;

    private SQLiteDatabase mSQLiteDatabase = null;

    public static boolean isDBOpen = false;

    public SearchDBHelper(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public void open() {
        dbHelper = new DatabaseHelper(context);
        mSQLiteDatabase = dbHelper.getWritableDatabase();
        isDBOpen = true;
    }

    public void close() {
        dbHelper.close();
        isDBOpen = false;
    }

    class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub

            db.execSQL(CREATE_TABLE_VIDEO_LOCAL_RECORDS);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub

            db.execSQL("DROP TABLE IF EXISTS "
                    + TABLE_VIDEO_LOCAL_RECORDS);
            onCreate(db);

        }

    }

    // 向表中插入数据
    public long insertVideoDataToTable(LocalSearchCacheInfo localRecordsInfo) {
        // String formatTime = getSysNowTime();
        ContentValues content = new ContentValues();
        content.put(LOCAL_RECORD_NAME, localRecordsInfo.getName());

        content.put(LOCAL_RECORD_CREATE_TIME, localRecordsInfo.getCreateTime());

        content.put(LOCAL_RECORD_TYPE, localRecordsInfo.getType());
        long returnValue = 0;
        // returnValue = mSQLiteDatabase.insert(TABLE_VIDEO_LOCAL_RECORDS, null,
        // content);
        returnValue = mSQLiteDatabase.insert(TABLE_VIDEO_LOCAL_RECORDS, null,
                content);
        return returnValue;

    }

    /**
     * 取出最近搜索的五条视频记录
     * 
     * @Title: getVideoTopFive
     * @return
     * @return Cursor
     * @date 2013-3-20 下午5:45:09
     */
    public Cursor getVideoTopFive() {
        Cursor c = null;
        String strQuery = "select _id,name,recordType "
                + "from videoLocalRecords  l1  " + "where _id in "
                +
                // 用来区分重复的数据，如果有重复的数据，拿最大的id数据
                "(select max(_id) from videoLocalRecords l2 where l1.name = l2.name )"
                + " order by _id desc limit 5 ";

        c = mSQLiteDatabase.rawQuery(strQuery, null);
        if (c != null) {
            c.moveToFirst();
        } else {
            c = null;// return null;
        }
        return c;
    }

    /**
     * 清空搜视频的表中数据
     * 
     * @Title: clearTable
     * @return void
     * @date 2013-3-21 下午8:13:36
     */
    public void clearVideoTable() {
        String sql = "delete from " + TABLE_VIDEO_LOCAL_RECORDS;
        mSQLiteDatabase.execSQL(sql);
        // 数据库id复位。下次再往数据库插入数据，id从o开始
        sql = "update sqlite_sequence set seq=0 where name='videoLocalRecords'";
        mSQLiteDatabase.execSQL(sql);

    }
}
