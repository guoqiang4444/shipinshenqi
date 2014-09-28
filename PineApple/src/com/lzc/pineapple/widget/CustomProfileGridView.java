package com.lzc.pineapple.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.lzc.pineapple.R;

/**
 * 自定义头像展示列表View
 * @author zengchan.lzc
 *
 */
public class CustomProfileGridView extends RelativeLayout implements Observer {

    private static final int        INDEX_TAG                  = 0x04 << 24;
    private static final int        DEFAULT_HORIZONTAL_DIVIDER = 16;
    private static final int        DEFAULT_VERTICAL_DIVIDER   = 16;
    private static final int        DEFAULT_COLUMN_NUM         = 4;
    private static final int        DEFAULT_CELL_HEIGHT        = 180;

    private BaseCustomGridAdapter   gridListAdapter;

    private LayoutInflater          layoutInflater;

    private OnItemClickListener     onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    private int                     cellWidth, cellHeight;
    private int                     horizontalDivider, verticalDivider;

    private int                     column;

    private Context                 context;

    public int getCellWidth() {
        return cellWidth;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public CustomProfileGridView(Context context) {
        super(context);
        init(context, null);
    }

    public CustomProfileGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        setmLayoutInflater(LayoutInflater.from(context));

        // Styleables from XML
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomProfileGridView);
        horizontalDivider = a.getDimensionPixelSize(
                R.styleable.CustomProfileGridView_horizontal_divider_space,
                DEFAULT_HORIZONTAL_DIVIDER);
        verticalDivider = a.getDimensionPixelSize(
                R.styleable.CustomProfileGridView_vertical_divider_space, DEFAULT_VERTICAL_DIVIDER);
        column = a.getInteger(R.styleable.CustomProfileGridView_column_num, DEFAULT_COLUMN_NUM);
        a.recycle();
        a = null;

    }

    public void setAdapter(BaseCustomGridAdapter adapter) {
        if (adapter == null) {
            throw new IllegalArgumentException("adapter should not be null");
        }
        gridListAdapter = adapter;
        adapter.register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != gridListAdapter) {
            gridListAdapter.register(null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        super.onLayout(changed, l, t, r, b);
        onLayoutChildren();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (null != gridListAdapter) {
            gridListAdapter.register(this);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    final OnClickListener     mOnClickListener    = new OnClickListener() {

                                                      @Override
                                                      public void onClick(View v) {
                                                          // TODO Auto-generated method stub
                                                          int index = (Integer) v.getTag(INDEX_TAG);
                                                          if (null != onItemClickListener) {
                                                              onItemClickListener.onItemClick(null,
                                                                      v, index, index);
                                                          }
                                                      }
                                                  };
    final OnLongClickListener onLongClickListener = new OnLongClickListener() {

                                                      @Override
                                                      public boolean onLongClick(View v) {
                                                          // TODO Auto-generated method stub
                                                          int index = (Integer) v.getTag(INDEX_TAG);
                                                          if (null != onItemLongClickListener) {
                                                              onItemLongClickListener
                                                                      .onItemLongClick(null, v,
                                                                              index, index);
                                                          }
                                                          return true;
                                                      }
                                                  };
    private List<View>        viewList            = new ArrayList<View>();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(gridListAdapter == null){
            return;
        }
        int pWidth = getMeasuredWidth();
        cellWidth = (pWidth - horizontalDivider * (column - 1)) / column;
        
        for(int i = 0;i < gridListAdapter.getCount();i++){
            View child = gridListAdapter.getView(i, null, null);
            LayoutParams p = (LayoutParams) child.getLayoutParams();
            if (p == null) {
                p = new LayoutParams(cellWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
                child.setLayoutParams(p);
            }
            int lpHeight = p.height;
            int childHeightSpec;
            if (lpHeight > 0) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
            } else {
                childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            }
            int childWidthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, 0, cellWidth);
            child.measure(childWidthSpec, childHeightSpec);
            cellHeight = child.getMeasuredHeight();
        }
    }

    public BaseCustomGridAdapter getAdapter() {
        return gridListAdapter;
    }

    public LayoutInflater getmLayoutInflater() {
        return layoutInflater;
    }

    public void setmLayoutInflater(LayoutInflater mLayoutInflater) {
        this.layoutInflater = mLayoutInflater;
    }

    private void onLayoutChildren() {
        // TODO Auto-generated method stub
        if(gridListAdapter == null){
            return;
        }
        removeAllViews();
        int len = gridListAdapter.getCount();
        boolean blockDescendant = getDescendantFocusability() == ViewGroup.FOCUS_BLOCK_DESCENDANTS;

        int left = 0;
        int top = 0;
        int row = 0;
        int clo = 0;

        for (int i = 0; i < len; i++) {

            row = i / column;
            clo = i % column;
            left = 0;
            top = 0;

            if (clo > 0) {
                left = (horizontalDivider + cellWidth) * clo;
            }
            if (row > 0) {
                top = (verticalDivider + cellHeight) * row;

            }
            RelativeLayout.LayoutParams lyp = new RelativeLayout.LayoutParams(cellWidth,
                    LayoutParams.WRAP_CONTENT);
            lyp.setMargins(left, top, 0, 0);
            View convertView = null;
            if (viewList.size() > i) {
                convertView = viewList.get(i);
            }
            View view = gridListAdapter.getView(i, convertView, null);
            if (i >= viewList.size()) {
                viewList.add(view);
            }
            if (!blockDescendant) {
                view.setOnClickListener(mOnClickListener);
                view.setOnLongClickListener(onLongClickListener);
            }
            view.setTag(INDEX_TAG, i);
            addView(view, lyp);
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        // TODO Auto-generated method stub
        onLayoutChildren();
    }
}
