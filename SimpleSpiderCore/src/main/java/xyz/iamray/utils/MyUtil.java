package iamray.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author liuwenrui
 * @date 2018/5/7
 */
public class MyUtil {

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
}
