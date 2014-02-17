package com.amplify.hiccup.service;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.SparseArray;

public class HiccupService {

    private static final String METHOD = "method";

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

    public Cursor delegateQuery(Uri uri) {
        int uriId = uriMatcher.match(uri);
        Controller controller = controllerMap.get(uriId);
        Object result = controller.get(uri);
        return httpCursorFactory.createCursor(result);
    }

    public Uri delegateInsert(Uri uri, ContentValues contentValues) {
        String method = contentValues.getAsString(METHOD);
        if ("POST".equals(method)) {
            int uriId = uriMatcher.match(uri);
            Controller controller = controllerMap.get(uriId);
            return controller.post(uri, contentValues);
        }
        else {
            throw new UnsupportedOperationException("Unsupported Http method (" + method + ")");
        }
    }
}
