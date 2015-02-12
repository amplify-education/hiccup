package com.amplify.hiccup.client;

import android.content.*;
import android.net.Uri;
import android.os.RemoteException;

import java.util.ArrayList;

public class BatchRequest {

    private final Context context;
    private final RequestAdapter requestAdapter;

    private String authority;
    private ArrayList<ContentProviderOperation> operations;

    BatchRequest(Context context, RequestAdapter requestAdapter) {
        this.context = context;
        this.requestAdapter = requestAdapter;
        this.operations = new ArrayList<ContentProviderOperation>();
    }

    public BatchRequest post(Uri uri, Object model) {
        assertAndSetCommonAuthority(uri);
        ContentValues contentValues = requestAdapter.toValues(model);
        ContentProviderOperation operation = ContentProviderOperation
                .newInsert(uri)
                .withValues(contentValues)
                .build();
        operations.add(operation);
        return this;
    }

    public BatchRequest put(Uri uri, Object model) {
        assertAndSetCommonAuthority(uri);
        ContentValues contentValues = requestAdapter.toValues(model);
        ContentProviderOperation operation = ContentProviderOperation
                .newUpdate(uri)
                .withValues(contentValues)
                .build();
        operations.add(operation);
        return this;
    }

    public BatchRequest delete(Uri uri) {
        assertAndSetCommonAuthority(uri);
        ContentProviderOperation operation = ContentProviderOperation
                .newDelete(uri)
                .build();
        operations.add(operation);
        return this;
    }

    public void submit() {
        if (operations.size() < 1) {
            throw new IllegalStateException("Cannot submit an empty batch");
        }

        ContentResolver resolver = context.getContentResolver();
        try {
            resolver.applyBatch(authority, operations);
        } catch (RemoteException e) {
            throw new BatchFailedException(e);
        } catch (OperationApplicationException e) {
            throw new BatchFailedException(e);
        }
    }

    public int size() {
        return operations.size();
    }

    private void assertAndSetCommonAuthority(Uri uri) {
        String newAuthority = uri.getAuthority();
        if (authority == null) {
            this.authority = newAuthority;
        } else if (!authority.equals(newAuthority)) {
            throw new IllegalArgumentException("Batch requests must specify the same authority");
        }
    }

    public static class BatchFailedException extends RuntimeException {
        BatchFailedException(Throwable throwable) {
            super(throwable);
        }
    }
}
