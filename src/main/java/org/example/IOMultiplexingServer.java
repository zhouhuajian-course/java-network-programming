package org.example;

import javafx.scene.control.SelectionMode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

@Slf4j
public class IOMultiplexingServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(8080));
        // 必须设置为非阻塞 不然 IllegalBlockingModeException
        ssc.configureBlocking(false);
        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        // blocking
        while (true) {
            log.debug("selecting...");
            selector.select();
            Set selectedKeys = selector.selectedKeys();
            Iterator iter = selectedKeys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = (SelectionKey) iter.next();
                iter.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    log.debug("client connected {}", sc);
                    // 必须设置为非阻塞 不然 IllegalBlockingModeException
                    sc.configureBlocking(false);
                    sc.register(selector, SelectionKey.OP_READ);
                }
                else if (key.isConnectable()) { }
                else if (key.isReadable()) {
                    SocketChannel sc = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(16);
                    try {
                        int read = sc.read(byteBuffer);
                        if (read > 0) {
                            byteBuffer.flip();
                            log.debug("data read {}", Charset.defaultCharset().decode(byteBuffer).toString());
                        } else if (read == -1) {
                            log.debug("client disconnected {}", sc);
                            key.cancel();
                        }
                    } catch (IOException e) {
                        // 远程主机强迫关闭了一个现有的连接
                        log.debug("client disconnected force {}", sc);
                        key.cancel();
                    }
                }
                else if (key.isWritable()) { }
            }
        }
    }
}
