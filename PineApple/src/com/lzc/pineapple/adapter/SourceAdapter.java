package com.lzc.pineapple.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzc.pineapple.R;
import com.lzc.pineapple.entity.SourceSet;

public class SourceAdapter  extends BaseAdapter{
	private List<SourceSet> setSource;
	private Context context;
	public SourceAdapter(Context context,List<SourceSet> setSource){
		this.setSource = setSource;
		this.context = context;
	}
    public void updateList(List<SourceSet> setSource){
    	this.setSource = setSource;
    	this.notifyDataSetChanged();
    }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return setSource.size();
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
		ViewHolder holder ;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = View.inflate(context,R.layout.source_item_layou, null);
			holder.image = (ImageView)convertView.findViewById(R.id.image);
			holder.select = (TextView)convertView.findViewById(R.id.select);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		if(setSource.get(position).isSelected()){
			holder.select.setVisibility(View.VISIBLE);
		}else{
			holder.select.setVisibility(View.GONE);
		}
		displaySourceIcon(position,holder.image);
		return convertView;
	}
	private void displaySourceIcon(int position,ImageView image){
		int iSrc = setSource.get(position).getiSrc();
		switch (iSrc) {
		case 1:
			image.setImageResource(R.drawable.source_1);
			break;
		case 2:
			image.setImageResource(R.drawable.source_2);
			break;
		case 3:
			image.setImageResource(R.drawable.source_3);
			break;
		case 4:
			image.setImageResource(R.drawable.source_4);
			break;
		case 5:
			image.setImageResource(R.drawable.source_5);
			break;
		case 6:
			image.setImageResource(R.drawable.source_6);
			break;
		case 7:
			image.setImageResource(R.drawable.source_7);
			break;
		case 8:
			image.setImageResource(R.drawable.source_8);
			break;
		case 9:
			image.setImageResource(R.drawable.source_9);
			break;
		case 10:
			image.setImageResource(R.drawable.source_10);
			break;
		case 11:
			image.setImageResource(R.drawable.source_11);
			break;
		case 15:
			image.setImageResource(R.drawable.source_15);
			break;
		case 16:
			image.setImageResource(R.drawable.source_16);
			break;
		case 17:
//			image.setImageResource(R.drawable.source_17);
			break;
		case 18:
//			image.setImageResource(R.drawable.source_18);
			break;
		case 25:
//			image.setImageResource(R.drawable.source_25);
			break;
		default:
			break;
		}
	}
	
}
class ViewHolder{
	ImageView image;
	TextView  select;
}
