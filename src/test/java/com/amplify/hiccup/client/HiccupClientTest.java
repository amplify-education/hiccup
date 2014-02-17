package com.amplify.hiccup.client;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class HiccupClientTest {

    private HiccupClient hiccupClient;

    @Mock
    private Context context;
    @Mock
    private ContentResolver contentResolver;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        given(context.getContentResolver()).willReturn(contentResolver);

        hiccupClient = new HiccupClient(context);
    }

    @Test
    public void shouldSendUriRequestViaContentResolver() {
        Uri uri = mock(Uri.class);
        Cursor expectedCursor = mock(Cursor.class);
        when(contentResolver.query(uri, null, null, null, null)).thenReturn(expectedCursor);

        Cursor actualCursor = hiccupClient.get(uri);

        assertThat(actualCursor, is(expectedCursor));
    }
}
