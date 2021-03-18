package com.github.jingshouyan.jrpc.starter.client.factory;

import javassist.*;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 代理工厂
 *
 * @author jingshouyan
 * 2020-11-02 11:16
 **/
@Slf4j
public class ProxyFactory {
    public static Object newProxyInstance(ClassLoader classLoader, Class<?> interfaceClass, InvocationHandler h) throws Throwable {
        ClassPool pool = ClassPool.getDefault();

        String proxyClassName = interfaceClass.getCanonicalName() + "$Proxy";
        // 1.创建代理类 ProxyClass
        CtClass proxyClass = pool.makeClass(proxyClassName);


        // 2.给代理类添加字段：handler;
        CtClass handlerCc = pool.get(InvocationHandler.class.getName());
        // CtField(CtClass fieldType, String fieldName, CtClass addToThisClass)
        CtField handlerField = new CtField(handlerCc, "handler", proxyClass);
        handlerField.setModifiers(AccessFlag.PRIVATE);
        proxyClass.addField(handlerField);

        // 3.添加构造函数：public NewProxyClass(InvocationHandler handler) { this.handler = handler; }
        CtConstructor ctConstructor = new CtConstructor(new CtClass[]{handlerCc}, proxyClass);
        // $0代表this, $1代表构造函数的第1个参数
        ctConstructor.setBody("$0.handler = $1;");

        proxyClass.addConstructor(ctConstructor);
        // 4.为代理类添加相应接口方法及实现
        CtClass ctInterface = pool.get(interfaceClass.getName());

        // 4.1 为代理类添加接口：public class ProxyClass implements IHello
        proxyClass.addInterface(ctInterface);

        List<Class<?>> is = getAllInterfaces(interfaceClass);
        for (Class<?> ic : is) {
            // 4.为代理类添加相应接口方法及实现
            CtClass icc = pool.get(ic.getName());
            // 4.2 为代理类添加相应方法及实现
            CtMethod[] ctMethods = icc.getDeclaredMethods();
            for (CtMethod ctMethod : ctMethods) {

                // 新的方法名
                String methodFieldName = ctMethod.getName();

                // 4.2.1 为代理类添加反射方法字段
                // 构造反射字段声明及赋值语句
                // 方法的多个参数类型以英文逗号分隔
                String classParamsStr = "new Class[0]";
                // getParameterTypes获取方法参数类型列表
                if (ctMethod.getParameterTypes().length > 0) {

                    for (CtClass clazz : ctMethod.getParameterTypes()) {
                        classParamsStr = (("new Class[0]".equals(classParamsStr)) ? clazz.getName() : classParamsStr + "," + clazz.getName()) + ".class";
                    }
                    classParamsStr = "new Class[] {" + classParamsStr + "}";
                }
                String methodFieldTpl = "private static java.lang.reflect.Method %s=Class.forName(\"%s\").getDeclaredMethod(\"%s\", %s);";
                String methodFieldBody = String.format(methodFieldTpl, ctMethod.getName(), ic.getName(), ctMethod.getName(), classParamsStr);
                // 为代理类添加反射方法字段. CtField.make(String sourceCodeText, CtClass addToThisClass)
                CtField methodField = CtField.make(methodFieldBody, proxyClass);
                proxyClass.addField(methodField);

                // 4.2.2 为方法添加方法体

                String methodBody = "$0.handler.invoke($0, " + methodFieldName + ", $args)";
                // 如果方法有返回类型，则需要转换为相应类型后返回，因为invoke方法的返回类型为Object
                if (CtPrimitiveType.voidType != ctMethod.getReturnType()) {
                    // 对8个基本类型进行转型
                    // 例如：((Integer)this.handler
                    // .invoke(this, this.m2, new Object[] { paramString, new Boolean(paramBoolean), paramObject }))
                    // .intValue();
                    if (ctMethod.getReturnType() instanceof CtPrimitiveType) {
                        CtPrimitiveType ctPrimitiveType = (CtPrimitiveType) ctMethod.getReturnType();
                        methodBody = "return ((" + ctPrimitiveType.getWrapperName() + ") " + methodBody + ")." + ctPrimitiveType.getGetMethodName() + "()";
                    } else { // 对于非基本类型直接转型即可
                        methodBody = "return (" + ctMethod.getReturnType().getName() + ") " + methodBody;
                    }
                }
                methodBody += ";";
                // 为代理类添加方法. CtMethod(CtClass returnType, String methodName, CtClass[] parameterTypes, CtClass addToThisClass)
                CtMethod newMethod = new CtMethod(ctMethod.getReturnType(), ctMethod.getName(),
                        ctMethod.getParameterTypes(), proxyClass);
                newMethod.setBody(methodBody);
                proxyClass.addMethod(newMethod);
            }

        }
        // 将接口上的注解添加到代理类上
        Annotation[] annotations = interfaceClass.getAnnotations();
        ClassFile classFile = proxyClass.getClassFile();
        ConstPool constPool = classFile.getConstPool();
        addAnnotationToCtClass(classFile, constPool, annotations);
        // 5.生成代理实例. 将入参InvocationHandler handler设置到代理类的InvocationHandler handler变量
        Class<?> newClass = proxyClass.toClass(classLoader, null);
        Constructor<?> constructor = newClass.getConstructor(InvocationHandler.class);
        return constructor.newInstance(h);
    }

