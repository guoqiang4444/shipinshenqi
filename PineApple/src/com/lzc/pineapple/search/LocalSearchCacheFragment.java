package com.lzc.pineapple.search;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lzc.pineapple.R;

public class LocalSearchCacheFragment extends Fragment implements OnClickListener {
    private Cursor                  localSearchCacheCursor;
    private ListView                localSearchCacheList;
    private LocalSearchCacheAdapter localSearchCacheAdapter;

    private LinearLayout            changeFragment;

    private Button                  clear;

    private LayoutInflater          inflater;

    private SearChListener          searchListener;
    private SearchDBListener        searchDBListener;
    

    public static final Fragment newInstance() {
        Fragment fragment = new LocalSearchCacheFragment();
        Bundle bundle = new Bundle();
        // bundle.putString(Constant.DEVICE_NAME_KEY, deviceName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        this.inflater = inflater;
        View view = inflater.inflate(R.layout.fragment_search_cache_layout, container, false);
        initViews(view);
        initAdapter();
        return view;
    }

    private void initViews(View view) {
        clear = (Button) view.findViewById(R.id.clear);
        clear.setOnClickListener(this);
        changeFragment = (LinearLayout) view.findViewById(R.id.change_layout);
        changeFragment.setOnClickListener(this);
        localSearchCacheList = (ListView) view.findViewById(R.id.listview);
        localSearchCacheList.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub

                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:

                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:

                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        hideInputMethod();
                        break;

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                // TODO Auto-generated method stub

            }

        });
    }
    
    private void initAdapter(){
        localSearchCacheCursor = searchDBListener.getCacheCursor();
        getActivity().startManagingCursor(localSearchCacheCursor);
        localSearchCacheAdapter = new LocalSearchCacheAdapter(getActivity(), localSearchCacheCursor, true);
        localSearchCacheList.setAdapter(localSearchCacheAdapter);
    }

    private void hideInputMethod() {

        InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getActivity().getCurrentFocus().getWindowToken(), 0);

    }

    public void setSearchListener(SearChListener searchListener) {
        this.searchListener = searchListener;
    }
    public void setSearchDBListener(SearchDBListener searchDBListener){
        this.searchDBListener = searchDBListener;
    }
    class LocalSearchCacheAdapter extends CursorAdapter {

        private static final int name_index = 1;
        private static final int type_index = 2;

        public LocalSearchCacheAdapter(Context context, Cursor c) {
            super(context, c);
            // TODO Auto-generated constructor stub
        }

        public LocalSearchCacheAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);

        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // TODO Auto-generated method stub

            TextView name = (TextView) view.findViewById(R.id.text);
            //name.setEllipsize(TextUtils.TruncateAt.END);
            final String[] fName = new String[1];
            fName[0] = cursor.getString(name_index);
            name.setText(fName[0]);
            name.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    searchListener.search(fName[0]);
                }

            });
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup vg) {
            // TODO Auto-generated method stub
            View view = inflater.inflate(R.layout.search_cache_list_item_layout, null);

            bindView(view, context, cursor);

            return view;
        }

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.clear:
                searchDBListener.clearCache();
                break;
            case R.id.change_layout:
                searchListener.changeFragment(SearchFragment.HOT_VIDEO_FRAME);
                break;

            default:
                break;
        }
    }

}
