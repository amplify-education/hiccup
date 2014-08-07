package com.amplify.hiccup.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class HttpContentAdapterTest {

    private HttpContentAdapter httpContentAdapter;

    @Mock
    private JsonConverter jsonConverter;

    @Before
    public void setUp() {
        initMocks(this);

        httpContentAdapter = new HttpContentAdapter(jsonConverter);
    }

    @Test
    public void returnEmptyCursorForNullResult() {
        Cursor cursor = httpContentAdapter.toCursor(null);

        assertThat(cursor.getCount()).isEqualTo(0);
    }

    @Test
    public void convertSingleModelIntoCursorWithSingleRow() {
        Iterable<Object> result = Arrays.asList(new Object());

        Cursor cursor = httpContentAdapter.toCursor(result);

        assertThat(cursor.getCount()).isEqualTo(1);
    }

    @Test
    public void convertMultipleModelsIntoCursorWithMultipleRows() {
        Iterable<Object> result = Arrays.asList(new Object(), new Object(), new Object());

        Cursor cursor = httpContentAdapter.toCursor(result);

        assertThat(cursor.getCount()).isEqualTo(3);
    }

    @Test
    public void convertSingleModelIntoCursorWithBaseColumnId() {
        Iterable<Object> result = Arrays.asList(new Object());

        Cursor cursor = httpContentAdapter.toCursor(result);

        int idColumn = cursor.getColumnIndex(BaseColumns._ID);
        cursor.moveToFirst();
        assertThat(cursor.getLong(idColumn)).isEqualTo(1L);
    }

    @Test
    public void convertMultipleModelsIntoCursorWithIncrementingBaseColumnId() {
        List<Object> result = Arrays.asList(new Object(), new Object(), new Object());

        Cursor cursor = httpContentAdapter.toCursor(result);

        int idColumn = cursor.getColumnIndex(BaseColumns._ID);
        cursor.moveToFirst();
        assertThat(cursor.getLong(idColumn)).isEqualTo(1L);
        cursor.moveToNext();
        assertThat(cursor.getLong(idColumn)).isEqualTo(2L);
        cursor.moveToNext();
        assertThat(cursor.getLong(idColumn)).isEqualTo(3L);
    }

    @Test
    public void convertSingleModelIntoCursorWithJsonBody() {
        String expectedBody = "{\"a_key\" : \"some value\"}";
        Object modelObject = new Object();
        Iterable<Object> result = Arrays.asList(modelObject);
        when(jsonConverter.toJson(modelObject)).thenReturn(expectedBody);

        Cursor cursor = httpContentAdapter.toCursor(result);

        int bodyColumn = cursor.getColumnIndex("body");
        cursor.moveToFirst();
        String actualBody = cursor.getString(bodyColumn);
        assertThat(actualBody).isEqualTo(expectedBody);
    }

    @Test
    public void convertMultipleModelsIntoCursorWithMultipleJsonBody() {
        String expectedJsonOne = "{\"some_key\" : \"a value\"}";
        String expectedJsonTwo = "{\"another_key\" : \"another value!\"}";
        String expectedJsonThree = "{\"to_be_sure\" : \"yet another value\"}";
        Iterable<Object> result = Arrays.asList(new Object(), new Object(), new Object());
        when(jsonConverter.toJson(anyObject())).thenReturn(expectedJsonOne, expectedJsonTwo, expectedJsonThree);

        Cursor cursor = httpContentAdapter.toCursor(result);

        int bodyColumnIndex = cursor.getColumnIndex("body");
        cursor.moveToFirst();
        assertThat(cursor.getString(bodyColumnIndex)).isEqualTo(expectedJsonOne);
        cursor.moveToNext();
        assertThat(cursor.getString(bodyColumnIndex)).isEqualTo(expectedJsonTwo);
        cursor.moveToNext();
        assertThat(cursor.getString(bodyColumnIndex)).isEqualTo(expectedJsonThree);
    }

    @Test
    public void convertHttpBodyToDomainModel() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("body", "some json goes here");
        DomainModelTest expectedModel = new DomainModelTest();
        when(jsonConverter.fromJson("some json goes here", DomainModelTest.class)).thenReturn(expectedModel);

        Object actualModel = httpContentAdapter.toModel(contentValues, DomainModelTest.class);

        assertThat(actualModel).isEqualTo(expectedModel);
    }

    private static class DomainModelTest {
    }
}
