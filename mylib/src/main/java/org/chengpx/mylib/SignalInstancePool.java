package org.chengpx.mylib;

import com.google.gson.Gson;

/**
 * create at 2018/4/23 15:54 by chengpx
 */
public class SignalInstancePool {

    private static Gson sGson;

    public static Gson newGson() {
        if (sGson == null) {
            sGson = new Gson();
        }
        return sGson;
    }

}
