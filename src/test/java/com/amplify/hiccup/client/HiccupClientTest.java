package com.amplify.hiccup.client;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class HiccupClientTest {

    private HiccupClient hiccupClient;

    @Mock
    private Context context;
    @Mock
    private ContentResolver contentResolver;
    @Mock
    private RequestAdapter requestAdapter;

    private Uri uri;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        uri = Uri.parse("content://com.fake.authority/some/fake/path");
        given(context.getContentResolver()).willReturn(contentResolver);

        hiccupClient = new HiccupClient(context, requestAdapter);
    }

    @Test
    public void queryUriOnGetRequest() {
        Cursor expectedCursor = mock(Cursor.class);
        when(contentResolver.query(uri, null, null, null, null)).thenReturn(expectedCursor);

        Cursor actualCursor = hiccupClient.get(uri);

        assertThat(actualCursor).isEqualTo(expectedCursor);
    }

    @Test
    public void insertContentValuesFromDomainModelOnPostRequest() {
        Object model = new Object();
        ContentValues expectedContentValues = new ContentValues();
        when(requestAdapter.toValues(model)).thenReturn(expectedContentValues);

        hiccupClient.post(uri, model);

        ArgumentCaptor<ContentValues> captor = ArgumentCaptor.forClass(ContentValues.class);
        verify(contentResolver).insert(eq(uri), captor.capture());
        ContentValues actualContentValues = captor.getValue();
        assertThat(actualContentValues).isEqualTo(expectedContentValues);
    }

    @Test
    public void returnUriFromPostRequest() {
        Uri expectedUri = mock(Uri.class);
        when(contentResolver.insert(eq(uri), any(ContentValues.class))).thenReturn(expectedUri);

        Uri actualUri = hiccupClient.post(uri, null);

        assertThat(actualUri).isEqualTo(expectedUri);
    }

    @Test
    public void updateContentValuesFromDomainModelOnPutRequest() {
        Object model = new Object();
        ContentValues expectedContentValues = new ContentValues();
        expectedContentValues.put("test", "value");
        when(requestAdapter.toValues(model)).thenReturn(expectedContentValues);

        hiccupClient.put(uri, model);

        ArgumentCaptor<ContentValues> captor = ArgumentCaptor.forClass(ContentValues.class);
        verify(contentResolver).update(eq(uri), captor.capture(), eq((String) null), eq((String[]) null));
        ContentValues actualContentValues = captor.getValue();
        assertThat(actualContentValues).isEqualTo(expectedContentValues);
    }

    @Test
    public void returnUpdatedRowsFromPutRequest() {
        int expectedCount = 123;
        when(contentResolver.update(eq(uri), any(ContentValues.class), eq((String) null), eq((String[]) null))).thenReturn(expectedCount);

        int actualCount = hiccupClient.put(uri, null);

        assertThat(actualCount).isEqualTo(expectedCount);
    }

}
