package com.im.common.current;

import com.google.common.util.concurrent.*;
import com.im.common.util.ThreadUtil;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class CallbackTaskScheduler {
    //方法二是使用自建的线程池时，专用于处理耗时操作
    static ListeningExecutorService guavaPool = null;

    static {
        ExecutorService jPool = ThreadUtil.getMixedTargetThreadPool();
        guavaPool = MoreExecutors.listeningDecorator(jPool);
    }


    private CallbackTaskScheduler() {
    }

    /**
     * 添加任务
     */
    public static <R> void add(CallbackTask<R> executeTask) {

        ListenableFuture<R> future = guavaPool.submit(new Callable<R>() {
            public R call() throws Exception {
                return executeTask.execute();
            }
        });

        Futures.addCallback(future, new FutureCallback<R>() {
            public void onSuccess(R r) {
                executeTask.onBack(r);
            }

            public void onFailure(Throwable t) {
                executeTask.onException(t);
            }
        });

    }

}