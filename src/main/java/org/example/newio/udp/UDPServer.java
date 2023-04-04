package org.example.newio.udp;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

@Slf4j
public class UDPServer {
    public static void main(String[] args) throws IOException {
        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.bind(new InetSocketAddress("127.0.0.3", 8080));
        log.debug("UDP服务端启动");
        Selector selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);

        while (selector.select() > 0) {
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                // 可读事件 有数据到来
                if (selectionKey.isReadable()) {
                    SocketAddress address = channel.receive(buffer);
                    buffer.flip();
                    log.debug("收到客户端数据：{}", new String(buffer.array(), 0, buffer.limit()));
                }
                iterator.remove();
            }
        }

        selector.close();
        channel.close();
    }
}
