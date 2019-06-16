package cn.edu.zjut.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThreadPool {
    private static Executor service;
    private static ThreadPool pool;
    public static ThreadPool getThreadPool(){
        return pool;
    }
    private ThreadPool(){
        service = Executors.newCachedThreadPool();
    }
    public void excute(Runnable thread)
    {
        service.execute(thread);
    }
}
