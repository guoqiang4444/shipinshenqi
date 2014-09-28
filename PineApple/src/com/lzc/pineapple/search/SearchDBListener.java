package com.lzc.pineapple.search;

import android.database.Cursor;

public interface SearchDBListener {
    public Cursor getCacheCursor();
    public void clearCache();
}
