package com.fm.commons.service;

/**
 * Created by admin on 2016/10/20.
 */

public class ParamsBody {
    Integer id;
    String method;
    Object params;

    public ParamsBody(String method, Object params) {
        id = 1;
        this.method = method;
        this.params = params;
    }
}
