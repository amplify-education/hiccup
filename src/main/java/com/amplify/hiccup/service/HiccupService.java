package com.amplify.hiccup.service;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.SparseArray;

public class HiccupService {

    private static final String METHOD = "method";

    private final String authority;
    private final ContentAdapter contentAdapter;
    private final UriMatcher uriMatcher;
    private final SparseArray<ControllerInfo> controllerMap;

    private int routeIdCounter;

    public HiccupService(String authority, ContentAdapter contentAdapter) {
        this.authority = authority;
        this.contentAdapter = contentAdapter;
        this.uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        this.controllerMap = new SparseArray<ControllerInfo>();
    }

    public <R> HiccupService newRoute(String path, Class<R> modelClass, Controller<R> controller) {
        routeIdCounter++;
        String adjustedPath = removeLeadingSlashForUriMatcherPreJellyBeanMR2(path);
        uriMatcher.addURI(authority, adjustedPath, routeIdCounter);
        ControllerInfo controllerInfo = new ControllerInfo(controller, modelClass);
        controllerMap.put(routeIdCounter, controllerInfo);
        return this;
    }

    public Cursor delegateQuery(Uri uri) {
        ControllerInfo controllerInfo = getControllerInfo(uri);
        Controller controller = controllerInfo.controller;
        Iterable result = controller.get(uri);
        return contentAdapter.createCursor(result);
    }

    public Uri delegateInsert(Uri uri, ContentValues contentValues) {
        ControllerInfo controllerInfo = getControllerInfo(uri);
        Controller controller = controllerInfo.controller;
        Class<?> modelClass = controllerInfo.modelClass;
        Object model = contentAdapter.toModel(contentValues, modelClass);
        String method = contentValues.getAsString(METHOD);
        if ("POST".equals(method)) {
            return controller.post(uri, model);
        } else {
            throw new UnsupportedOperationException("Unsupported Http method (" + method + ")");
        }
    }

    ControllerInfo getControllerInfo(Uri uri) {
        int uriId = uriMatcher.match(uri);
        if (uriId == -1) {
            throw new UnsupportedOperationException("Path does not match any route (" + uri.getPath() + ")");
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
        final Class<?> modelClass;

        public ControllerInfo(Controller controller, Class<?> modelClass) {
            this.controller = controller;
            this.modelClass = modelClass;
        }
    }
}
