package xyz.iamray.bilianimespider.bean;

import xyz.iamray.bilianimespider.Cell;

import java.util.Date;
import java.util.List;

/**
 * Created by liuwenrui on 2018/5/5
 */
public class Anime {

    @Cell("标题")
    private String title;
    @Cell("追番人数")
    private Integer favorites;//追番人数
    @Cell("完结")
    private Integer is_finish;//是否完结
    @Cell("更新到")
    private Integer newest_index;//更新到第几话
    @Cell("付费抢先")
    private String badge;//是否付费抢先
    @Cell("发布时间")
    private Date pub_time;//
    @Cell("最近更新")
    private Date update_time;
    @Cell("全几话")
    private Integer totalcount;//全几话
    @Cell("seasonID")
    private Integer season_id;//id
    @Cell("URL")
    private String url;
    @Cell("每周几更新")
    private Integer week;//??更新时间
    @Cell("封面URL")
    private String cover;//封面
    @Cell("标签")
    private List<String> tagList;//b标签
    @Cell("描述")
    private String content;//
    @Cell("播放次数")
    private Integer playnum;
    @Cell("弹幕总数")
    private Integer danmu;// 弹幕数
    @Cell("评分")
    private Float score;
    @Cell("评分人数")
    private Integer scorecount;
    @Cell("地区")
    private List<String> area;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getFavorites() {
        return favorites;
    }

    public void setFavorites(Integer favorites) {
        this.favorites = favorites;
    }

    public Integer getIs_finish() {
        return is_finish;
    }

    public void setIs_finish(Integer is_finish) {
        this.is_finish = is_finish;
    }

    public Integer getNewest_index() {
        return newest_index;
    }

    public void setNewest_index(Integer newest_index) {
        this.newest_index = newest_index;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public Date getPub_time() {
        return pub_time;
    }

    public void setPub_time(Date pub_time) {
        this.pub_time = pub_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Integer getTotalcount() {
        return totalcount;
    }

    public void setTotalcount(Integer totalcount) {
        this.totalcount = totalcount;
    }

    public Integer getSeason_id() {
        return season_id;
    }

    public void setSeason_id(Integer season_id) {
        this.season_id = season_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getPlaynum() {
        return playnum;
    }

    public void setPlaynum(Integer playnum) {
        this.playnum = playnum;
    }

    public Integer getDanmu() {
        return danmu;
    }

    public void setDanmu(Integer danmu) {
        this.danmu = danmu;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public Integer getScorecount() {
        return scorecount;
    }

    public void setScorecount(Integer scorecount) {
        this.scorecount = scorecount;
    }

    public List<String> getArea() {
        return area;
    }

    public void setArea(List<String> area) {
        this.area = area;
    }

    public Anime(String title, Integer favorites, Integer is_finish, Integer newest_index, String badge, Date pub_time, Date update_time, Integer totalcount, Integer season_id, String url, Integer week, String cover) {
        this.title = title;
        this.favorites = favorites;
        this.is_finish = is_finish;
        this.newest_index = newest_index;
        this.badge = badge;
        this.pub_time = pub_time;
        this.update_time = update_time;
        this.totalcount = totalcount;
        this.season_id = season_id;
        this.url = url;
        this.week = week;
        this.cover = cover;
    }

    @Override
    public String toString() {
        return "Anime{" +
                "title='" + title + '\'' +
                ", favorites=" + favorites +
                ", is_finish=" + is_finish +
                ", newest_index=" + newest_index +
                ", badge='" + badge + '\'' +
                ", pub_time=" + pub_time +
                ", update_time=" + update_time +
                ", totalcount=" + totalcount +
                ", season_id=" + season_id +
                ", url='" + url + '\'' +
                ", week=" + week +
                ", cover='" + cover + '\'' +
                ", tagList=" + tagList +
                ", content='" + content + '\'' +
                ", playnum=" + playnum +
                ", danmu=" + danmu +
                ", score=" + score +
                ", scorecount=" + scorecount +
                ", area=" + area +
                '}';
    }
}
