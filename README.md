# [j-rpc][1]
j-rpc 基于thrift 的 json 格式 rpc调用框架


## 目录结构
1. j-rpc-base # 基础包
2. j-rpc-server # server 包，主要包含：服务注册，方法接口，内置方法
3. j-rpc-client # client 包
4. j-rpc-registry # 注册与发现
5. j-rpc-common # 
    1.  j-rpc-common-crud # [j-jdbc][2] 接口工具包
    2.  j-rpc-common-trace # 调用链追踪
6. j-rpc-starter # springboot starter
    1. j-rpc-starter-client # 客户端
    2. j-rpc-starter-server # 服务端
    3. j-rpc-starter-registry # 注册与发现
    4. j-rpc-starter-forward # 接口转发
    5. j-rpc-starter-dql # [j-jdbc][2] 查询接口
    6. j-rpc-starter-dml # [j-jdbc][2] 增删改接口
    7. j-rpc-starter-trace-zipkin # 基于 zipkin 的调用链追踪
    8. j-rpc-starter-desensitize # 接口请求&响应数据脱敏,日志|zipkin
7. j-rpc-test # 测试相关
    1.  j-rpc-test-client # 客户端测试
    2.  j-rpc-test-server # 服务端测试
    3.  j-rpc-test-forward # 转发测试
    4.  j-rpc-test-jmeter # jmeter 接口测试包
8. j-rpc-apidoc # 根据线上服务方法生成的接口文档

## 简介

server 启动后，将连接信息注册到 zookeeper，client 监听 zk 服务节点树，将所有服务信息缓存到内存中。当发起一次调用请求时根据服务名，版本号，服务实例名等路由信息来获取一个连接信息，然后根据连接信息创建一个连接池（如果该连接池已存在，复用），从连接池取出一个将数据发送到 server。

## 基本用法

### server:
> 参见 j-rpc-test-server
#### 1. 引入pom
```mvn
<dependency>
    <groupId>com.github.jingshouyan</groupId>
    <artifactId>j-rpc-starter-server</artifactId>
    <version>${jrpc-version}</version>
</dependency>
```

#### 2. 添加 spring 配置信息
```yaml
j-rpc:
  registry:
    model: zookeeper # 目前仅支持 zookeeper
    inet: 127.0.0.1 # 本机ip, 获取本机ip优先级 inet>inet-env>InetAddress.getLocalHost().getHostAddress()>127.0.0.1
    inet-env: LOCAL_IP # 本级ip环境变量key
    zookeeper:
      address: 127.0.0.1:2181 # zk 地址 ,优先级 address>address-env>127.0.0.1:2181
      address-env : ZK_ADDR # zk 地址环境变量
      namespace: /com.github.jingshouyan.jrpc # 服务注册的namespace
      session-timeout: 20000 # 
      connection-timeout: 5000 #
      retry-interval-ms: 5000 #
  server:
    version: v2.0 #服务版本号 default：v1.0
    port: 8999 #端口号 default：8888
    name: test #服务名 default：j-rpc
    timeout: 5000 # 接口超时时间 default：5000
    maxReadBufferBytes: 102400 # 缓冲区最大长度 default：25 * 1024 * 1024 （25MB）

```

#### 3. 实现 Method<T,R> 接口 或 AsyncMethod<T,R> 接口
```java
@Data
public class IdQuery {
    //基于 validation 的注解,如果验证不通过会返回 Code.PARAM_INVALID 错误码,如果想返回自定义的错误码,message 设置如下
    @NotNull(message = BaseConstant.INVALID_CODE_PREFIX + TestCode.NAME_IS_NULL)
    @Size(min = 4,max = 20)
    private String name;

    @Min(5)@Max(99)
    private int age = 10;

    @NotNull@Size(min = 1,max= 100)
    private List<String> ids;
}
```
```java
@Component("getUserInfo")
public class GetUserInfo implements Method<IdQuery,List<UserBean>> {

    // 本方法只会在 idQuery 校验成功执行
    @Override
    public List<UserBean> action(Token token,IdQuery idQuery) {
        // throw new JException(TestCode.JUST_ERROR);  //通过异常返回错误码
//         throw new JException(TestCode.JUST_ERROR,idQuery);  //通过异常返回错误码,并返回一些数据
        return idQuery.getIds().stream().map(id -> {
            UserBean userBean = new UserBean();
            userBean.setId(id);
            userBean.setAge(idQuery.getAge());
            userBean.setName(idQuery.getName());
            return userBean;
        }).collect(Collectors.toList());
    }
}
```
```java
@Component("getUserInfo2")
public class GetUserInfo2 implements AsyncMethod<IdQuery,List<UserBean>> {

    // 本方法只会在 idQuery 校验成功执行
    @Override
    public Mono<List<UserBean>> action(Token token,IdQuery idQuery) {
        // throw new JException(TestCode.JUST_ERROR);  //通过异常返回错误码
//         throw new JException(TestCode.JUST_ERROR,idQuery);  //通过异常返回错误码,并返回一些数据
        return Mono.fromCallable(() -> {
                throw new JException(TestCode.JUST_ERROR,idQuery);//任何位置都可以使用来返回
                return idQuery.getIds().stream().map(id -> {
                    UserBean userBean = new UserBean();
                    userBean.setId(id);
                    userBean.setAge(idQuery.getAge());
                    userBean.setName(idQuery.getName());
                    return userBean;
                }).collect(Collectors.toList());
        });

    }
}
```

