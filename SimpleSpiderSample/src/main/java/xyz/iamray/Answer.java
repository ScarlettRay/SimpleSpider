package xyz.iamray;

import lombok.Data;

import java.util.Date;

/**
 * @author liuwenrui
 * @since 2018/12/16
 */
@Data
public class Answer {

    public String name;

    public Date createdTime;

    public Date updatedTime;

    public boolean isCopyable;//转载许可？？

    public Integer voteupCount;

    public Integer commentCount;

    public String content;

    public String url;
}
