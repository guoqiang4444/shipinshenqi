package com.lzc.pineapple.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzc.pineapple.R;
import com.lzc.pineapple.util.Utils;


public class SearchTopFragment extends Fragment implements OnClickListener{
    
    private ImageView search;
    private ImageView delete;
    private EditText editText;
    private TextView searchTextView;
    
    private SearChListener searchListener ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.search_top_fragment, null);
        initViews(view);
        setListener();
        return view;
    }
    
    private void initViews(View view) {

        searchTextView = (TextView) view.findViewById(R.id.search);
        
        delete = (ImageView)view.findViewById(R.id.image_delete);

        search = (ImageView) view.findViewById(R.id.image_search);
        editText = (EditText) view.findViewById(R.id.editext_content);
        editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

    }
    
    private void setListener(){
        
        searchTextView.setOnClickListener(this);
        search.setOnClickListener(this);
        editText.setOnClickListener(this);
        delete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            case R.id.search:
                search();
                break;
            case R.id.image_search:
                break;
            case R.id.editext_content:
                String nowSearch = editText.getText().toString();
                goToLocalSearchCache();
                break;
            case R.id.image_delete:
                clearEditContent();
                break;
            }
    }
    private void goToLocalSearchCache(){
        searchListener.changeFragment(SearchFragment.LOCAL_SEARCH_CACHE_FRAME);
    }
    private void search(){
        String nowSearch = editText.getText().toString();
        if (nowSearch == null || nowSearch.trim().length() == 0) {
            // 输入的搜索内容无效
            Utils.showTips(getActivity(),getActivity().getResources().getString(R.string.more_search_input_invalid));
            return;
        }

        searchListener.search(nowSearch);
    }
    public void setSearchListener(SearChListener searchListener){
        this.searchListener = searchListener;
    }
   
    private void clearEditContent(){
        editText.setText("");   
    }
}
