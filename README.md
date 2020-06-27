# SimpleSpider
:beetle: 一个轻量级的爬虫框架，亮点就是够轻，够快，够容易上手
### 【简介】
这个爬虫框架小到只有是十几个类，可以说就是几个工具类合在一起，主要是将 [jsoup](https://github.com/jhy/jsoup) 和 [fastjson](https://github.com/jhy/jsoup) 的API进行封装，
底层连接用HttpClient，并配置连接池和一个线程池，实现资源的循环利用和多线程爬取，
也可以用BlockingQueue完成异步爬取。框架设置了丰富的API来对连接进行个性化配置。
 -  -  -  
### 【快速上手】
> 内置案例讲解--爬取知乎壁纸问题（有哪些壁纸是你永远都不想换掉的？）上的图片（当然你可以更换任何一个话题）

首先定义自己的Action，Action即抓取动作，即你的爬虫需要从请求返回的数据中抓取哪些元素，这个需要用户根据自己的需求来定义，
案例定义了两个Action类，分别爬取[列表页](https://bangumi.bilibili.com/web_api/season/index_global?page_size=20&version=0&is_finish=0&start_year=0&tag_id=&index_type=1&index_sort=0&quarter=0&page=)和详情页，
列表页请求获取的数据类型是json格式的数据，
所以定义的Action就应该继承AbstractJsonCrawlerAction类，详情页请求获取的是Document，Action继承AbstractDocumentCrawlerAction,
泛型填入你要从Action中返回的数据。
  </br>      
开始启动爬虫，和爬虫有关的API都设置在SimpleSpider类中。
```
SimpleSpider.make()
            .setHeader(SpiderConstant.DefaultHeader)
            .crawlURL(url+pageNum,null)
            .crawl(new bilianimeSpiderAction());
```
这段代码的功能介绍参见下面的SimpleSpider的API
至此爬虫就开始工作了！！
### 【API】
> #### CrawlerAction<T>  

定义抓取动作的，一般用户直接继承他的子类即可，根据链接获取的资源的数据类型，继承不同的Action类，框架内置了四种资源的Action抽象类:  
AbstractByteCrawlerAction类用于抓取二进制资源，比如图片；  
AbstractCrawlerAction类用于抓取标签文档，如html页面；
AbstractJsonCrawlerAction抓取JSON数据；  
AbstractJsonObjectCrawlerAction抓取JsonObject数据；  
AbstractStringCrawlerAction抓取String数据；
支持用户自定义资源，实现Parser类，在ParserMap里面注册。

泛型T为重写方法的返回类型;
</br>
> #### AbstractSpider </br>
SimpleSpider类对象中设置了一个线程池，线程数为3，所以用户进行小量的爬取时，可以不设置自定义的ThreadPool,内置的线程池不能满足时，用户可以自定义


* setRequestHeader(Map map):设置请求头，可以自定义，需要一个Map对象，SpiderConstant提供了两个Header，一个是伪装成Chrome的，还有一个是暴露为Spider的头部；

* setStartConfiger():此方法有多个重载方法，这些参数共同组装一个爬虫的启动配置项StartConfiger。
url和urls,爬去的url和一组urls;  
crawlerAction,抓取的动作；  
httpClient，维持一段会话；
postBody，post请求传入postBody参数；


* setProperty(String key,Object value):设置爬虫上下文参数，用来和Action进行信息传递，不要使用setProperty(Properties property)

* setRetryTime(Integer retryTime):失败重试次数；  

* setConnectTimeout(Integer connectTimeout): 链接超时的时长；  

* customThreadPool(ExecutorService cumstomizeExecutorService,boolean useCustomThreadPool):传入自定义的线程池，后面一个参数表示是否在抓取中使用者线程池；
* setListenHttpStatus(int httpStatus):设置期待返回结果的状态码，默认是200；

* start():启动爬虫。他还有一个异步爬取的重载方法start(BlockingQueue<T2> blockingQueue)，这个方法需要传入阻塞队列用于存放爬取结果，异步抓取返回结果为null,不需要接收。

> #### AbstractSpider的两个继承类SimpleSpider和PostSpider  

* SimpleSpider用于执行Get请求
* PostSpider用于执行Post请求
### LICENSE
* * * * * 
[Apache 2.0](https://github.com/ScarlettRay/SimpleSpider/blob/master/LICENSE)
