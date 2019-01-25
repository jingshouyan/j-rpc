# j-rpc
j-rpc 基于thrift 的 json 格式 rpc调用框架

https://github.com/jingshouyan/j-rpc


## 目录结构

1. j-rpc-base # 基础组建
2. j-rpc-server # server 包，主要包含：服务注册，方法接口，内置方法
3. j-rpc-client # client 包
4. j-rpc-server-starter # 基于 springboot-starter 对j-rpc-server 的封装
5. j-rpc-client-starter # 基于 springboot-starter 对j-rpc-client 的封装
6. j-rpc-forward-starter # 接口转发模块
7. j-rpc-trace-zipkin-starter # 基于 springboot-starter 和 zipkin 的服务调用追踪
8. j-rpc-apidoc # 根据线上服务方法生成的接口文档
9. j-rpc-plugins # 服务接口插件
    基于 [j-jdbc][2] 的接口增强
   1. crud-common # crud 接口基础包
   2. crud-dql-starter # 查询接口
   3. crud-dml-starter # 新增、修改、删除接口
10. j-rpc-hmily-starter # 基于 hmily 的 tcc 分布式事务组件,未完成
10. test-server # 测试服务示例
11. test-client # client测试示例
12. test-server-forward # 转发测试


server 启动后，将连接信息注册到zk，client 监听 zk 服务节点树，将所有服务信息缓存到内存中。当发起一次调用请求时根据服务名，版本号，服务实例名等路由信息来获取一个连接信息，然后根据连接信息创建一个连接池（如果该连接池已存在，复用），从连接池取出一个将数据发送到 server。

## 基本用法

### server:
> 参见 test-server
#### 1. 引入pom
```mvn
<dependency>
    <groupId>com.github.jingshouyan</groupId>
    <artifactId>j-rpc-server-starter</artifactId>
    <version>${jrpc-version}</version>
</dependency>
```

#### 2. 添加 spring 配置信息
```yaml
j-rpc:
  server:
    version: v2.0 #服务版本号 default：v1.0
    host: 127.0.0.1 #本机host,默认使用 InetUtils.findFirstNonLoopbackAddress
    port: 8999 #端口号 default：8888
    name: test #服务名 default：j-rpc
    timeout: 5000 # 接口超时时间 default：5000
    maxReadBufferBytes: 102400 # 接口数据最大长度 default：25 * 1024 * 1024 （25MB）
    zkHost: 127.0.0.1:2181 # zk 连接地址 default：127.0.0.1:2181
    zkRoot: /com.github.jingshouyan.jrpc #zk 服务注册根目录

```

#### 3. 实现 Method<T,R> 接口
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

### client:
> 参见 test-client
#### 1. 引入pom
```mvn
<dependency>
    <groupId>com.github.jingshouyan</groupId>
    <artifactId>j-rpc-client-starter</artifactId>
    <version>${jrpc-version}</version>
</dependency>
```

#### 2. 添加 spring 配置信息
```yaml
j-rpc:
  client:
    zkHost: 127.0.0.1:2181 # zk 连接地址 default：127.0.0.1:2181
    zkRoot: /com.github.jingshouyan.jrpc #zk 服务注册根目录
    poolMinIdle: 10 # 连接池配置
    poolMaxIdle: 50
    poolMaxTotal: 200

```

#### 3. 调用方法
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
//                .setVersion("2.0")   //服务的版本号,只选择向 2.0 版本的服务发送数据,没找到会有相应的错误码
//                .setInstance("test-111") //服务实例名,多个实例可以指定发送到对应的服务,没找到会有相应的错误码
                .setMethod("getUserInfo") //服务方法名
                .setToken(token) // 设置token ,可选 token 信息
                .setParamObj(idQuery) //请求参数对象,也可以使用 setParamJson 直接设置json字符串
                .setOneway(true) //是否为 oneway 调用,
                .send() //发送请求,这时已经得到 Rsp 对象
                .checkSuccess(); //检查 返回码,不为 SUCCESS 则抛出异常
        List<UserBean> userBeans = rsp.list(UserBean.class); //rsp中result实际为json字符串.list为将json反序列化为 List对象
        List<UserBean> userBeans1 = rsp.get(List.class,UserBean.class); //也可以使用 get 带泛型的反序列化
    }
}

```

### 接口转发模块

#### 1. 引入pom

```mvn
<dependency>
    <groupId>com.github.jingshouyan</groupId>
    <artifactId>j-rpc-forward-starter</artifactId>
    <version>${jrpc-version}</version>
</dependency>
```

#### 2. 添加 spring 配置信息

```yaml
jrpc:
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
    <artifactId>j-rpc-trace-zipkin-starter</artifactId>
</dependency>
```

#### 2. 添加 spring 配置信息

```yaml
jrpc:
  trace:
    name: trace-xxx # 链路名称，可选，若无取 jrpc.server.name 或 spring.application.name
    endpoint: http://127.0.0.1:9411/api/v2/spans # zipkin 接口地址
    more: true # 更多调用信息 default: false
```

#### 3. 字节码注入
```sh
# 启动时注入 TransmittableThreadLocal
# 主要用于使用线程池和forkjoin框架时 span 信息传递
java -javaagent:path/to/transmittable-thread-local-2.x.x.jar -jar test-server.jar
```


### 查询插件
#### 引入pom

```mvn
<dependency>
    <groupId>com.github.jingshouyan</groupId>
    <artifactId>crud-dql-starter</artifactId>
    <version>${jrpc-version></version>
</dependency>
```
> [retrieve][11] 方法自动注册到服务


### 增删改插件
#### 1.引入pom

```mvn
<dependency>
    <groupId>com.github.jingshouyan</groupId>
    <artifactId>crud-dml-starter</artifactId>
    <version>${jrpc-version></version>
</dependency>
```
> [create][12] 方法自动注册到服务
> [update][13] 方法自动注册到服务
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
[11]: j-rpc-plugins/crud-dql-starter/src/main/java/com/github/jingshouyan/jrpc/crud/dql/method/Retrieve.java "retrieve"
[12]: j-rpc-plugins/crud-dml-starter/src/main/java/com/github/jingshouyan/jrpc/crud/dml/method/Create.java "create"
[13]: j-rpc-plugins/crud-dml-starter/src/main/java/com/github/jingshouyan/jrpc/crud/dml/method/Update.java "update"
[14]: j-rpc-plugins/crud-dml-starter/src/main/java/com/github/jingshouyan/jrpc/crud/dml/method/Delete.java "delete"