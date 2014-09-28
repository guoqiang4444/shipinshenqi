package com.lzc.pineapple.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 播放记录缓存数据库帮助类
 * 
 * @author zengchan.lzc
 * 
 */
public class PlayHistoryDBHelper {
	Context context;

	private static final String DATABASE_NAME = "play_history.db";

	public static final String TABLE_VIDEO_LOCAL_RECORDS = "play_history_records";

	public static final String TABLE_COLLECTION_RECORDS = "collection_records";

	private static final int DATABASE_VERSION = 1;

	private static final String LOCAL_RECORD_ID = "_id";

	private static final String LOCAL_RECORD_NAME = "name";

	private static final String LOCAL_RECORD_CREATE_TIME = "createTime";

	private static final String LOCAL_RECORD_COVER = "cover";

	private static final String LOCAL_RECORD_VIDEO_ID = "videoId";
	
	private static final String LOCAL_RECORD_TYPE = "type";
	
	private static final String LOCAL_RECORD_URL = "url";

	private static final String CREATE_TABLE_VIDEO_LOCAL_RECORDS = "create table play_history_records("
			+ LOCAL_RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // rowID
			LOCAL_RECORD_NAME + " TEXT," + // 视频名称

			LOCAL_RECORD_CREATE_TIME + " TEXT," + // 播放时间
			LOCAL_RECORD_VIDEO_ID + " TEXT," + // 视频id
			LOCAL_RECORD_COVER + " TEXT," + // 封面url
			LOCAL_RECORD_TYPE + " INTEGER," + // 数据类型
			LOCAL_RECORD_URL + " TEXT" + // url

			");";

	private static final String CREATE_TABLE_COLLECTION_RECORDS = "create table collection_records("
			+ LOCAL_RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // rowID
			LOCAL_RECORD_NAME + " TEXT," + // 视频名称

			LOCAL_RECORD_CREATE_TIME + " TEXT," + // 播放时间
			LOCAL_RECORD_VIDEO_ID + " TEXT," + // 视频id
			LOCAL_RECORD_COVER + " TEXT," + // 封面url
			LOCAL_RECORD_TYPE + " INTEGER," + // 数据类型
			LOCAL_RECORD_URL + " TEXT" + // url
			");";

	private DatabaseHelper dbHelper;

	private SQLiteDatabase mSQLiteDatabase = null;

	public static boolean isDBOpen = false;

