package com.lzc.pineapple.cache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.lzc.pineapple.BaiduPlayActivity;
import com.lzc.pineapple.R;
import com.lzc.pineapple.cache.manager.DownloadManager;
import com.lzc.pineapple.cache.manager.DownloadManager.Request;
import com.lzc.pineapple.cache.provider.DownloadService;
import com.lzc.pineapple.cache.util.StorageUtils;
import com.lzc.pineapple.cache.widget.DownloadAdapter;
import com.lzc.pineapple.entity.CacheVideoInfo;
import com.lzc.pineapple.util.Utils;

/**
 * 缓存完成的视频列表
 * @author zengchan.lzc
 *
 */
public class CacheCompletedFragment extends Fragment implements OnItemClickListener,OnCancelListener  {
	private Button allPause;
	private Button allStart;
	private ListView listview;
	private View     emptyView;
	private DownloadAdapter adapter;
    private DownloadManager downloadManager;	
	private Cursor completedCursor = null;

	private Activity activity;
	
	private MyContentObserver mContentObserver = new MyContentObserver();
	private MyDataSetObserver mDataSetObserver = new MyDataSetObserver();

	private int mStatusColumnId;
	private int mIdColumnId;
	private int mLocalUriColumnId;
	private int mMediaTypeColumnId;
	private int mReasonColumndId;
	private int mCoverColumnId;

	private boolean mIsSortedBySize = true;
	private Set<Long> mSelectedIds = new HashSet<Long>();
	/**
	 * We keep track of when a dialog is being displayed for a pending download,
	 * because if that download starts running, we want to immediately hide the
	 * dialog.
	 */
	private Long mQueuedDownloadId = null;
	private AlertDialog mQueuedDialog;
	
	
	
	private String url = "http://img.yingyonghui.com/apk/16457/com.rovio.angrybirdsspace.ads.1332528395706.apk";

