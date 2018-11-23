package xyz.iamray.link;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

/**
 * @author liuwenrui
 * @date 2018/5/7
 */
public class SpiderUtil {

    /**
     * 获取对象的类上的泛型，并判断是否为Collection或者是Map的子类
     * @param object
     * @return
     */
    public static boolean isArgumentsCollectionInSuperClass(Object object, int index){
        Type[] genericType = ((ParameterizedType)object.getClass().getGenericSuperclass()).getActualTypeArguments();
        try {
            String fullTypeName = genericType[index].getTypeName();
            if(fullTypeName.contains("<")){
                fullTypeName = fullTypeName.substring(0,fullTypeName.indexOf("<"));
            }
            return Collection.class.isAssignableFrom(Class.forName(fullTypeName))
                    || Map.class.isAssignableFrom(Class.forName(fullTypeName));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static String[] getClassArguments(Class clazz){
        Type[] genericType = ((ParameterizedType)clazz.getGenericSuperclass()).getActualTypeArguments();
        String[] types = new String[genericType.length];
        for (int i = 0; i < genericType.length; i++){
            types[i] = genericType[i].getTypeName();
        }
        return types;
    }

    /**
     * 只获取第一个接口的泛型
     * @param clazz
     * @return
     */
    public static String[] getClassArgumentsFromInterface(Class clazz){
        Type[] genericType = ((ParameterizedType)clazz.getGenericInterfaces()[0]).getActualTypeArguments();
        String[] types = new String[genericType.length];
        for (int i = 0; i < genericType.length; i++){
            types[i] = genericType[i].getTypeName();
        }
        return types;
    }

    public static Class[] getClass(Class clazz){
        Type[] genericType = ((ParameterizedType)clazz.getGenericSuperclass()).getActualTypeArguments();
        Class[] types = new Class[genericType.length];
        for (int i = 0; i < genericType.length; i++) {
            if(genericType[i] instanceof ParameterizedTypeImpl){
                types[i] = ((ParameterizedTypeImpl) genericType[i]).getRawType();
            }else if(genericType[i] instanceof Class){
                types[i] = (Class) genericType[i];
            }
        }
        return types;
    }


}
