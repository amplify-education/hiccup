package com.amplify.hiccup.client;

import android.content.ContentValues;

import com.amplify.hiccup.shared.JsonConverter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class HttpRequestAdapterTest {

    private HttpRequestAdapter httpRequestAdapter;

    @Mock
    private JsonConverter jsonConverter;

    @Before
    public void setUp() {
        initMocks(this);

        httpRequestAdapter = new HttpRequestAdapter(jsonConverter);
    }

    @Test
    public void addBodyToContentValues() {
        Object model = new Object();
        String expectedBody = "Lois, this is not my Batman glass.";
        when(jsonConverter.toJson(model)).thenReturn(expectedBody);

        ContentValues actualContentValues = httpRequestAdapter.toValues(model);

        assertThat(actualContentValues).isNotNull();
        assertThat(actualContentValues.getAsString("body")).isEqualTo(expectedBody);
    }

}