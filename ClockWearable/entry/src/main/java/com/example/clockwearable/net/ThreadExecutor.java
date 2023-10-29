package com.example.clockwearable.net;

import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;

public class ThreadExecutor {
    //切换任务到主线程执行
    public static void runUI(Runnable runnable) {
        EventRunner eventRunner = EventRunner.getMainEventRunner();//切换到主线程
        EventHandler eventHandler = new EventHandler(eventRunner);
        eventHandler.postSyncTask(runnable);//执行任务
    }

    //在子线程执行任务
    public static void runBG(Runnable runnable) {
        EventRunner eventRunner = EventRunner.create(true);//开启一个新的线程
        EventHandler eventHandler = new EventHandler(eventRunner);
        eventHandler.postTask(runnable, 0, EventHandler.Priority.IMMEDIATE);//执行任务
    }
}
