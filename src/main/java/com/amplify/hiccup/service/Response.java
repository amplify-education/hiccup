package com.amplify.hiccup.service;

public interface Response<M> {
    Iterable<M> getResults();
    String getBody(M model);
}