### client:
> 参见 j-rpc-test-client
#### 1. 引入pom
```mvn
<dependency>
    <groupId>com.github.jingshouyan</groupId>
    <artifactId>j-rpc-starter-client</artifactId>
    <version>${jrpc-version}</version>
</dependency>
```

#### 2. 添加 spring 配置信息
```yaml
j-rpc:
  registry:
    model: zookeeper # 目前仅支持 zookeeper
    inet: 127.0.0.1 # 本机ip, 获取本机ip优先级 inet>inet-env>InetAddress.getLocalHost().getHostAddress()>127.0.0.1
    inet-env: LOCAL_IP # 本级ip环境变量key
    zookeeper:
      address: 127.0.0.1:2181 # zk 地址 ,优先级 address>address-env>127.0.0.1:2181
      address-env : ZK_ADDR # zk 地址环境变量
      namespace: /com.github.jingshouyan.jrpc # 服务注册的namespace
      session-timeout: 20000 # 
      connection-timeout: 5000 #
      retry-interval-ms: 5000 #
  client:
    connect: # 
      timeout: 5000
    pool: # 连接池配置
      min-idle: 10 
      max-idle: 50
      max-total: 200


```

#### 3. 使用方法1
```java
public class Test 
{
    @Resource
    private JrpcClient jrpcClient;

    public void test2(){
        IdQuery idQuery = new IdQuery();
        idQuery.setName("zhangsan");
        idQuery.setAge(77);
        idQuery.setIds(Lists.newArrayList("123","345"));

        Token token = new Token();

        Rsp rsp = Request.newInstance()
                .setClient(jrpcClient) //设置发送客户端
                .setServer("test")     //调用的服务名
                .setVersion("2.0")   //服务的版本号
//                .setInstance("test-111") //服务实例名,多个实例可以指定发送到对应的服务,没找到会有相应的错误码
                .setMethod("getUserInfo") //服务方法名
                .setToken(token) // 设置token ,可选 token 信息
                .setParamObj(idQuery) //请求参数对象,也可以使用 setParamJson 直接设置json字符串
                //.setOneway(true) //是否为 oneway 调用,
                //asyncSend() 得到 Single<Rsp> 对象,异步调用模式
                .send() //发送请求,这时已经得到 Rsp 对象

                .checkSuccess(); //检查 返回码,不为 SUCCESS 则抛出异常
        List<UserBean> userBeans = rsp.list(UserBean.class); //rsp中result实际为json字符串.list为将json反序列化为 List对象
        List<UserBean> userBeans1 = rsp.get(List.class,UserBean.class); //也可以使用 get 带泛型的反序列化
    }
}

```

#### 使用方法2

##### 添加 @EnableJrpcServices 注解
```java
@SpringBootApplication
@EnableJrpcServices
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```
##### 编写服务接口 
```java
@JrpcService(server = "test", version = "1.0")
public interface TestService {

    void traceTest2(Token token, int i);

    Rsp asyncErr(Token token, String abc);

    String asyncTest(Token token, String abc);

    Mono<Void> myMethod(Token token, Object abc);

    Mono<Object> testMethod(Token token, List<String> abc);

    Mono<Rsp> traceTest(Token token, int i);

    Mono<InterfaceInfo> getServerInfo(Token token, Object obj);
}

```
> 必须添加 @JrpcService 注解, server,version 为对应服务名&版本号.<br>
> 方法名为对应接口名,参数0 必须是 Token,参数1 接口入参,参考Method.action,参数3可选 指定实例名 <br>
> 返回值类型
>> void 不关心结果,不关心调用是否出错. <br>
>> Rsp 得到 Rsp 响应. <br>
>> 其他类型 将 Rsp 中 result 转换成对应类型的结果,如果请求失败,抛出 JException 异常. <br>
>> Mono\<Void> 不建议使用,使用Mono<Rsp>代替. <br>
>> Mono\<Rsp> 响应式编程对象. <br>
>> Mono\<其他类型> 将 Rsp 中 result 转换成对应类型的结果,如果请求失败,在mono线程中抛出 JException 异常.

