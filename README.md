## 内网穿透工具

- Netty
- TCP
- JAVA

#### 心跳

1. client在一定时间A内没有写操作，就向服务端发一个心跳包（这样能让服务端产生读操作）
2. server在一定时间B内没有读操作，直接断开和该client的连接
3. B略大于A
4. server收到心跳包后可以向client写个响应包(~~没太大必要~~)
5. 假如连接断开，client将会抛出异常，捕获异常，重试连接server

#### 半包/粘包问题

使用LengthFieldBasedDecoder解决


#### 服务端

**数据流向**

> UserRequest -> OuterHandler(Server) -> DataHandler(Client) -> Services..

**流程**

- 启动注册服务
    - 接到客户端的注册请求
    - 根据客户端注册内容和自己的配置文件文件比对
    - 比对通过，开放映射的端口
- 用户请求开放的接口
    - 把channel和生成的session id映射存好
    - 从channel读取用户过来的数据
    - 打包发给client（包括数据、session id等）
- 从客户端收到包裹
    - 解包，根据session id,获取channel，写入数据
    - 如果是client请求关闭外部连接，把channel关了
- 客户端连接断开
    - 关闭相关的开放端口

#### 客户端

**数据流向**

> Services -> ServerHandler -> ClientDataHandler -> OuterRequestChannel

**流程**

- 发送注册请求到服务端
- 收到服务端包裹
    - 打开真实服务的连接（根据session id确定是否打开，已存在session，直接使用）
    - 向真实通道写入服务端过来的数据
- 接收真实服务的数据
    - 打包发给服务端（内容包括数据、session id等）
    - 如果真实服务关闭了连接，也要发个包告诉服务端
