# SimpleSpider
:beetle: 一个轻量级的爬虫框架，亮点就是够轻，够快，够容易上手</br>
###【简介】
这个爬虫框架小到只有是十几个类，可以说就是几个工具类合在一起，主要是将 [jsoup](https://github.com/jhy/jsoup) 和 [fastjson](https://github.com/jhy/jsoup) 的API进行封装，
底层连接用HttpClient,并配置连接池和一个线程池，实现资源的循环利用和多线程爬取，
也可以用BlockingQueue完成异步爬取。框架设置了丰富的API来对连接进行个性化配置。
 -  -  -  
 </br>
###【快速上手】</br>
[列表页]:https://bangumi.bilibili.com/web_api/season/index_global?page_size=20&version=0&is_finish=0&start_year=0&tag_id=&index_type=1&index_sort=0&quarter=0&page=
> 内置样例讲解--爬取B站的番剧
</br>
首先定义自己的Action，Action即抓取动作，这个需要用户根据自己的需求来定义，
sample定义了两个Action类，分别爬取[列表页]和详情页，
列表页请求获取的数据类型是json格式的数据，
所以定义的Action就应该继承AbstractJsonCrawlerAction类，详情页请求获取的是Document，Action继承AbstractDocumentCrawlerAction,
泛型填入你要从Action中返回的数据。
  </br>      
开始启动爬虫，和爬虫有关的API都设置在SimpleSpider类中。
```
SimpleSpider.make().setHeader(SpiderConstant.DefaultHeader).crawlURL(url+pageNum,null).crawl(new bilianimeSpiderAction());
```
这段代码的功能介绍参见下面的SimpleSpider的API
至此爬虫就开始工作了！！
</br>
###【API】
> CrawlerAction<T>  接口

定义抓取动作的，一般用户直接继承他的子类即可，根据链接获取的数据类型，继承不同的Action类，框架内置了三种类型，JSON数据继承AbstractJsonCrawlerAction类，重写JSONCrawl()；
Document这类的文档继承AbstractDocumentCrawlerAction,重写DocumentCrawl(), 图片这类的文件继承AbstractByteCrawlerAction，重写FileCrawl();
泛型T定义为重写方法的返回类型;
</br>
> SimpleSpider 
```
SimpleSpider类对象中设置了一个线程池，线程数为3，所以用户进行小量的爬取时，可以不设置自定义的ThreadPool,内置的线程池不能满足时，用户可以自定义
```

</br>
* make():创建一个SimpleSpider对象；

* setHeader(Map map):设置请求头，可以自定义，需要一个Map对象，SpiderConstant提供了两个Header，一个是伪装成Chrome的，还有一个是暴露为Spider的头部；

* crawlURL(String url,ClosedHTTPClient httpClient):设置爬取得URL和HTTPClient对象，如果需要维持一段会话的爬取，就需要公用一个HTTPClient对象，

所以这里留一个设置HTTPClient的接口，如果不需要维持一段会话，这里可以设置成null，程序会为爬虫分配一个HTTPClient；

* crawlURLS(String[] urls,CloseableHttpClient httpClient):设置一组URL

* setProperty(Property property):设置Property 对象，用来和Action进行信息传递

* setRequestConfig(Integer retryTime,Integer connectTimeout): retryTime:失败重试次数；connectTimeout: 链接超时的时长

* myInitThreadPool(Integer coreNum,Integer maxNum,Long KeepAliveTime):coreNum:核心线程数；maxNum:最大线程数；KeepAliveTime: 当前线程数超出核心线程数时，多余线程的最大存活时间

* async(BlockingQueue queue):让爬虫异步执行，需要传入blockingqueue对象接收返回结果

* crawl(CrawlerAction action):执行爬取，同步时返回结果

* crawlBundle(CrawlerAction action):多个URL时的批量爬取，返回结果的List对象
</br>
###LICENSE
* * * * * 
[Apache 2.0](https://github.com/ScarlettRay/SimpleSpider/blob/master/LICENSE)