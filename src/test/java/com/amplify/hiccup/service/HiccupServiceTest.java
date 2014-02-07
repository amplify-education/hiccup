package com.amplify.hiccup.service;

import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.SparseArray;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class HiccupServiceTest {

    private static final String ROUTE_ONE = "/path/to/collection";
    private static final String ROUTE_TWO = "/path/to/collection/resource";
    private static final String AUTHORITY = "com.authority.name";

    private HiccupService hiccupService;

    @Mock
    private UriMatcher uriMatcher;
    @Mock
    private SparseArray<Controller> controllerMap;
    @Mock
    private Controller controller;
    @Mock
    private HttpCursorFactory httpCursorFactory;

    @Before
    public void setUp() {
        initMocks(this);
        hiccupService = new HiccupService(AUTHORITY, uriMatcher, controllerMap, httpCursorFactory);
    }

    @Test
    public void shouldAddMultipleRoutes() {
        hiccupService.newRoute(ROUTE_ONE, controller);
        verify(uriMatcher).addURI(AUTHORITY, ROUTE_ONE, 1);
        verify(controllerMap).put(1, controller);

        hiccupService.newRoute(ROUTE_TWO, controller);
        verify(uriMatcher).addURI(AUTHORITY, ROUTE_TWO, 2);
        verify(controllerMap).put(2, controller);
    }

    @Test
    public void shouldDelegateGetRequestToMatchingControllerForRoute() {
        Uri uri = mock(Uri.class);
        Object expectedResult = new Object();
        Cursor expectedCursor = mock(Cursor.class);
        when(controllerMap.get(anyInt())).thenReturn(controller);
        when(controller.get(uri)).thenReturn(expectedResult);
        when(httpCursorFactory.from(expectedResult)).thenReturn(expectedCursor);

        Cursor actualCursor = hiccupService.get(uri);

        assertThat(actualCursor, is(expectedCursor));
    }
}
