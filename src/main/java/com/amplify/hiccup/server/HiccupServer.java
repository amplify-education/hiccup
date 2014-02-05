package com.amplify.hiccup.server;

import android.content.Context;

public abstract class HiccupServer {

    private Context context;

    public HiccupServer(Context context) {
        this.context = context;
    }

    public abstract void registerController();
}