    private static List<Class<?>> getAllInterfaces(Class<?> clazz) {
        List<Class<?>> list = new ArrayList<>();
        list.add(clazz);
        Class<?>[] cs = clazz.getInterfaces();
        for (Class<?> c : cs) {
            list.addAll(getAllInterfaces(c));
        }
        return list;
    }

    /**
     * 添加注解到CtClass
     *
     * @param annotation
     * @param classFile
     * @param constPool
     * @throws NotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private static void addAnnotationToCtClass(ClassFile classFile, ConstPool constPool, Annotation... annotation)
            throws NotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        AnnotationsAttribute attr = new AnnotationsAttribute(constPool,
                AnnotationsAttribute.visibleTag);
        javassist.bytecode.annotation.Annotation[] annotations = new javassist.bytecode.annotation.Annotation[annotation.length];
        for (int i = 0; i < annotation.length; i++) {
            annotations[i] = createJavaAssistAnnotation(annotation[i], constPool);
        }
        attr.setAnnotations(annotations);
        classFile.addAttribute(attr);

    }

    /**
     * 根据JDK的注解实例，创建javaassist的注解实例
     *
     * @param jdkAnnotation
     * @param constPool
     * @return
     * @throws NotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static javassist.bytecode.annotation.Annotation createJavaAssistAnnotation(
            Annotation jdkAnnotation, ConstPool constPool) throws NotFoundException,
            NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        CtClass jdkAnnotaionCtClass = ClassPool.getDefault()
                .get(jdkAnnotation.annotationType().getCanonicalName());
        // 使用已有的jdk annotation构造javaassist annotation
        javassist.bytecode.annotation.Annotation javaAssistAnnotation = new javassist.bytecode.annotation.Annotation(
                constPool, jdkAnnotaionCtClass);
        setJavaAssistAnnotation(javaAssistAnnotation, jdkAnnotation, constPool);
        return javaAssistAnnotation;
    }

    /**
     * 遍历jdk注解属性，设置到javaassist注解中
     *
     * @param javaAssistAnnotaion
     * @param jdkAnnotation
     * @param constPool
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private static void setJavaAssistAnnotation(
            javassist.bytecode.annotation.Annotation javaAssistAnnotaion,
            Annotation jdkAnnotation, ConstPool constPool) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {

        Class<? extends Annotation> clazz = jdkAnnotation.annotationType();
        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            String methodName = method.getName();
            log.info("Get method : {}", methodName);
            Object value = method.getDefaultValue();
            log.info("\tGet Default Value:{}", value);
            Class<?> returnType = method.getReturnType();
            log.info("\tGet Return Type:{}", returnType);
            Object actualValue = MethodUtils.invokeExactMethod(jdkAnnotation, methodName);
            if (!Objects.equals(value, actualValue)) {
                value = actualValue;
            }
            MemberValue memberValue = createMemberValue(returnType, value, constPool);
            if (memberValue != null) {
                javaAssistAnnotaion.addMemberValue(methodName, memberValue);
            }
        }
    }

    /**
     * 工程方法，根据returnType类型创建相应的MemberValue
     *
     * @param returnType
     * @param value
     * @param constPool
     * @return
     */
    private static MemberValue createMemberValue(Class<?> returnType, Object value,
                                                 ConstPool constPool) {
        MemberValue result = null;
        if (returnType.isAssignableFrom(String.class)) {
            result = new StringMemberValue(((String) value), constPool);
        } else if (returnType.isAssignableFrom(boolean.class)) {
            result = new BooleanMemberValue((Boolean) value, constPool);
        } else if (returnType.isAssignableFrom(int.class)) {
            result = new IntegerMemberValue(constPool, (Integer) value);
        }

        // TODO: 编写其他类型的
        return result;
    }
}
