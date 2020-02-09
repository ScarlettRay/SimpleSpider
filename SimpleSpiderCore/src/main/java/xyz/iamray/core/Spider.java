package xyz.iamray.core;

import xyz.iamray.link.Result;

import java.util.concurrent.BlockingQueue;

/**
 * @author liuwenrui
 * @date 2018/11/3
 */
public interface Spider {

    /**
     * 爬虫启动方法
     * @param <T1>
     * @param <T2>
     * @return
     */
    <T1,T2> Result<T2> start();

    /**
     * 一步爬取的爬虫启动方法
     * @param blockingQueue
     * @param <T1>
     * @param <T2>
     * @return
     */
    <T1,T2> Result<T2> start(BlockingQueue<T2> blockingQueue);

}
