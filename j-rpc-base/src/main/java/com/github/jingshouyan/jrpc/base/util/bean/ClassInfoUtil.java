package com.github.jingshouyan.jrpc.base.util.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.github.jingshouyan.jrpc.base.bean.ClassInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author jingshouyan
 * #date 2018/10/24 17:49
 */
public class ClassInfoUtil {

    public static ClassInfo getClassInfo(Type type, int deep){
        return getClassInfo(type,TypeBindings.emptyBindings(),deep);
    }

    private static ClassInfo getClassInfo(Type type, TypeBindings typeBindings, int deep){
        ClassInfo classInfo = new ClassInfo();
        JavaType javaType = JsonUtil.getJavaType(type,typeBindings);
        classInfo.setJavaType(javaType);
        Class<?> clazz = javaType.getRawClass();
        classInfo.setType(clazz.getSimpleName());
        classInfo.setDeep(deep);
        if(deep > 0 && !isSimpleType(clazz)) {
            if(isShowGenerics(clazz)){
                List<JavaType> generics = javaType.getBindings().getTypeParameters();
                for (JavaType g : generics) {
                    ClassInfo gc = getClassInfo(g,TypeBindings.emptyBindings(),deep);
                    classInfo.getGenerics().add(gc);
                }
            }
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields){
                int mod = field.getModifiers();
                //静态属性
                if (Modifier.isStatic(mod)) {
                    continue;
                }
                //排除添加 @Ignore 的属性
                if (field.isAnnotationPresent(JsonIgnore.class)) {
                    continue;
                }
                ClassInfo c = getClassInfo(field.getGenericType(),javaType.getBindings(),deep -1);
                c.setName(field.getName());
                Annotation[] annotations = field.getAnnotations();
                for (Annotation annotation : annotations){
                    c.getAnnotations().add(annotation.annotationType().getSimpleName());
                }
                classInfo.getFields().add(c);
            }
        }

        return classInfo;
    }

    private static boolean isShowGenerics(Class<?> clazz){
        if(Collection.class.isAssignableFrom(clazz)){
            return true;
        }
        if(Map.class.isAssignableFrom(clazz)){
            return true;
        }
        return false;
    }

    private static boolean isSimpleType(Class<?> clazz){
        if(
                clazz == byte.class || clazz == Byte.class
                ||clazz == short.class || clazz == Short.class
                ||clazz == int.class || clazz == Integer.class
                ||clazz == long.class || clazz == Long.class
                ||clazz == float.class || clazz == Float.class
                ||clazz == double.class || clazz == Double.class
                ||clazz == char.class || clazz == Character.class
                ||clazz == boolean.class || clazz == Boolean.class
                ||clazz == String.class
                ||clazz == Object.class
                ||clazz == Void.class
            ){
            return true;
        }
        return false;
    }
}
