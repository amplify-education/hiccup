package com.amplify.hiccup.client;

import android.content.ContentValues;

public interface RequestAdapter {
    ContentValues toValues(Object model);
}
