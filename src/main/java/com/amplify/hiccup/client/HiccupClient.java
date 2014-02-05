package com.amplify.hiccup.client;

import android.database.Cursor;
import android.net.Uri;

public interface HiccupClient {

    Cursor get(Uri uri);

}
