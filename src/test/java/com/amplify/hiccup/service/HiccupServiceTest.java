package com.amplify.hiccup.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class HiccupServiceTest {

    private static final String AUTHORITY = "com.authority.name";
    private static final String ROUTE_ONE_PATH = "path/to/collection";
    private static final String ROUTE_TWO_PATH = "path/to/collection/resource";
    private static final Uri ROUTE_ONE_URI = Uri.parse("content://" + AUTHORITY + "/" + ROUTE_ONE_PATH);
    private static final Uri ROUTE_TWO_URI = Uri.parse("content://" + AUTHORITY + "/" + ROUTE_TWO_PATH);

    private HiccupService hiccupService;

    @Mock
    private Controller controller1;
    @Mock
    private Controller controller2;
    @Mock
    private ContentAdapter<SomeDomainModel> contentAdapter;

    @Before
    public void setUp() {
        initMocks(this);
        hiccupService = new HiccupService(AUTHORITY);
    }

    @Test
    public void addMultipleRoutes() {
        hiccupService.newRoute(ROUTE_ONE_PATH, controller1);
        hiccupService.newRoute(ROUTE_TWO_PATH, controller2);

        Controller actualController1 = hiccupService.getControllerInfo(ROUTE_ONE_URI).controller;
        Controller actualController2 = hiccupService.getControllerInfo(ROUTE_TWO_URI).controller;
        assertThat(actualController1).isEqualTo(controller1);
        assertThat(actualController2).isEqualTo(controller2);
    }

    @Test
    public void addMultipleRoutesWithoutLeadingSlashToSupportPreJellyBeanMR2UriMatcher() {
        hiccupService.newRoute("/" + ROUTE_ONE_PATH, controller1);
        hiccupService.newRoute("/" + ROUTE_TWO_PATH, controller2);

        Controller actualController1 = hiccupService.getControllerInfo(ROUTE_ONE_URI).controller;
        Controller actualController2 = hiccupService.getControllerInfo(ROUTE_TWO_URI).controller;
        assertThat(actualController1).isEqualTo(controller1);
        assertThat(actualController2).isEqualTo(controller2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwIllegalArgumentExceptionForNonMatchingRouteWhenDelegatingQuery() {
        hiccupService.delegateQuery(Uri.parse("content://com.fake.authority/some/fake/query/path"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwIllegalArgumentExceptionForNonMatchingRouteWhenDelegatingInsert() {
        hiccupService.delegateInsert(Uri.parse("content://com.fake.authority/some/fake/insert/path"), new ContentValues());
    }

    @Test
    public void delegateGetRequestToMatchingControllerForRoute() {
        Cursor expectedCursor = mock(Cursor.class);
        hiccupService.newRoute(ROUTE_ONE_PATH, controller1);
        when(controller1.get(ROUTE_ONE_URI)).thenReturn(expectedCursor);

        Cursor actualCursor = hiccupService.delegateQuery(ROUTE_ONE_URI);

        assertThat(actualCursor).isEqualTo(expectedCursor);
    }

    @Test
    public void delegatePostRequestToMatchingControllerForRoute() {
        hiccupService.newRoute(ROUTE_ONE_PATH, controller1);
        ContentValues contentValues = new ContentValues();
        Uri expectedUri = mock(Uri.class);
        when(controller1.post(ROUTE_ONE_URI, contentValues)).thenReturn(expectedUri);

        Uri actualUri = hiccupService.delegateInsert(ROUTE_ONE_URI, contentValues);

        verify(controller1).post(ROUTE_ONE_URI, contentValues);
        assertThat(actualUri).isEqualTo(expectedUri);
    }

    @Test
    public void delegatePutRequestToMatchingControllerForRoute() {
        hiccupService.newRoute(ROUTE_ONE_PATH, controller1);
        ContentValues contentValues = new ContentValues();
        int expectedCount = 42;
        when(controller1.put(ROUTE_ONE_URI, contentValues)).thenReturn(expectedCount);

        int actualCount = hiccupService.delegateUpdate(ROUTE_ONE_URI, contentValues);

        verify(controller1).put(ROUTE_ONE_URI, contentValues);
        assertThat(actualCount).isEqualTo(expectedCount);
    }

    private static class SomeDomainModel {}
}
