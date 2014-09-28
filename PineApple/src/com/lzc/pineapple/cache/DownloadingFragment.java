package com.lzc.pineapple.cache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.lzc.pineapple.R;
import com.lzc.pineapple.cache.manager.DownloadManager;
import com.lzc.pineapple.cache.manager.DownloadManager.Request;
import com.lzc.pineapple.cache.provider.DownloadService;
import com.lzc.pineapple.cache.util.StorageUtils;
import com.lzc.pineapple.cache.widget.DownloadAdapter;
import com.lzc.pineapple.entity.CacheVideoInfo;
import com.lzc.pineapple.entity.WrapperCacheVideoInfo;
import com.lzc.pineapple.util.Constant;
import com.lzc.pineapple.util.Utils;

/**
 * 正在下载中的视频列表
 * 
 * @author zengchan.lzc
 * 
 */
public class DownloadingFragment extends Fragment implements OnItemClickListener,OnClickListener,OnCancelListener  {
	private Button allPause;
	private Button allStart;
	private ListView listview;
	private View     emptyView;
	private DownloadAdapter adapter;
	private static final String CACHE_VIDEO_INFO_KEY = "CACHE_VIDEO_INFO_KEY";
	private WrapperCacheVideoInfo cacheVideoInfo;
    private DownloadManager downloadManager;	
	private Cursor downloadingCursor = null;

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
	private String testUrl = "http://down.mumayi.com/41052/mbaidu";
	public static final Fragment newInstance(WrapperCacheVideoInfo cacheVideoInfo) {
		Fragment fragment = new DownloadingFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(CACHE_VIDEO_INFO_KEY, cacheVideoInfo);
		
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
		
		checkStorage();
		mkDir();
		renderValues();
		startDownloadService();
		startDownload();
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
			downloadingCursor.registerContentObserver(mContentObserver);
			downloadingCursor.registerDataSetObserver(mDataSetObserver);
			refresh();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (haveCursors()) {
			downloadingCursor.unregisterContentObserver(mContentObserver);
			downloadingCursor.unregisterDataSetObserver(mDataSetObserver);
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
		downloadingCursor.requery();
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
			if (downloadingCursor.getInt(mStatusColumnId) != DownloadManager.STATUS_PAUSED
					|| !isPausedForWifi(downloadingCursor)) {
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
		for (downloadingCursor.moveToFirst(); !downloadingCursor.isAfterLast(); downloadingCursor
				.moveToNext()) {
			allIds.add(downloadingCursor.getLong(mIdColumnId));
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
		for (downloadingCursor.moveToFirst(); !downloadingCursor.isAfterLast(); downloadingCursor
				.moveToNext()) {
			if (downloadingCursor.getLong(mIdColumnId) == downloadId) {
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
		if (downloadingCursor == null || downloadingCursor.getCount() == 0) {
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
	private void checkStorage() {
		if (!StorageUtils.isSDCardPresent()) {
			Utils.showTips(activity, "未发现SD卡");
			return;
		}

		if (!StorageUtils.isSdCardWrittenable()) {
			Utils.showTips(activity, "SD卡不能读写");
			return;
		}

	}

	private void mkDir() {
		try {
			StorageUtils.mkdir();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void initCursor(){
		DownloadManager.Query baseQuery = new DownloadManager.Query().setOnlyIncludeVisibleInDownloadsUi(true);
		baseQuery.setShowCompletedSuccessful(false);
		downloadingCursor = downloadManager.query(baseQuery);
	}
    private void startDownload(){
    	if(cacheVideoInfo != null){
    		startDownload(cacheVideoInfo);
    	}
    }
	
	private void startDownload(WrapperCacheVideoInfo cacheVideoInfo) {
		List<CacheVideoInfo> list = cacheVideoInfo.getList();
		for(CacheVideoInfo cvi : list){
			
			DownloadManager.Request request = new Request(cvi);
			boolean isCacheOnlyWifi = Utils.getBooleanPrefrence(activity.getApplicationContext(),
					Constant.CACHE_ONLY_WIFI_KEY);
			if(isCacheOnlyWifi){//仅仅在wifi下下载
				request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
				if(Utils.isNotWifi(activity)){
					Utils.showTips(activity, "您已设置仅在Wifi下缓存，可以到个人中心修改缓存策略");
				}
			}
			request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/");
			request.setDescription(activity.getResources().getString(R.string.app_name));
			downloadManager.enqueue(request);
		}
	}
	private void startDownloadService() {
		Intent intent = new Intent();
		intent.setClass(activity, DownloadService.class);
		activity.startService(intent);
	}

	private void renderValues() {
		cacheVideoInfo = (WrapperCacheVideoInfo) getArguments().getSerializable(
				CACHE_VIDEO_INFO_KEY);
		if(cacheVideoInfo != null){
			List<CacheVideoInfo> list = cacheVideoInfo.getList();
			for(CacheVideoInfo cvi : list){
				cvi.setUrl(Utils.encodeUrl(cvi.getUrl()));
			}
		}
	}

	private void initViews(View view) {
		allPause = (Button) view.findViewById(R.id.all_pause);
		allStart = (Button) view.findViewById(R.id.all_start);
		listview = (ListView) view.findViewById(R.id.listview);
		emptyView = (View)view.findViewById(R.id.empty);
		
		// only attach everything to the listbox if we can access the download
		// database. Otherwise,
		// just show it empty
		if (haveCursors()) {
			activity.startManagingCursor(downloadingCursor);

			mStatusColumnId = downloadingCursor
					.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS);
			mIdColumnId = downloadingCursor
					.getColumnIndexOrThrow(DownloadManager.COLUMN_ID);
			mLocalUriColumnId = downloadingCursor
					.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI);
			mMediaTypeColumnId = downloadingCursor
					.getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE);
			mCoverColumnId = downloadingCursor
					.getColumnIndexOrThrow(DownloadManager.COLUMNN_COVER);
			mReasonColumndId = downloadingCursor
					.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON);
			adapter = new DownloadAdapter(activity,downloadingCursor,downloadManager);
			listview.setAdapter(adapter);
		}
	}
	private boolean haveCursors() {
		return downloadingCursor != null ;
	}
	private void setListener() {
		allPause.setOnClickListener(this);
		allStart.setOnClickListener(this);
		listview.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.all_pause:
			onAllPauseClick();
			break;
		case R.id.all_start:
			onAllStartClick();
			break;
		default:
			break;
		}
	}

	private void onAllPauseClick() {
		downloadManager.pauseAllDownload();
	}

	private void onAllStartClick() {
		downloadManager.resumeAllDownload();
	};

	private void onClearClick() {

	}

	@Override
	public void onCancel(DialogInterface dialog) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		downloadingCursor.moveToPosition(position);
		handleItemClick(downloadingCursor);
		
	}
	private void handleItemClick(Cursor cursor) {
		long id = cursor.getInt(mIdColumnId);
		switch (cursor.getInt(mStatusColumnId)) {
		case DownloadManager.STATUS_PENDING:
		case DownloadManager.STATUS_RUNNING:
			showRunningDialog(id);
			break;

		case DownloadManager.STATUS_PAUSED:
			if (isPausedForWifi(cursor)) {
				mQueuedDownloadId = id;
				mQueuedDialog = new AlertDialog.Builder(activity)
						.setTitle(R.string.dialog_title_queued_body)
						.setMessage(R.string.dialog_queued_body)
						.setPositiveButton(R.string.keep_queued_download, null)
						.setNegativeButton(R.string.remove_download,
								getDeleteClickHandler(id))
						.setOnCancelListener(this).show();
			} else {
				showPausedDialog(id);
			}
			break;

		case DownloadManager.STATUS_SUCCESSFUL:
			openCurrentDownload(cursor);
			break;

		case DownloadManager.STATUS_FAILED:
			showFailedDialog(id, getErrorMessage(cursor));
			break;
		}
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
	}
	private void showRunningDialog(long downloadId) {
		new AlertDialog.Builder(activity)
				.setTitle(R.string.download_running)
				.setMessage(R.string.dialog_running_body)
				.setNegativeButton(R.string.cancel_running_download,
						getDeleteClickHandler(downloadId))
				.setPositiveButton(R.string.pause_download,
						getPauseClickHandler(downloadId)).show();
	}
	private void showPausedDialog(long downloadId) {
		new AlertDialog.Builder(activity)
				.setTitle(R.string.download_queued)
				.setMessage(R.string.dialog_paused_body)
				.setNegativeButton(R.string.delete_download,
						getDeleteClickHandler(downloadId))
				.setPositiveButton(R.string.resume_download,
						getResumeClickHandler(downloadId)).show();
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
	 * @return an OnClickListener to pause the given downloadId from the
	 *         Download Manager
	 */
	private DialogInterface.OnClickListener getPauseClickHandler(
			final long downloadId) {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				downloadManager.pauseDownload(downloadId);
			}
		};
	}

	/**
	 * @return an OnClickListener to resume the given downloadId from the
	 *         Download Manager
	 */
	private DialogInterface.OnClickListener getResumeClickHandler(
			final long downloadId) {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				downloadManager.resumeDownload(downloadId);
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
			int status = downloadingCursor.getInt(mStatusColumnId);
			boolean isComplete = status == DownloadManager.STATUS_SUCCESSFUL
					|| status == DownloadManager.STATUS_FAILED;
			String localUri = downloadingCursor.getString(mLocalUriColumnId);
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
	/**
	 * @return the appropriate error message for the failed download pointed to
	 *         by cursor
	 */
	private String getErrorMessage(Cursor cursor) {
		switch (cursor.getInt(mReasonColumndId)) {
		case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
			if (isOnExternalStorage(cursor)) {
				return getString(R.string.dialog_file_already_exists);
			} else {
				// the download manager should always find a free filename for
				// cache downloads,
				// so this indicates a strange internal error
				return getUnknownErrorMessage();
			}

		case DownloadManager.ERROR_INSUFFICIENT_SPACE:
			if (isOnExternalStorage(cursor)) {
				return getString(R.string.dialog_insufficient_space_on_external);
			} else {
				return getString(R.string.dialog_insufficient_space_on_cache);
			}

		case DownloadManager.ERROR_DEVICE_NOT_FOUND:
			return getString(R.string.dialog_media_not_found);

		case DownloadManager.ERROR_CANNOT_RESUME:
			return getString(R.string.dialog_cannot_resume);

		default:
			return getUnknownErrorMessage();
		}
	}
	private boolean isOnExternalStorage(Cursor cursor) {
		String localUriString = cursor.getString(mLocalUriColumnId);
		if (localUriString == null) {
			return false;
		}
		Uri localUri = Uri.parse(localUriString);
		if (!localUri.getScheme().equals("file")) {
			return false;
		}
		String path = localUri.getPath();
		String externalRoot = Environment.getExternalStorageDirectory()
				.getPath();
		return path.startsWith(externalRoot);
	}
	private String getUnknownErrorMessage() {
		return getString(R.string.dialog_failed_body);
	}
}
