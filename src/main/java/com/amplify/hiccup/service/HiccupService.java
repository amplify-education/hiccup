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

    /**
     * Creates a new instance of a Hiccup service with the given {@code authority}.
     * All incoming requests need to match both the authority and the path.
     *
     * @see #newRoute(String, Controller)
     */
    public HiccupService(String authority) {
        this.authority = authority;
        this.uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        this.controllerMap = new SparseArray<ControllerInfo>();
    }

    /**
     * Registers a {@link Controller} to handle requests for a given path.
     * Leading slashes in the path are valid and work in all versions of Android.
     *
     * <p>Routes would typically be added within {@link android.content.ContentProvider#onCreate()}.</p>
     *
     * @param path the path to match. * may be used as a wild card for any text, and # may
     *     be used as a wild card for numbers.
     * @param controller the {@link Controller} that the service will delegate requests
     *     to for the given {@code path}.
     *
     * @return {@link HiccupService} for fluent interface.
     */
    public HiccupService newRoute(String path, Controller controller) {
        routeIdCounter++;
        String adjustedPath = removeLeadingSlashForUriMatcherPreJellyBeanMR2(path);
        uriMatcher.addURI(authority, adjustedPath, routeIdCounter);
        ControllerInfo controllerInfo = new ControllerInfo(controller);
        controllerMap.put(routeIdCounter, controllerInfo);
        return this;
    }

    /**
     * Delegates to the appropriate {@link Controller#get(android.net.Uri)}
     * for a matching route registered in {@link #newRoute(String, Controller)}.
     *
     * <p>This method would typically be called from within
     * {@link android.content.ContentProvider#query(android.net.Uri, String[], String, String[], String)}.</p>
     *
     * <p>The SQLite parameters are intentionally ignored to encourage HTTP GET semantics and RESTful requests.
     * However, the {@link Controller} may choose to implement data subsets/filtering based on query
     * parameters, eg, {@code GET /categories?type=toys}</p>
     *
     * @param uri the uri of the request.
     *
     * @throws IllegalArgumentException if no route exists to handle the request.
     *
     * @return cursor result set returned from the {@link Controller#get(android.net.Uri)}
     *     that handled the request.
     */
    public Cursor delegateQuery(Uri uri) {
        ControllerInfo controllerInfo = getControllerInfo(uri);
        Controller controller = controllerInfo.controller;
        return controller.get(uri);
    }

    /**
     * Delegates to the appropriate {@link Controller#post(android.net.Uri, android.content.ContentValues)}
     * for a matching route registered in {@link #newRoute(String, Controller)}.
     *
     * <p>This method would typically be called from within
     * {@link android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)}.</p>
     *
     * @param uri the uri of the request.
     * @param contentValues the requested values to POST
     *
     * @throws IllegalArgumentException if no route exists to handle the request.
     *
     * @return Uri the one returned from the {@link Controller#post(android.net.Uri, android.content.ContentValues)}
     *     that handled the request.
     */
    public Uri delegateInsert(Uri uri, ContentValues contentValues) {
        ControllerInfo controllerInfo = getControllerInfo(uri);
        Controller controller = controllerInfo.controller;
        return controller.post(uri, contentValues);
    }

    /**
     * Delegates to the appropriate {@link Controller#put(android.net.Uri, android.content.ContentValues)}
     * for a matching route registered in {@link #newRoute(String, Controller)}.
     *
     * <p>This method would typically be called from within
     * {@link android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, String, String[])}.</p>
     *
     * <p>The SQLite parameters are intentionally ignored to encourage HTTP PUT semantics and RESTful requests.</p>
     *
     * @param uri the uri of the request.
     * @param contentValues the requested values to PUT
     *
     * @throws IllegalArgumentException if no route exists to handle the request.
     *
     * @return int the number of affected rows returned from the
     *     {@link Controller#post(android.net.Uri, android.content.ContentValues)} that handled the request.
     */
    public int delegateUpdate(Uri uri, ContentValues contentValues) {
        ControllerInfo controllerInfo = getControllerInfo(uri);
        Controller controller = controllerInfo.controller;
        return controller.put(uri, contentValues);
    }

    /**
     * Delegates to the appropriate {@link Controller#delete(android.net.Uri)}
     * for a matching route registered in {@link #newRoute(String, Controller)}.
     *
     * <p>This method would typically be called from within
     * {@link android.content.ContentProvider#delete(android.net.Uri, String, String[])}.</p>
     *
     * <p>The SQLite parameters are intentionally ignored to encourage HTTP DELETE semantics
     * and RESTful requests. However, the {@link Controller} may choose to implement deletion
     * filtering based on query parameters, eg, {@code DELETE /categories?type=toys}</p>
     *
     * @param uri the uri of the request.
     *
     * @throws IllegalArgumentException if no route exists to handle the request.
     *
     * @return int the number of affected rows returned from the
     *     {@link Controller#delete(android.net.Uri)} that handled the request.
     */
    public int delegateDelete(Uri uri) {
        ControllerInfo controllerInfo = getControllerInfo(uri);
        Controller controller = controllerInfo.controller;
        return controller.delete(uri);
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