	public static final Fragment newInstance() {
		Fragment fragment = new CacheCompletedFragment();
		Bundle bundle = new Bundle();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = View.inflate(getActivity(),R.layout.fragment_caching_layout, null);
		activity = getActivity();
		downloadManager = new DownloadManager(activity.getContentResolver(),activity.getPackageName());
		downloadManager.setAccessAllDownloads(true);
		initCursor();
		initViews(view);
		chooseListToShow();
		setListener();
		return view;
	}
	@Override
	public void onResume() {
		super.onResume();
		if (haveCursors()) {
			completedCursor.registerContentObserver(mContentObserver);
			completedCursor.registerDataSetObserver(mDataSetObserver);
			refresh();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (haveCursors()) {
			completedCursor.unregisterContentObserver(mContentObserver);
			completedCursor.unregisterDataSetObserver(mDataSetObserver);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		
	}
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}
	/**
	 * Requery the database and update the UI.
	 */
	private void refresh() {
		completedCursor.requery();
	}
	private long[] getSelectionAsArray() {
		long[] selectedIds = new long[mSelectedIds.size()];
		Iterator<Long> iterator = mSelectedIds.iterator();
		for (int i = 0; i < selectedIds.length; i++) {
			selectedIds[i] = iterator.next();
		}
		return selectedIds;
	}
	private class MyContentObserver extends ContentObserver {
		public MyContentObserver() {
			super(new Handler());
		}

		@Override
		public void onChange(boolean selfChange) {
			handleDownloadsChanged();
		}
	}

	private class MyDataSetObserver extends DataSetObserver {
		@Override
		public void onChanged() {
			// may need to switch to or from the empty view
			chooseListToShow();
		}
	}
	/**
	 * Called when there's a change to the downloads database.
	 */
	void handleDownloadsChanged() {
		checkSelectionForDeletedEntries();

		if (mQueuedDownloadId != null && moveToDownload(mQueuedDownloadId)) {
			if (completedCursor.getInt(mStatusColumnId) != DownloadManager.STATUS_PAUSED
					|| !isPausedForWifi(completedCursor)) {
				mQueuedDialog.cancel();
			}
		}
	}
	/**
	 * Check if any of the selected downloads have been deleted from the
	 * downloads database, and remove such downloads from the selection.
	 */
	private void checkSelectionForDeletedEntries() {
		// gather all existing IDs...
		Set<Long> allIds = new HashSet<Long>();
		for (completedCursor.moveToFirst(); !completedCursor.isAfterLast(); completedCursor
				.moveToNext()) {
			allIds.add(completedCursor.getLong(mIdColumnId));
		}

		// ...and check if any selected IDs are now missing
		for (Iterator<Long> iterator = mSelectedIds.iterator(); iterator
				.hasNext();) {
			if (!allIds.contains(iterator.next())) {
				iterator.remove();
			}
		}
	}
	/**
	 * Move {@link #mDateSortedCursor} to the download with the given ID.
	 * 
	 * @return true if the specified download ID was found; false otherwise
	 */
	private boolean moveToDownload(long downloadId) {
		for (completedCursor.moveToFirst(); !completedCursor.isAfterLast(); completedCursor
				.moveToNext()) {
			if (completedCursor.getLong(mIdColumnId) == downloadId) {
				return true;
			}
		}
		return false;
	}
	private boolean isPausedForWifi(Cursor cursor) {
		return cursor.getInt(mReasonColumndId) == DownloadManager.PAUSED_QUEUED_FOR_WIFI;
	}

	/**
	 * Show the correct ListView and hide the other, or hide both and show the
	 * empty view.
	 */
	private void chooseListToShow() {
		listview.setVisibility(View.GONE);
		if (completedCursor == null || completedCursor.getCount() == 0) {
			emptyView.setVisibility(View.VISIBLE);
		} else {
			emptyView.setVisibility(View.GONE);
			listview.setVisibility(View.VISIBLE);
			listview.invalidateViews(); // ensure checkboxes get updated
		}
		
	}
    private void updateDownloadList(){
    	adapter.notifyDataSetChanged();
    }
	private void initCursor(){
		DownloadManager.Query baseQuery = new DownloadManager.Query().setOnlyIncludeVisibleInDownloadsUi(true);
		baseQuery.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
		completedCursor = downloadManager.query(baseQuery);
	}

	private void initViews(View view) {
		allPause = (Button) view.findViewById(R.id.all_pause);
		allStart = (Button) view.findViewById(R.id.all_start);
		listview = (ListView) view.findViewById(R.id.listview);
		emptyView = (View)view.findViewById(R.id.empty);
		view.findViewById(R.id.bottom_layout).setVisibility(View.GONE);
		
		// only attach everything to the listbox if we can access the download
		// database. Otherwise,
		// just show it empty
		if (haveCursors()) {
			activity.startManagingCursor(completedCursor);

			mStatusColumnId = completedCursor
					.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS);
			mIdColumnId = completedCursor
					.getColumnIndexOrThrow(DownloadManager.COLUMN_ID);
			mLocalUriColumnId = completedCursor
					.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI);
			mMediaTypeColumnId = completedCursor
					.getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE);
			mCoverColumnId = completedCursor
					.getColumnIndexOrThrow(DownloadManager.COLUMNN_COVER);
			mReasonColumndId = completedCursor
					.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON);
			adapter = new DownloadAdapter(activity,completedCursor,downloadManager);
			listview.setAdapter(adapter);

		}
	}
	private boolean haveCursors() {
		return completedCursor != null ;
	}
	private void setListener() {
		listview.setOnItemClickListener(this);
	}

	private void onClearClick() {

	}

	@Override
	public void onCancel(DialogInterface dialog) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		completedCursor.moveToPosition(position);
		handleItemClick(completedCursor);
		
	}
	private void handleItemClick(Cursor cursor) {
		openCurrentDownload(cursor);
	}
	private void openCurrentDownload(Cursor cursor) {
		Uri localUri = Uri.parse(cursor.getString(mLocalUriColumnId));
		try {
			activity.getContentResolver().openFileDescriptor(localUri, "r").close();
		} catch (FileNotFoundException exc) {
			
			showFailedDialog(cursor.getLong(mIdColumnId),
					getString(R.string.dialog_file_missing_body));
			return;
		} catch (IOException exc) {
			// close() failed, not a problem
		}
		BaiduPlayActivity.launch(activity, localUri);
        /**
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(localUri, cursor.getString(mMediaTypeColumnId));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_GRANT_READ_URI_PERMISSION);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			Toast.makeText(activity, R.string.download_no_application_title,
					Toast.LENGTH_LONG).show();
		}
		*/
	}

	private void showFailedDialog(long downloadId, String dialogBody) {
		new AlertDialog.Builder(activity)
				.setTitle(R.string.dialog_title_not_available)
				.setMessage(dialogBody)
				.setNegativeButton(R.string.delete_download,
						getDeleteClickHandler(downloadId))
				.setPositiveButton(R.string.retry_download,
						getRestartClickHandler(downloadId)).show();
	}
	
	/**
	 * @return an OnClickListener to delete the given downloadId from the
	 *         Download Manager
	 */
	private DialogInterface.OnClickListener getDeleteClickHandler(
			final long downloadId) {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteDownload(downloadId);
			}
		};
	}


	/**
	 * @return an OnClickListener to restart the given downloadId in the
	 *         Download Manager
	 */
	private DialogInterface.OnClickListener getRestartClickHandler(
			final long downloadId) {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				downloadManager.restartDownload(downloadId);
			}
		};
	}
	
	/**
	 * Delete a download from the Download Manager.
	 */
	private void deleteDownload(long downloadId) {
		if (moveToDownload(downloadId)) {
			int status = completedCursor.getInt(mStatusColumnId);
			boolean isComplete = status == DownloadManager.STATUS_SUCCESSFUL
					|| status == DownloadManager.STATUS_FAILED;
			String localUri = completedCursor.getString(mLocalUriColumnId);
			if (isComplete && localUri != null) {
				String path = Uri.parse(localUri).getPath();
				if (path.startsWith(Environment.getExternalStorageDirectory()
						.getPath())) {
					downloadManager.markRowDeleted(downloadId);
					return;
				}
			}
		}
		downloadManager.remove(downloadId);
	}
}
