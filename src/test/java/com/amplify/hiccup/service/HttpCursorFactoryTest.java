package com.amplify.hiccup.service;

import android.database.Cursor;
import android.provider.BaseColumns;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class HttpCursorFactoryTest {

    private HttpCursorFactory factory;

    private Object dbModel;
    @Mock
    private JsonConverter jsonConverter;

    @Before
    public void setUp() {
        initMocks(this);
        dbModel = new Object();

        factory = new HttpCursorFactory(jsonConverter);
    }

    @Test
    public void shouldConvertSingleObjectIntoCursorWithBaseColumnId() {
        Cursor cursor = factory.from(dbModel);

        int idColumn = cursor.getColumnIndex(BaseColumns._ID);
        cursor.moveToFirst();
        assertThat(cursor.getLong(idColumn), is(1L));
    }

    @Test
    public void shouldConvertSingleObjectIntoCursorWithJsonBody() {
        String expectedBody = "helllllooooooo";
        when(jsonConverter.toJson(dbModel)).thenReturn(expectedBody);

        Cursor cursor = factory.from(dbModel);

        int bodyColumn = cursor.getColumnIndex("body");
        cursor.moveToFirst();
        String actualBody = cursor.getString(bodyColumn);
        assertThat(actualBody, is(expectedBody));
    }
}
