package xyz.iamray.action;


import xyz.iamray.repo.CrawlMes;

import java.util.Properties;

/**
 *
 * @param <T1> A parameter of method {@code crawl()}
 * @param <T2> Result type of the mathod {@code crawl()}
 */
public interface CrawlerAction<T1,T2> {

    /**
     * Crawl action defined here
     * @param t
     * @return
     */
    T2 crawl(T1 t, CrawlMes crawlMes);

    /**
     * <p>Outer program can pass params to spider with this method.
     * <p></p>So you can get params in your spider's actions through the method.
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    <T>T getAttr(String key, Class<T> clazz);

    void setProperty(Properties property);
}
