package xyz.iamray.link.parser;

import xyz.iamray.link.SpiderUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * <>
 *     Storing parses in a map whose key is parser`s arguments
 * </>
 * @author liuwenrui
 * @date 2018/11/5
 */
public class ParserMap {

    public static final ParserMap parserMap = new ParserMap();

    private Map<String,Parser> map = new HashMap<>();

    private ParserMap(){
        map.put(SpiderUtil.getClassArguments(ByteParser.class)[0],new ByteParser());
        map.put(SpiderUtil.getClassArguments(DocumentParser.class)[0],new DocumentParser());
        map.put(SpiderUtil.getClassArguments(JsonParser.class)[0],new JsonParser());
        map.put(SpiderUtil.getClassArguments(StringParser.class)[0],new StringParser());
    }

    public boolean contains(String clazz){
        return map.containsKey(clazz);
    }

    public Parser get(String clazz){
        return map.get(clazz);
    }

    public Parser put(String clazz,Parser parser){
       return map.put(clazz,parser);
    }
}
