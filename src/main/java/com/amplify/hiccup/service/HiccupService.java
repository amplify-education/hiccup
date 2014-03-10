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

    public HiccupService(String authority) {
        this(authority, new UriMatcher(UriMatcher.NO_MATCH), new SparseArray<Controller>(), new HttpCursorFactory());
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
        Controller controller = getController(uri);
        Response result = controller.get(uri);
        return httpCursorFactory.createCursor(result);
    }

    public Uri delegateInsert(Uri uri, ContentValues contentValues) {
        Controller controller = getController(uri);
        String method = contentValues.getAsString(METHOD);
        if ("POST".equals(method)) {
            return controller.post(uri, contentValues);
        }
        else {
            throw new UnsupportedOperationException("Unsupported Http method (" + method + ")");
        }
    }

    private Controller getController(Uri uri) {
        int uriId = uriMatcher.match(uri);
        if (uriId == -1) {
            throw new UnsupportedOperationException("Path does not match any route (" + uri.getPath() + ")");
        }
        return controllerMap.get(uriId);
    }
}
