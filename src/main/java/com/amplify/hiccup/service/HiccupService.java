package com.amplify.hiccup.service;

import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.SparseArray;

public class HiccupService {

    private final UriMatcher uriMatcher;
    private final String authority;
    private final SparseArray<Controller> controllerMap;
    private final HttpCursorFactory httpCursorFactory;

    private int routeIdCounter;

    public HiccupService(String authority, JsonConverter jsonConverter) {
        this(authority, new UriMatcher(UriMatcher.NO_MATCH), new SparseArray<Controller>(), new HttpCursorFactory(jsonConverter));
    }

    protected HiccupService(String authority, UriMatcher uriMatcher, SparseArray<Controller> controllerMap,
                            HttpCursorFactory httpCursorFactory) {
        this.authority = authority;
        this.uriMatcher = uriMatcher;
        this.controllerMap = controllerMap;
        this.httpCursorFactory = httpCursorFactory;
    }

    public HiccupService newRoute(String path, Controller controller) {
        routeIdCounter++;
        uriMatcher.addURI(authority, path, routeIdCounter);
        controllerMap.put(routeIdCounter, controller);
        return this;
    }

    public Cursor get(Uri uri) {
        int uriId = uriMatcher.match(uri);
        Controller controller = controllerMap.get(uriId);
        Object result = controller.get(uri);
        return httpCursorFactory.createCursor(result);
    }
}
