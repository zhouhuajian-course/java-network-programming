# Java 网络编程

## 单线程 阻塞IO Blocking I/O

accept 阻塞的时候，不能 read  
read 阻塞的时候，不能 accept

## 单线程 非阻塞IO Non-blocking I/O

accept read 非阻塞

可正常 accept read 但 CPU 长时间空转 电脑风扇会响 约使用 20% CPU

## 单线程 多路复用 I/O multiplexing

或叫做事件驱动IO

```text
// 客户端可读时，触发OP_READ事件， 正常数据 >= 0 客户端正常断开连接 -1  客户端异常断开连接 抛IOException 远程主机强迫关闭了一个现有的连接
public static final int OP_READ = 1 << 0; 
// 读到客户端数据进行处理后，写入客户端，如果没法一次写完，需要注册监听客户端可写事件，等客户端可写时，写入剩余数据
public static final int OP_WRITE = 1 << 2;
public static final int OP_CONNECT = 1 << 3;
// 有客户端连接时，触发OP_ACCEPT事件
public static final int OP_ACCEPT = 1 << 4;
```

## 信号驱动 signal-driven I/O

## 异步IO asynchronous I/O

## 其他

Server Client 都用调试方式运行 方便调试

调试状态下，可选中代码，再点击 Evaluate Expression

