package com.jing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.github.jingshouyan.jrpc.base.bean.Empty;
import com.github.jingshouyan.jrpc.base.bean.FieldInfo;
import com.github.jingshouyan.jrpc.base.bean.TypeInfo;
import com.github.jingshouyan.jrpc.base.util.bean.ClassInfoUtil;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.google.common.collect.Lists;
import com.jing.test.method.TestMethod;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jingshouyan
 * #date 2018/10/27 9:29
 */
public class Test {

    private static StringBuilder getTypeName(JavaType javaType) {
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
//        if(isSimpleType(javaType.getRawClass())){
//            return;
//        }
        if(isShowGenerics(javaType.getRawClass())){
            return;
        }
        if(javaType.isJavaLangObject()){
            return;
        }
        String typeName = getTypeName(javaType).toString();
        boolean has = typeInfos.stream().anyMatch( typeInfo -> typeName.equals(typeInfo.getType()));
        if(has){
            return;
        }
        TypeInfo typeInfo = new TypeInfo();
        typeInfo.setType(typeName);
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

    private static boolean isShowGenerics(Class<?> clazz){
        if(Collection.class.isAssignableFrom(clazz)){
            return true;
        }
        if(Map.class.isAssignableFrom(clazz)){
            return true;
        }
        return false;
    }



    public static void main(String[] args) {
        Empty empty = Empty.EMPTY;
        System.out.println(JsonUtil.toJsonString(empty));
        TestMethod testMethod = new TestMethod();
        JavaType javaType = JsonUtil.getJavaType(testMethod.getOutputType(),TypeBindings.emptyBindings());
        String typeName = getTypeName(javaType).toString();
        System.out.println(typeName);
        List<TypeInfo> typeInfos = ClassInfoUtil.types(testMethod.getOutputType());
        System.out.println(typeInfos);

        List<TypeInfo> typeInfos1 = ClassInfoUtil.types(String.class);
        System.out.println(typeInfos1);
    }
}
