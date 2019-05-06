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
import lombok.SneakyThrows;

import javax.validation.Constraint;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author jingshouyan
 * #date 2018/10/24 17:49
 */
public class ClassInfoUtil {

    private static final String LEFT_BRACE = "{";
    private static final String RIGHT_BRACE = "}";

    public static BeanInfo beanInfo(Type type) {
        BeanInfo beanInfo = new BeanInfo();
        JavaType javaType = JsonUtil.getJavaType(type,TypeBindings.emptyBindings());
        String rootType = ClassInfoUtil.getTypeName(javaType).toString();
        List<TypeInfo> types = ClassInfoUtil.types(type);
        beanInfo.setRootType(rootType);
        beanInfo.setTypes(types);
        return beanInfo;
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
        typeInfo.setAnnotations(annotations(annotations));
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
                fieldInfo.setAnnotations(annotations(fieldAnnotations));

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


    private static List<String> annotations(Annotation[] annotations) {
        return Stream.of(annotations)
                .filter(annotation -> annotation.annotationType().isAnnotationPresent(Constraint.class))
                .map(ClassInfoUtil::annotationStr).collect(Collectors.toList());
    }

    @SneakyThrows
    private static String annotationStr(Annotation annotation) {
        StringBuilder sb = new StringBuilder();
        Class<?> clazz = annotation.annotationType();
        sb.append(clazz.getSimpleName());
        StringBuilder sb2 = new StringBuilder();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method m : methods) {
            int modifiers = m.getModifiers();
            if(Modifier.isStatic(modifiers)){
                continue;
            }
            if(m.getParameterCount() > 0){
                continue;
            }
            String name = m.getName();
            Object value = m.invoke(annotation);
            String valueStr = valueStr(value);
            if(valueStr == null) {
                continue;
            }
            sb2.append(name);
            sb2.append("=");
            sb2.append(valueStr);
            sb2.append(",");
        }
        if(sb2.length() > 0) {
            sb2.deleteCharAt(sb2.length() -1);
            sb.append("(");
            sb.append(sb2);
            sb.append(")");
        }
        return sb.toString();
    }

    private static String valueStr(Object value) {
        if(value == null) {
            return null;
        }
        if(value instanceof String) {
            String str = (String) value;
            if("".equals(str)){
                return null;
            }
            if(str.startsWith(LEFT_BRACE)&&str.endsWith(RIGHT_BRACE)){
                return null;
            }
            return str;
        }
        if(value.getClass().isArray()) {
            Object[] objects = (Object[]) value;
            if(objects.length == 0){
                return null;
            }
            return Lists.newArrayList(objects).toString();
        }
        return value.toString();
    }

    public static void main(String[] args) {

        String[] a = {"abc","def"};
        Object[] c = a;
        List<String> b = Stream.of(a).collect(Collectors.toList());
        System.out.println(a);
        System.out.println(b);
    }

}
