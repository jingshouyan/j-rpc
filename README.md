# j-rpc
j-rpc 基于thrift 的 json 格式 rpc调用框架

https://github.com/jingshouyan/j-rpc


## 目录结构

1. j-rpc-base # 基础组建
2. j-rpc-server # server 包，主要包含：服务注册，方法接口，内置方法
3. j-rpc-client # client 包
4. j-rpc-server-starter # 基于 springboot-starter 对j-rpc-server 的封装
5. j-rpc-client-starter # 基于 springboot-starter 对j-rpc-client 的封装
6. j-rpc-trace-zipkin-starter # 基于 springboot-starter 和 zipkin 的服务调用追踪
7. j-rpc-apidoc # 根据线上服务方法生成的接口文档
8. j-rpc-plugins # 服务接口插件
   1. crud-common # crud 接口基础包 https://github.com/jingshouyan/j-jdbc
   2. crud-dql-starter # 查询接口
   3. crud-dml-starter # 新增、修该、删除接口
9. test-server # 测试服务示例
10. test-client # client测试示例


server 启动后，将连接信息注册到zk，client 监听 zk 服务节点树，将所有服务信息缓存到内存中。当发起一次调用请求时根据服务名，版本号，服务实例名等路由信息来获取一个连接信息，然后根据连接信息创建一个连接池（如果该连接池已存在，复用），从连接池取出一个将数据发送到 server。

## 基本用法

### server:

#### 1. 引入pom
```mvn
<dependency>
    <groupId>com.github.jingshouyan</groupId>
    <artifactId>j-rpc-server-starter</artifactId>
    <version>${jrpc.version}</version>
</dependency>
```

#### 2. 添加 spring 配置信息
```yaml
j-rpc:
  server:
    version: v2.0 #服务版本号 default：v1.0
    port: 8999 #端口号 default：8888
    name: test #服务名 default：j-rpc
    timeout: 5000 # 接口超时时间 default：5000
    maxReadBufferBytes: 102400 # 接口数据最大长度 default：25 * 1024 * 1024 （25MB）
    zkHost: 127.0.0.1:2181 # zk 连接地址 default：127.0.0.1:2181
    zkRoot: /com.github.jingshouyan.jrpc #zk 服务注册根目录

```

#### 3. 实现 Method<T,R> 接口
```java
// 方法名为注册到 spring 中的名称
@Component("traceTest")
public class TraceTest implements Method<Integer,Integer> {

    public static final ExecutorService exec = Executors.newFixedThreadPool(20,new ThreadFactoryBuilder().setNameFormat("exec-%d").build());

    @Autowired
    ServerProperties properties;

    @Autowired
    JrpcClient client;

    @Override
    public Integer action(Token token, Integer i) {
        if(i!=null && i>0){
            exec.execute(
                    () -> {
                        for (int j = 0; j < 2; j++) {
                            Request.newInstance().setClient(client)
                                    .setServer(properties.getName())
                                    .setMethod("traceTest")
                                    .setParamObj(i-1)
                                    .setOneway(true)
                                    .send();
                        }

                    }
            );

        }
        return i;
    }
}
```

### client:

#### 1. 引入pom
```mvn
<dependency>
    <groupId>com.github.jingshouyan</groupId>
    <artifactId>j-rpc-client-starter</artifactId>
    <version>${jrpc.version}</version>
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

@Resource
private JrpcClient jrpcClient;

Rsp rsp = Request.newInstance() // 新建request
            .setClient(jrpcClient) // 设置 client
            .setServer("test") // 路由信息：服务名
            .setMethod("traceTest") // 路由信息： 方法名
            .setParamObj(12) // 请求参数对象
            .send(); // 发送请求

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
