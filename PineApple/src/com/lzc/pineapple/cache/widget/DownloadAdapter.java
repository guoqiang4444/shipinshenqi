/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lzc.pineapple.cache.widget;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lzc.pineapple.R;
import com.lzc.pineapple.cache.manager.DownloadManager;
import com.lzc.pineapple.util.Utils;

/**
 * List adapter for Cursors returned by {@link DownloadManager}.
 */
public class DownloadAdapter extends CursorAdapter {
	private Context mContext;
	private Cursor mCursor;
	private Resources mResources;
	private DateFormat mDateFormat;
	private DateFormat mTimeFormat;

	final private int mTitleColumnId;
	final private int mStatusColumnId;
	final private int mReasonColumnId;
	final private int mTotalBytesColumnId;
	final private int mCurrentBytesColumnId;
	final private int mMediaTypeColumnId;
	final private int mDateColumnId;
	final private int mIdColumnId;
	final private int mCoverColumnId;
	final private int mSpeedColumnId;
	
	private long lastSize = 0;
	
	private DownloadManager downloadManager;

	public DownloadAdapter(Context context, Cursor cursor,DownloadManager downloadManager) {
		super(context, cursor);
		mContext = context;
		mCursor = cursor;
		this.downloadManager = downloadManager;
		mResources = mContext.getResources();
		mDateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
		mTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);

		mIdColumnId = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_ID);
		mTitleColumnId = cursor
				.getColumnIndexOrThrow(DownloadManager.COLUMN_TITLE);
		mStatusColumnId = cursor
				.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS);
		mReasonColumnId = cursor
				.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON);
		mTotalBytesColumnId = cursor
				.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
		mCurrentBytesColumnId = cursor
				.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
		mMediaTypeColumnId = cursor
				.getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE);
		mDateColumnId = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP);
		mCoverColumnId = cursor.getColumnIndexOrThrow(DownloadManager.COLUMNN_COVER);
		mSpeedColumnId = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_SPEED_BYTES);
	}

	public View newView() {
		View view = LayoutInflater.from(mContext)
				.inflate(R.layout.cache_item_layout, null);
		
		return view;
	}

	public void bindView(View convertView) {

		long downloadId = mCursor.getLong(mIdColumnId);
//		convertView.setDownloadId(downloadId);

		// Retrieve the icon for this download
		retrieveAndSetIcon(convertView);

		String title = mCursor.getString(mTitleColumnId);
		long totalBytes = mCursor.getLong(mTotalBytesColumnId);
		long currentBytes = mCursor.getLong(mCurrentBytesColumnId);
		long cacheSpeed   = mCursor.getLong(mSpeedColumnId);

		if (title.length() == 0) {
			title = "";
		}
		setTextForView(convertView, R.id.name, title);
		
		int status = mCursor.getInt(mStatusColumnId);
		int progress = getProgressValue(totalBytes, currentBytes);
		boolean indeterminate = status == DownloadManager.STATUS_PENDING;
		ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
		progressBar.setIndeterminate(indeterminate);
		if (!indeterminate) {
			progressBar.setProgress(progress);
		}
		setTextForView(convertView, R.id.cache_size, getCurrentSizeText(currentBytes));
		setTextForView(convertView, R.id.total_size, getSizeText(totalBytes));
		setTextForView(convertView, R.id.cache_speed, getSpeedSizeText(cacheSpeed));
		
		setTextForView(convertView, R.id.cache_status,mResources.getString(getStatusStringId(status)));
//		setTextForView(convertView, R.id.last_modified_date, getDateString());
        if (status == DownloadManager.STATUS_SUCCESSFUL) {//下载完成
			
		} 
        setStatusIcon(convertView,R.id.cache_status_image,status);
	}

	private String getDateString() {
		Date date = new Date(mCursor.getLong(mDateColumnId));
		if (date.before(getStartOfToday())) {
			return mDateFormat.format(date);
		} else {
			return mTimeFormat.format(date);
		}
	}

	private Date getStartOfToday() {
		Calendar today = new GregorianCalendar();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		return today.getTime();
	}

	public int getProgressValue(long totalBytes, long currentBytes) {
		if (totalBytes == -1) {
			return 0;
		}
		return (int) (currentBytes * 100 / totalBytes);
	}

	private String getSizeText(long bytes) {
		String sizeText = "";
		if (bytes >= 0) {
			sizeText = Formatter.formatFileSize(mContext, bytes);
		}
		return sizeText ;
	}
	
	private String getCurrentSizeText(long bytes) {
		String sizeText = "";
		if (bytes >= 0) {
			sizeText = Formatter.formatFileSize(mContext, bytes);
		}
		return sizeText + "/";
	}
	
	private String getSpeedSizeText(long bytes) {
		String sizeText = "";
		if (bytes >= 0) {
			sizeText = Formatter.formatFileSize(mContext, bytes);
		}
		return sizeText + "/S";
	}

	private int getStatusStringId(int status) {
		switch (status) {
		case DownloadManager.STATUS_FAILED:
			return R.string.download_error;

		case DownloadManager.STATUS_SUCCESSFUL:
			return R.string.download_success;

		case DownloadManager.STATUS_PENDING:
		case DownloadManager.STATUS_RUNNING:
			return R.string.download_running;

		case DownloadManager.STATUS_PAUSED:
			if (mCursor.getInt(mReasonColumnId) == DownloadManager.PAUSED_QUEUED_FOR_WIFI) {
				return R.string.download_queued;
			} else {
				return R.string.download_paused;
			}
		}
		throw new IllegalStateException("Unknown status: "
				+ mCursor.getInt(mStatusColumnId));
	}

	private void retrieveAndSetIcon(View convertView) {
		String cover = mCursor.getString(mCoverColumnId);
		ImageView iconView = (ImageView) convertView.findViewById(R.id.cover);
		Utils.displayImage(cover,iconView);
	}

	private void setTextForView(View parent, int textViewId, String text) {
		TextView view = (TextView) parent.findViewById(textViewId);
		view.setText(text);
	}
	private void setStatusIcon(View parent,int viewId,int status){
		ImageView view = (ImageView) parent.findViewById(viewId);
		if(status == DownloadManager.STATUS_FAILED
				|| status == DownloadManager.STATUS_PAUSED){
			view.setImageResource(R.drawable.btn_download_pause_selector);
		}else if(status == DownloadManager.STATUS_PENDING
				|| status == DownloadManager.STATUS_RUNNING){
			view.setImageResource(R.drawable.btn_download_start_selector);
		}
		
	}

	// CursorAdapter overrides

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return newView();
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		bindView(view);
	}
}