##### @Autowired 注入对象

```java
public class ServiceTest {
    @Autowired
    private TestService testService;
}

```
### 接口转发模块

#### 1. 引入pom

```mvn
<dependency>
    <groupId>com.github.jingshouyan</groupId>
    <artifactId>j-rpc-starter-forward</artifactId>
    <version>${jrpc-version}</version>
</dependency>
```

#### 2. 添加 spring 配置信息

```yaml
j-rpc:
  forward:
    methods:  #格式: 本服务方法名: 转发服务名,对应方法名
      forwardTest: test,testMethod
      forwardTest2: test2,testMethod
```

### zipkin 集成

#### 1. 引入pom

```mvn
<dependency>
    <groupId>com.github.jingshouyan</groupId>
    <artifactId>j-rpc-starter-trace-zipkin</artifactId>
    <version>${jrpc-version}</version>
</dependency>
```

#### 2. 添加 spring 配置信息

```yaml
j-rpc:
  trace:
    name: trace-xxx # 链路名称，可选，若无取 jrpc.server.name 或 spring.application.name
    endpoint: http://127.0.0.1:9411/api/v2/spans # zipkin 接口地址
    rate: 0.1 # 采样率 0 ~ 1 
    dataShow: 0 # 请求参数&响应数据采集模式, 0: 不采集,1: 接口返回错误时采集,2:采集 
```

#### 3. 字节码注入
```sh
# 启动时注入 TransmittableThreadLocal
# 主要用于使用线程池和forkjoin框架时 span 信息传递
java -javaagent:path/to/transmittable-thread-local-2.x.x.jar -jar test-server.jar
```
### 数据脱敏
#### 1. 引入pom

```mvn
<dependency>
    <groupId>com.github.jingshouyan</groupId>
    <artifactId>j-rpc-starter-desensitize</artifactId>
    <version>${jrpc-version}</version>
</dependency>
```

#### 2. 添加 spring 配置信息

```yaml
j-rpc:
  desensitize:
    settings: 
      name: 203 # key 为 name,且值为String类型的字段,除开头2位结尾3位,其他以 * 替换  
```

### 查询接口
#### 引入pom

```mvn
<dependency>
    <groupId>com.github.jingshouyan</groupId>
    <artifactId>j-rpc-starter-dql</artifactId>
    <version>${jrpc-version></version>
</dependency>
```
> [retrieve][11] 方法自动注册到服务

#### client 调用
```java

```

### 增删改接口
#### 1.引入pom

```mvn
<dependency>
    <groupId>com.github.jingshouyan</groupId>
    <artifactId>j-rpc-starter-dml</artifactId>
    <version>${jrpc-version></version>
</dependency>
```
> [create][12] 方法自动注册到服务
>
> [update][13] 方法自动注册到服务
>
> [delete][14] 方法自动注册到服务

#### 2. 添加 spring 配置信息
```yml
j-rpc:
  server:
    plugin:
      crud:
        create: * # * 表示允许全部,默认为 * ,将所有dao 的 insert/batchInsert 方法暴露到服务
        update: user,account # userDao, accountDao 的 update/batchUpdate 方法暴露到服务
        delete: user # userDao 的 delete/delete4List 方法暴露到服务
```
### 示例
https://github.com/jingshouyan/j-rpc-demo

[1]: https://github.com/jingshouyan/j-rpc "j-rpc"
[2]: https://github.com/jingshouyan/j-jdbc "j-jdbc"
[11]: j-rpc-starter/j-rpc-starter-dql/src/main/java/com/github/jingshouyan/jrpc/crud/dql/method/Retrieve.java "retrieve"
[12]: j-rpc-starter/j-rpc-starter-dml/src/main/java/com/github/jingshouyan/jrpc/crud/dml/method/Create.java "create"
[13]: j-rpc-starter/j-rpc-starter-dml/src/main/java/com/github/jingshouyan/jrpc/crud/dml/method/Update.java "update"
[14]: j-rpc-starter/j-rpc-starter-dml/src/main/java/com/github/jingshouyan/jrpc/crud/dml/method/Delete.java "delete"