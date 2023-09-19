package com.im.common.current;

import com.im.common.util.ThreadUtil;

import java.util.concurrent.ThreadPoolExecutor;

public class FutureTaskScheduler {
    //方法二是使用自建的线程池时，专用于处理耗时操作
    static ThreadPoolExecutor mixPool;

    static {
        mixPool = ThreadUtil.getMixedTargetThreadPool();
    }

    private static FutureTaskScheduler inst = new FutureTaskScheduler();

    private FutureTaskScheduler() {

    }

    /**
     * 添加任务
     */
    public static void add(ExecuteTask executeTask) {
        mixPool.submit(executeTask::execute);
    }


}