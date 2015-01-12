package com.amplify.hiccup.service;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.SparseArray;

public class HiccupService {

    private final String authority;
    private final UriMatcher uriMatcher;
    private final SparseArray<ControllerInfo> controllerMap;

    private int routeIdCounter;

    public HiccupService(String authority) {
        this.authority = authority;
        this.uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        this.controllerMap = new SparseArray<ControllerInfo>();
    }

    public HiccupService newRoute(String path, Controller controller) {
        routeIdCounter++;
        String adjustedPath = removeLeadingSlashForUriMatcherPreJellyBeanMR2(path);
        uriMatcher.addURI(authority, adjustedPath, routeIdCounter);
        ControllerInfo controllerInfo = new ControllerInfo(controller);
        controllerMap.put(routeIdCounter, controllerInfo);
        return this;
    }

    public Cursor delegateQuery(Uri uri) {
        ControllerInfo controllerInfo = getControllerInfo(uri);
        Controller controller = controllerInfo.controller;
        return controller.get(uri);
    }

    public Uri delegateInsert(Uri uri, ContentValues contentValues) {
        ControllerInfo controllerInfo = getControllerInfo(uri);
        Controller controller = controllerInfo.controller;
        return controller.post(uri, contentValues);
    }

    public int delegateUpdate(Uri uri, ContentValues contentValues) {
        ControllerInfo controllerInfo = getControllerInfo(uri);
        Controller controller = controllerInfo.controller;
        return controller.put(uri, contentValues);
    }

    ControllerInfo getControllerInfo(Uri uri) {
        int uriId = uriMatcher.match(uri);
        if (uriId == -1) {
            throw new IllegalArgumentException("Path does not match any route (" + uri.getPath() + ")");
        }
        return controllerMap.get(uriId);
    }

    private String removeLeadingSlashForUriMatcherPreJellyBeanMR2(String path) {
        if (path.startsWith("/")) {
            return path.substring(1);
        }
        return path;
    }

    static class ControllerInfo {
        final Controller controller;

        public ControllerInfo(Controller controller) {
            this.controller = controller;
        }
    }
}
