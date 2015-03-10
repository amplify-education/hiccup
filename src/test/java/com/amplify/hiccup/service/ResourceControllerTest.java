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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class ResourceControllerTest {

    private static Uri POST_RESPONSE = Uri.parse("/some/post/response");

    private ResourceController resourceController;

    @Mock
    private ContentAdapter contentAdapter;

    @Before
    public void setUp() {
        initMocks(this);

        resourceController = new ResourceControllerImpl(contentAdapter, Object.class);
    }

    @Test
    public void convertCollectionOfModelsToCursorResponseOnGet() {
        Cursor expectedCursor = mock(Cursor.class);
        when(contentAdapter.toCursor(any(Iterable.class))).thenReturn(expectedCursor);

        Cursor actualCursor = resourceController.get(Uri.EMPTY);

        assertThat(actualCursor).isEqualTo(expectedCursor);
    }

    @Test
    public void convertReturnUriFromSubclassOnPost() {
        Uri actualUri = resourceController.post(any(Uri.class), any(ContentValues.class));

        assertThat(actualUri).isEqualTo(POST_RESPONSE);
    }

    @Test
    public void invokeSubclassGetHandlerOnGet() {
        ResourceController resourceControllerSpy = spy(resourceController);

        resourceControllerSpy.get(Uri.parse("/path/to/resource"));

        verify(resourceControllerSpy).handleGet(Uri.parse("/path/to/resource"));
    }

    @Test
    public void invokeSubclassPostHandlerWithModelOnPost() {
        ContentValues contentValues = new ContentValues();
        Object expectedModel = new Object();
        when(contentAdapter.toModel(contentValues, expectedModel.getClass())).thenReturn(expectedModel);
        ResourceController resourceControllerSpy = spy(resourceController);

        resourceControllerSpy.post(Uri.parse("/some/path/here"), contentValues);

        verify(resourceControllerSpy).handlePost(Uri.parse("/some/path/here"), expectedModel);
    }

    @Test
    public void invokeSubclassPutHandlerWithModelOnPut() {
        ContentValues contentValues = new ContentValues();
        Object expectedModel = new Object();
        when(contentAdapter.toModel(contentValues, expectedModel.getClass())).thenReturn(expectedModel);
        ResourceController resourceControllerSpy = spy(resourceController);

        resourceControllerSpy.put(Uri.parse("/a/path/is/a/path"), contentValues);

        verify(resourceControllerSpy).handlePut(Uri.parse("/a/path/is/a/path"), expectedModel);
    }

    @Test
    public void invokeSubclassDeleteHandlerOnDelete() {
        ResourceController resourceControllerSpy = spy(resourceController);

        resourceControllerSpy.delete(Uri.parse("/any/path/will/do"));

        verify(resourceControllerSpy).handleDelete(Uri.parse("/any/path/will/do"));
    }

    private static class ResourceControllerImpl extends ResourceController<Object> {

        public ResourceControllerImpl(ContentAdapter contentAdapter, Class<Object> modelClass) {
            super(contentAdapter, modelClass);
        }

        @Override
        protected Iterable<Object> handleGet(Uri uri) {
            return null;
        }

        @Override
        protected Uri handlePost(Uri uri, Object model) {
            return POST_RESPONSE;
        }

        @Override
        protected int handlePut(Uri uri, Object model) {
            return 0;
        }

        @Override
        protected int handleDelete(Uri uri) {
            return 0;
        }

        @Override
        public int patch(Uri uri, ContentValues[] contentValues) {
            return 0;
        }
    }
}