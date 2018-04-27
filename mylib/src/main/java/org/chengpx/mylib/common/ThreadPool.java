package org.chengpx.mylib.common;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * create at 2018/4/26 16:03 by chengpx
 */
public class ThreadPool {

    private static ThreadPool sThreadPool = new ThreadPool();

    private final ThreadPoolExecutor mThreadPoolExecutor;

    private ThreadPool() {
        mThreadPoolExecutor = new ThreadPoolExecutor(5, 10, 200,
                TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(10));
    }

    public static ThreadPool getInstance() {
        return sThreadPool;
    }

    public void execute(Runnable runnable) {
        if (runnable != null) {
            mThreadPoolExecutor.execute(runnable);
        }
    }

}
