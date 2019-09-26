package com.qunar.superoa.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: xing.zhou
 * @Despriction:
 * @Date:Created in 22:10 2018/11/11
 * @Modify by:
 */
public class ThreadPool {

  private static ExecutorService executorService;

  public static ExecutorService createThreadPool() {
    if (executorService == null) {
      executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
          60L, TimeUnit.SECONDS,
          new SynchronousQueue<Runnable>());
      return executorService;
    } else {
      return executorService;
    }
  }
}
