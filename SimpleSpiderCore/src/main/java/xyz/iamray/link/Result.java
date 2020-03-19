package xyz.iamray.link;

import lombok.Data;
import xyz.iamray.repo.CrawlMes;

import java.util.List;

/**
 * @author liuwenrui
 * @date 2018/11/13
 */
@Data
public class Result<T> {

    private T obj;

    private List<T> objList;

    private CrawlMes crawlMes;

    private Exception e;

    public Result(T obj,CrawlMes crawlMes){
        this.obj = obj;
        this.crawlMes = crawlMes;
    }

    public Result(List<T> objList,CrawlMes crawlMes){
        this.objList = objList;
        this.crawlMes = crawlMes;
    }

    public Result(Exception e){
        this.e = e;
    }
}
