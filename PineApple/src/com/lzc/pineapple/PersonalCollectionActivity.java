package com.lzc.pineapple;

import java.util.ArrayList;
import java.util.List;

import com.lzc.pineapple.PlayHistoryActivity.VideoAdapter;
import com.lzc.pineapple.PlayHistoryActivity.ViewHolder;
import com.lzc.pineapple.db.PlayCacheInfo;
import com.lzc.pineapple.db.PlayHistoryDBHelper;
import com.lzc.pineapple.entity.RecommendVideo;
import com.lzc.pineapple.util.Utils;
import com.lzc.pineapple.util.Utils.OnDialogDoneListener;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * 个人收藏展示界面
 * @author zengchan.lzc
 *
 */
public class PersonalCollectionActivity extends BaseFragmentActivity implements OnClickListener{
	private ImageView                   back;
    private TextView                    title;
    private TextView                    edit;
    private ListView                    listview;
    private VideoAdapter                videoAdapter;
    private PlayHistoryDBHelper         dbHelper;
    private List<PlayCacheInfo>         list = new ArrayList<PlayCacheInfo>();
    private int                         currentPos = -1;
    private static final int            CONTEXT_MENU_ITEM_ID_DELETE = 0x01;
    private static final int            CONTEXT_MENU_ITEM_ID_CANCEL = 0x02;

    public static void launch(Context c) {
        Intent intent = new Intent(c, PersonalCollectionActivity.class);
        c.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        openDBhelper();
        setContentView(R.layout.activity_play_history_layout);
        initViews();
        setListener();
        getValues();
    }
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	MobclickAgent.onResume(this);
    }
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	MobclickAgent.onPause(this);
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        closeDBHelper();
    }
    private void initViews(){
        back = (ImageView)findViewById(R.id.back);
        title = (TextView)findViewById(R.id.title);
        title.setText(R.string.personal_collection);
        edit = (TextView)findViewById(R.id.right_text);
        edit.setText(R.string.clear);
        edit.setVisibility(View.VISIBLE);
       
        listview = (ListView)findViewById(R.id.listview);
        registerForContextMenu(listview);
        videoAdapter = new VideoAdapter();
        listview.setAdapter(videoAdapter);
    }
    private void setListener(){
    	back.setOnClickListener(this);
    	edit.setOnClickListener(this);
    	listview.setOnItemClickListener(onItemClickListener);
    	listview.setOnItemLongClickListener(onItemLongClickListener);
    }
    private void getValues(){
    	list = (List<PlayCacheInfo>) dbHelper.getCollectionVideoList();
    	videoAdapter.notifyDataSetChanged();
    }
    private void openDBhelper() {
        dbHelper = new PlayHistoryDBHelper(this);
        dbHelper.open();
    }

    private void closeDBHelper() {
        dbHelper.close();
    }
    final OnItemLongClickListener onItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			currentPos = position;
			listview.showContextMenu();
			return true;
		}
	};
	final OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			PlayCacheInfo playCacheInfo = list.get(position);
			int type = playCacheInfo.getType();
			if(type == 0 || type == 2){//特色专区,以及直接播放的
				RecommendVideo recommendVideo = new RecommendVideo();
				recommendVideo.setImg(playCacheInfo.getCover());
				recommendVideo.setTitle(playCacheInfo.getVideoName());
				List<String> src = new ArrayList<String>();
				src.add(playCacheInfo.getUrl());
				recommendVideo.setSrc(src);
				FeatureAreaVideoDetailActivity.launch(PersonalCollectionActivity.this, recommendVideo);
			}else{
				VideoDetailActivity.launch(PersonalCollectionActivity.this, playCacheInfo.getVideoId(), playCacheInfo.getVideoName());
			}
		}
	};
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        // TODO Auto-generated method stub
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CONTEXT_MENU_ITEM_ID_DELETE, 0, R.string.delete);
        menu.add(0, CONTEXT_MENU_ITEM_ID_CANCEL, 0, R.string.cancel);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case CONTEXT_MENU_ITEM_ID_DELETE:
                showDeleteDialog();
                return true;
            case CONTEXT_MENU_ITEM_ID_CANCEL:

                return true;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
    class VideoAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if(convertView == null){
				holder = new ViewHolder();
				convertView = View.inflate(PersonalCollectionActivity.this, R.layout.play_item_layout, null);
				holder.cover = (ImageView)convertView.findViewById(R.id.cover);
				holder.name = (TextView)convertView.findViewById(R.id.name);
				holder.time = (TextView)convertView.findViewById(R.id.time);
				holder.check = (CheckBox)convertView.findViewById(R.id.check);		
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			PlayCacheInfo playCacheInfo = list.get(position);
			holder.time.setText(playCacheInfo.getTime());
			holder.name.setText(playCacheInfo.getVideoName());
			if(playCacheInfo.isVisible()){
				holder.check.setVisibility(View.VISIBLE);
			}else{
				holder.check.setVisibility(View.GONE);
			}
			holder.check.setChecked(playCacheInfo.isSelected());
			Utils.displayImage(playCacheInfo.getCover(), holder.cover);
			return convertView;
		}

    	
    }
    class ViewHolder{
    	ImageView cover;
    	TextView  name;
    	TextView  time;
    	CheckBox  check;
    }
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.back:
			onBackClick();
			break;
		case R.id.right_text:
			onClearClick();
		default:
			break;
		}
	}
	private void onBackClick(){
		onBackPressed();
	}
	private void showDeleteDialog(){
		Utils.showDialog(this, "确定要删除这条记录吗？", new OnDialogDoneListener(){

			@Override
			public void onDialogDone() {
				// TODO Auto-generated method stub
				String videoId = list.get(currentPos).getVideoId();
				dbHelper.deleteCollectionRow(videoId);
				list.remove(currentPos);
				videoAdapter.notifyDataSetChanged();
			}

			@Override
			public void onDialogCancel() {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	private void onClearClick(){
		if(list.isEmpty()){
			return;
		}
		Utils.showDialog(this, "确定要清除所有记录吗？", new OnDialogDoneListener(){

			@Override
			public void onDialogDone() {
				// TODO Auto-generated method stub
				dbHelper.clearCollectionTable();
				list.clear();
				videoAdapter.notifyDataSetChanged();
			}

			@Override
			public void onDialogCancel() {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
}
