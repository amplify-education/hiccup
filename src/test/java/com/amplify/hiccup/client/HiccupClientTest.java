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
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class HiccupClientTest {

    private HiccupClient hiccupClient;

    @Mock
    private Context context;
    @Mock
    private ContentResolver contentResolver;

    private Uri uri;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        uri = Uri.parse("content://com.fake.authority/some/fake/path");
        given(context.getContentResolver()).willReturn(contentResolver);

        hiccupClient = new HiccupClient(context);
    }

    @Test
    public void queryUriOnGetRequest() {
        Cursor expectedCursor = mock(Cursor.class);
        when(contentResolver.query(uri, null, null, null, null)).thenReturn(expectedCursor);

        Cursor actualCursor = hiccupClient.get(uri);

        assertThat(actualCursor).isEqualTo(expectedCursor);
    }

    @Test
    public void insertContentValuesOnPostRequest() {
        ContentValues expectedContentValues = new ContentValues();

        hiccupClient.post(uri, expectedContentValues);

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
    public void updateContentValuesOnPutRequest() {
        ContentValues expectedContentValues = new ContentValues();
        expectedContentValues.put("test", "value");

        hiccupClient.put(uri, expectedContentValues);

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

    @Test
    public void deleteUriForDeleteRequest() {
        hiccupClient.delete(uri);

        verify(contentResolver).delete(eq(uri), eq((String) null), eq((String[]) null));
    }

    @Test
    public void returnDeletedRowsFromDeleteRequest() {
        int expectedCount = 456;
        when(contentResolver.delete(eq(uri), eq((String) null), eq((String[]) null))).thenReturn(expectedCount);

        int actualCount = hiccupClient.delete(uri);

        assertThat(actualCount).isEqualTo(expectedCount);
    }

    @Test
    public void patchUriForPatchRequest() {
        ContentValues[] values = new ContentValues[] {
                new ContentValues(),
                new ContentValues()
        };

        hiccupClient.patch(uri, values);

        verify(contentResolver).bulkInsert(eq(uri), eq(values));
    }

    @Test
    public void returnSuccessfulOperationCountFromPatchRequest() {
        int expectedCount = 456;
        ContentValues[] values = new ContentValues[] {
                new ContentValues(),
                new ContentValues()
        };
        when(contentResolver.bulkInsert(eq(uri), eq(values))).thenReturn(expectedCount);

        int actualCount = hiccupClient.patch(uri, values);

        assertThat(actualCount).isEqualTo(expectedCount);
    }

    @Test
    public void createNewBatch() {
        BatchRequest batch = hiccupClient.newBatch();

        assertThat(batch).isNotNull();
    }
}
