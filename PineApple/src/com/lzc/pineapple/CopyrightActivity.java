package com.lzc.pineapple;

import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class CopyrightActivity extends BaseFragmentActivity implements OnClickListener{
	private ImageView                   back;
    private TextView                    title;
	
	public static void launch(Context c) {
		Intent intent = new Intent(c, CopyrightActivity.class);
		c.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_copyright_layout);
		initViews();
		setListener();
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
	}
	private void initViews(){
	    back = (ImageView)findViewById(R.id.back);
	    title = (TextView)findViewById(R.id.title);
	    title.setText(R.string.copyright);
	}
    private void setListener(){
    	back.setOnClickListener(this);
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			onBackClick();
			break;

		default:
			break;
		}
	}
	private void onBackClick(){
		onBackPressed();
	}
}
