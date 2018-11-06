package xyz.iamray.link;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author liuwenrui
 * @date 2018/5/7
 */
public class SpiderUtil {

    /**
     * 获取对象的类上的泛型，并判断是否为Collection的子类
     * @param object
     * @return
     */
    public static boolean isArgumentsCollection(Object object){
        Type[] genericType = ((ParameterizedType)object.getClass().getGenericSuperclass()).getActualTypeArguments();
        try {
            return Collection.class.isAssignableFrom(Class.forName(genericType[0].getTypeName()));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String[] getClassArguments(Class clazz){
        Type[] genericType = ((ParameterizedType)clazz.getGenericSuperclass()).getActualTypeArguments();
        String[] types = new String[genericType.length];
        for (int i = 0; i < genericType.length-1; i++) {
            types[i] = genericType[i].getTypeName();
        }
        return types;
    }

    public static Class[] getClass(Class clazz){
        Type[] genericType = ((ParameterizedType)clazz.getGenericSuperclass()).getActualTypeArguments();
        Class[] types = new Class[genericType.length];
        for (int i = 0; i < genericType.length-1; i++) {
            types[i] = genericType[i].getClass();
        }
        return types;
    }
}
