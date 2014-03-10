package com.amplify.hiccup.service;

import android.database.Cursor;
import android.provider.BaseColumns;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class HttpCursorFactoryTest {

    private HttpCursorFactory factory;

    @Mock
    private Response response;

    @Before
    public void setUp() {
        initMocks(this);

        factory = new HttpCursorFactory();
    }

    @Test
    public void shouldReturnEmptyCursorForNullResponse() {
        Cursor cursor = factory.createCursor(null);

        assertThat(cursor.getCount(), is(0));
    }

    @Test
    public void shouldConvertSingleModelIntoCursorWithSingleRow() {
        when(response.getResults()).thenReturn(Arrays.asList(new Object()));

        Cursor cursor = factory.createCursor(response);

        assertThat(cursor.getCount(), is(1));
    }

    @Test
    public void shouldConvertMultipleModelsIntoCursorWithMultipleRows() {
        Iterable<Object> modelObjects = Arrays.asList(new Object(), new Object(), new Object());
        when(response.getResults()).thenReturn(modelObjects);

        Cursor cursor = factory.createCursor(response);

        assertThat(cursor.getCount(), is(3));
    }

    @Test
    public void shouldConvertSingleModelIntoCursorWithBaseColumnId() {
        when(response.getResults()).thenReturn(Arrays.asList(new Object()));

        Cursor cursor = factory.createCursor(response);

        int idColumn = cursor.getColumnIndex(BaseColumns._ID);
        cursor.moveToFirst();
        assertThat(cursor.getLong(idColumn), is(1L));
    }

    @Test
    public void shouldConvertMultipleModelsIntoCursorWithIncrementingBaseColumnId() {
        List<Object> modelObjects = Arrays.asList(new Object(), new Object(), new Object());
        when(response.getResults()).thenReturn(modelObjects);
        Cursor cursor = factory.createCursor(response);

        int idColumn = cursor.getColumnIndex(BaseColumns._ID);
        cursor.moveToFirst();
        assertThat(cursor.getLong(idColumn), is(1L));
        cursor.moveToNext();
        assertThat(cursor.getLong(idColumn), is(2L));
        cursor.moveToNext();
        assertThat(cursor.getLong(idColumn), is(3L));
    }

    @Test
    public void shouldConvertSingleModelIntoCursorWithJsonBody() {
        String expectedBody = "{\"aKey\" : \"some value\"}";
        Object modelObject = new Object();
        when(response.getResults()).thenReturn(Arrays.asList(modelObject));
        when(response.getBody(modelObject)).thenReturn(expectedBody);

        Cursor cursor = factory.createCursor(response);

        int bodyColumn = cursor.getColumnIndex("body");
        cursor.moveToFirst();
        String actualBody = cursor.getString(bodyColumn);
        assertThat(actualBody, is(expectedBody));
    }

    @Test
    public void shouldConvertMultipleModelsIntoCursorWithMultipleJsonBody() {
        String expectedJsonOne = "{\"someKey\" : \"a value\"}";
        String expectedJsonTwo = "{\"anotherKey\" : \"another value!\"}";
        String expectedJsonThree = "{\"toBeSure\" : \"yet another value\"}";
        List<Object> modelObjects = Arrays.asList(new Object(), new Object(), new Object());
        when(response.getResults()).thenReturn(modelObjects);
        when(response.getBody(anyObject())).thenReturn(expectedJsonOne, expectedJsonTwo, expectedJsonThree);

        Cursor cursor = factory.createCursor(response);

        int bodyColumn = cursor.getColumnIndex("body");
        cursor.moveToFirst();
        assertThat(cursor.getString(bodyColumn), is(expectedJsonOne));
        cursor.moveToNext();
        assertThat(cursor.getString(bodyColumn), is(expectedJsonTwo));
        cursor.moveToNext();
        assertThat(cursor.getString(bodyColumn), is(expectedJsonThree));
    }

}
