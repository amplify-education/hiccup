package com.amplify.hiccup.client;

import android.content.*;
import android.net.Uri;
import android.os.RemoteException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class BatchRequestTest {

    private static final String COM_FAKE_AUTHORITY = "com.fake.authority";

    private BatchRequest batchRequest;

    @Mock
    private Context context;
    @Mock
    private ContentResolver contentResolver;
    @Mock
    private ContentProvider contentProvider;
    @Captor
    private ArgumentCaptor<ArrayList<ContentProviderOperation>> operationListCaptor;

    private Uri uri;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        uri = Uri.parse("content://" + COM_FAKE_AUTHORITY + "/some/fake/path");
        given(context.getContentResolver()).willReturn(contentResolver);
        given(contentProvider.insert(any(Uri.class), any(ContentValues.class))).willReturn(uri);

        batchRequest = new BatchRequest(context);
    }

    @Test
    public void newBatchRequestsAreEmpty() {
        assertThat(batchRequest.size()).isEqualTo(0);
    }

    @Test(expected = IllegalStateException.class)
    public void cannotSubmitEmptyBatch() {
        batchRequest.submit();
    }

    @Test(expected = IllegalArgumentException.class)
    public void authoritiesMustBeTheSameForAllRequests() {
        Uri authorityOne = Uri.parse("content://com.abstract/path/to/file");
        Uri authorityTwo = Uri.parse("content://com.dragon/path/to/file");

        batchRequest
                .post(authorityOne, new ContentValues())
                .put(authorityTwo, new ContentValues());
    }

    @Test
    public void requestsAreNotSentIfBatchNotSubmitted() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("some", "thing");

        batchRequest
                .post(uri, contentValues)
                .put(uri, contentValues)
                .delete(uri);

        verifyZeroInteractions(contentResolver);
    }

    @Test
    public void multipleRequestsCanBeAddedToBatch() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("any", "value");

        batchRequest
                .post(uri, contentValues)
                .put(uri, contentValues)
                .delete(uri);

        assertThat(batchRequest.size()).isEqualTo(3);
    }

    @Test
    public void submitPostRequestFromBatch() throws RemoteException, OperationApplicationException {
        ContentValues expectedContentValues = new ContentValues();
        expectedContentValues.put("yes", "no");

        batchRequest
                .post(uri, expectedContentValues)
                .submit();

        simulateApplyContentProviderOperations();
        verify(contentProvider).insert(uri, expectedContentValues);
    }

    @Test
    public void submitPutRequestFromBatch() throws RemoteException, OperationApplicationException {
        ContentValues expectedContentValues = new ContentValues();
        expectedContentValues.put("blues", "brothers");

        batchRequest
                .put(uri, expectedContentValues)
                .submit();

        simulateApplyContentProviderOperations();
        verify(contentProvider).update(uri, expectedContentValues, null, null);
    }

    @Test
    public void submitDeleteRequestFromBatch() throws RemoteException, OperationApplicationException {
        batchRequest
                .delete(uri)
                .submit();

        simulateApplyContentProviderOperations();
        verify(contentProvider).delete(uri, null, null);
    }

    @Test
    public void multipleRequestsAreAppliedInOrderAdded() throws RemoteException, OperationApplicationException {
        ContentValues contentValues = new ContentValues();
        contentValues.put("any", "value");

        batchRequest
                .post(uri, contentValues)
                .put(uri, contentValues)
                .delete(uri)
                .submit();

        simulateApplyContentProviderOperations();
        InOrder inOrder = inOrder(contentProvider);
        inOrder.verify(contentProvider).insert(any(Uri.class), any(ContentValues.class));
        inOrder.verify(contentProvider).update(any(Uri.class), any(ContentValues.class), anyString(), any(String[].class));
        inOrder.verify(contentProvider).delete(any(Uri.class), anyString(), any(String[].class));
    }

    @Test(expected = BatchRequest.BatchFailedException.class)
    public void batchExceptionIsThrownIfRemoteExceptionOccurs() throws RemoteException, OperationApplicationException {
        when(contentResolver.applyBatch(anyString(), any(ArrayList.class))).thenThrow(new RemoteException());

        batchRequest
                .delete(uri)
                .submit();
    }

    @Test(expected = BatchRequest.BatchFailedException.class)
    public void batchExceptionIsThrownIfOperationApplicationExceptionOccurs() throws RemoteException, OperationApplicationException {
        when(contentResolver.applyBatch(anyString(), any(ArrayList.class))).thenThrow(new OperationApplicationException());

        batchRequest
                .delete(uri)
                .submit();
    }

    private void simulateApplyContentProviderOperations() {
        try {
            verify(contentResolver).applyBatch(anyString(), operationListCaptor.capture());
            ArrayList<ContentProviderOperation> operations = operationListCaptor.getValue();
            for (ContentProviderOperation operation : operations) {
                operation.apply(contentProvider, null, 0);
            }
        } catch (RemoteException e) {
            throw new RuntimeException("Simulate apply batch failed!", e);
        } catch (OperationApplicationException e) {
            throw new RuntimeException("Simulate apply batch failed!", e);
        }
    }

}