	public PlayHistoryDBHelper(Context context) {
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
			db.execSQL(CREATE_TABLE_COLLECTION_RECORDS);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

			db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEO_LOCAL_RECORDS);
			db.execSQL("DROP TABLE IF EXISTS "
					+ CREATE_TABLE_COLLECTION_RECORDS);
			onCreate(db);

		}

	}

	// 向表中插入数据
	public long insertPlayVideoDataToTable(PlayCacheInfo playCacheInfo) {
		return mSQLiteDatabase.insert(TABLE_VIDEO_LOCAL_RECORDS, null,
				initContent(playCacheInfo));

	}

	// 向表中插入数据
	public long insertCollectionVideoDataToTable(PlayCacheInfo playCacheInfo) {
		
		return mSQLiteDatabase.insert(TABLE_COLLECTION_RECORDS, null,
				initContent(playCacheInfo));

	}
    private ContentValues initContent(PlayCacheInfo playCacheInfo){
    	ContentValues content = new ContentValues();
		content.put(LOCAL_RECORD_NAME, playCacheInfo.getVideoName());

		content.put(LOCAL_RECORD_CREATE_TIME, playCacheInfo.getTime());

		content.put(LOCAL_RECORD_COVER, playCacheInfo.getCover());
		content.put(LOCAL_RECORD_VIDEO_ID, playCacheInfo.getVideoId());
		content.put(LOCAL_RECORD_TYPE, playCacheInfo.getType());
		content.put(LOCAL_RECORD_URL, playCacheInfo.getUrl());
		return content;
    }
	/**
	 * 
	 * 
	 * 
	 * @return
	 * @return Cursor
	 * @date 2013-3-20 下午5:45:09
	 */
	public Cursor getPlayVideoCursor() {
		Cursor c = null;
		String strQuery = "select * " + "from play_history_records  l1  "
				+ "where _id in "
				+
				// 用来区分重复的数据，如果有重复的数据，拿最大的id数据
				"(select max(_id) from play_history_records l2 where l1.videoId = l2.videoId )"
				+ " order by _id desc";

		c = mSQLiteDatabase.rawQuery(strQuery, null);
		if (c != null && c.getCount() > 0) {
			c.moveToFirst();
		} else {
			c = null;// return null;
		}
		return c;
	}
	public Cursor getCollectionVideoCursor() {
		Cursor c = null;
		String strQuery = "select * " + "from collection_records  l1  "
				+ "where _id in "
				+
				// 用来区分重复的数据，如果有重复的数据，拿最大的id数据
				"(select max(_id) from collection_records l2 where l1.videoId = l2.videoId )"
				+ " order by _id desc";

		c = mSQLiteDatabase.rawQuery(strQuery, null);
		if (c != null && c.getCount() > 0) {
			c.moveToFirst();
		} else {
			c = null;// return null;
		}
		return c;
	}
	public List<PlayCacheInfo> getPlayVideoList(){
		Cursor cursor = getPlayVideoCursor();
		return getVideoList(cursor);
	}
	public List<PlayCacheInfo> getCollectionVideoList(){
		Cursor cursor = getCollectionVideoCursor();
		return getVideoList(cursor);
	}
	private List<PlayCacheInfo> getVideoList(Cursor cursor){
		
		List<PlayCacheInfo> list = new ArrayList<PlayCacheInfo>();
		if(cursor != null ){
			try{
				do{
					PlayCacheInfo playCacheInfo = new PlayCacheInfo();
					playCacheInfo.setCover(cursor.getString(cursor.getColumnIndex(LOCAL_RECORD_COVER)));
					playCacheInfo.setVideoName(cursor.getString(cursor.getColumnIndex(LOCAL_RECORD_NAME)));
					playCacheInfo.setVideoId(cursor.getString(cursor.getColumnIndex(LOCAL_RECORD_VIDEO_ID)));
					playCacheInfo.setTime(cursor.getString(cursor.getColumnIndex(LOCAL_RECORD_CREATE_TIME)));
					playCacheInfo.setType(cursor.getInt(cursor.getColumnIndex(LOCAL_RECORD_TYPE)));
					playCacheInfo.setUrl(cursor.getString(cursor.getColumnIndex(LOCAL_RECORD_URL)));
					list.add(playCacheInfo);
				}while(cursor.moveToNext());
			}finally{
				cursor.close();
			}
		}
		
		return list;
	}
	

	public boolean deletePlayHistoryRow(String videoId) {
		return mSQLiteDatabase.delete(TABLE_VIDEO_LOCAL_RECORDS,
				LOCAL_RECORD_VIDEO_ID + "= ?" ,new String[]{videoId}) > 0;
	}
    public boolean deleteCollectionRow(String videoId){
    	return mSQLiteDatabase.delete(TABLE_COLLECTION_RECORDS,
				LOCAL_RECORD_VIDEO_ID + "= ?" ,new String[]{videoId}) > 0;
    }
    public boolean isCollected(String videoId){
		String strQuery = "select * from collection_records where videoId = ?";
		Cursor c = mSQLiteDatabase.rawQuery(strQuery, new String[]{videoId});
	    if(c != null && c.getCount() > 0){
	    	return true;
	    }
	    return false;
    }
	/**
	 * 清空搜视频的表中数据
	 * 
	 * @Title: clearTable
	 * @return void
	 * @date 2013-3-21 下午8:13:36
	 */
	public void clearPlayVideoTable() {
		String sql = "delete from " + TABLE_VIDEO_LOCAL_RECORDS;
		mSQLiteDatabase.execSQL(sql);
		// 数据库id复位。下次再往数据库插入数据，id从o开始
		sql = "update sqlite_sequence set seq=0 where name='play_history_records'";
		mSQLiteDatabase.execSQL(sql);

	}
	public void clearCollectionTable(){
		String sql = "delete from " + TABLE_COLLECTION_RECORDS;
		mSQLiteDatabase.execSQL(sql);
		// 数据库id复位。下次再往数据库插入数据，id从o开始
		sql = "update sqlite_sequence set seq=0 where name='collection_records'";
		mSQLiteDatabase.execSQL(sql);
	}
}
