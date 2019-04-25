package com.github.jingshouyan.jrpc.base.util.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.github.jingshouyan.jrpc.base.bean.BeanInfo;
import com.github.jingshouyan.jrpc.base.bean.ClassInfo;
import com.github.jingshouyan.jrpc.base.bean.FieldInfo;
import com.github.jingshouyan.jrpc.base.bean.TypeInfo;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.google.common.collect.Lists;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
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

    public static BeanInfo beanInfo(Type type) {
        BeanInfo beanInfo = new BeanInfo();
        JavaType javaType = JsonUtil.getJavaType(type,TypeBindings.emptyBindings());
        String rootType = ClassInfoUtil.getTypeName(javaType).toString();
        List<TypeInfo> types = ClassInfoUtil.types(type);
        beanInfo.setRootType(rootType);
        beanInfo.setTypes(types);
        return beanInfo;
    }

    private static ClassInfo getClassInfo(Type type, TypeBindings typeBindings, int deep){
        ClassInfo classInfo = new ClassInfo();
        JavaType javaType = JsonUtil.getJavaType(type,typeBindings);
        classInfo.setJavaType(javaType);
        Class<?> clazz = javaType.getRawClass();
        classInfo.setType(clazz.getSimpleName());
        classInfo.setDeep(deep);
        if(deep > 0 && !isSimpleType(clazz)) {
            if(isCollectionOrMap(clazz)){
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
                    c.getAnnotations().add(annotation.toString());
                }
                classInfo.getFields().add(c);
            }
        }

        return classInfo;
    }

    private static boolean isCollectionOrMap(Class<?> clazz){
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


    public static StringBuilder getTypeName(JavaType javaType) {
        StringBuilder sb = new StringBuilder();
        sb.append(javaType.getRawClass().getSimpleName());
        if(javaType.hasGenericTypes()){
            sb.append('<');
            List<JavaType> generics = javaType.getBindings().getTypeParameters();
            for (JavaType j : generics){
                sb.append(getTypeName(j));
                sb.append(',');
            }
            sb.deleteCharAt(sb.length()-1);
            sb.append('>');
        }
        return sb;
    }

    private static void types(JavaType javaType, List<TypeInfo> typeInfos) {
        String typeName = getTypeName(javaType).toString();
        boolean has = typeInfos.stream().anyMatch( typeInfo -> typeName.equals(typeInfo.getType()));
        if(has){
            return;
        }
        List<JavaType> generics = javaType.getBindings().getTypeParameters();
        for (JavaType g : generics) {
            types(g,typeInfos);
        }

        Class clazz = javaType.getRawClass();
        TypeInfo typeInfo = new TypeInfo();
        typeInfo.setType(typeName);
        if(isSimpleType(clazz)){
            return;
        }
        if(isCollectionOrMap(clazz)){
            return;
        }
        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation: annotations) {
            typeInfo.getAnnotations().add(annotation.toString());
        }
        List<FieldInfo>fieldInfos = Lists.newArrayList();
        JavaType temp = javaType;
        while (temp!=null && !temp.isJavaLangObject()){
            Field[] fields = temp.getRawClass().getDeclaredFields();
            for (Field field :fields){
                int mod = field.getModifiers();
                if(Modifier.isStatic(mod)){
                    continue;
                }
                if(field.isAnnotationPresent(JsonIgnore.class)){
                    continue;
                }
                FieldInfo fieldInfo = new FieldInfo();
                fieldInfo.setName(field.getName());

                Annotation[] fieldAnnotations = field.getAnnotations();
                for (Annotation annotation: fieldAnnotations) {
                    fieldInfo.getAnnotations().add(annotation.toString());
                }

                JavaType jtype = JsonUtil.getJavaType(field.getGenericType(),temp.getBindings());

                types(jtype,typeInfos);
                fieldInfo.setType(getTypeName(jtype).toString());
                fieldInfos.add(fieldInfo);
            }
            temp = temp.getSuperClass();
        }
        typeInfo.setFields(fieldInfos);
        typeInfos.add(typeInfo);
    }

    public static List<TypeInfo> types(Type type){
        JavaType javaType = JsonUtil.getJavaType(type,TypeBindings.emptyBindings());
        List<TypeInfo> types = Lists.newArrayList();
        types(javaType,types);
        Collections.reverse(types);
        return types;
    }



}
