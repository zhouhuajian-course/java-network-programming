package org.example.udp;

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
public class CustomProtocolUDPServer {
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
                    // channel.receive(buffer);
                    channel.receive(buffer);
                    buffer.flip();
                    // 数据校验 crc16 两个字节 16个二进制位
                    // 2字节
                    short length = Util.byte2ToShort(new byte[]{buffer.get(), buffer.get()});
                    byte serializeType = buffer.get();
                    short crc = Util.byte2ToShort(new byte[]{buffer.get(), buffer.get()});
                    byte[] bodyBytes = new byte[length - 2 - 1 - 2];
                    buffer.get(bodyBytes);
                    String body = new String(bodyBytes);
                    Student student = null;
                    if ((short) Util.getCRC16(bodyBytes) == crc) {
                        switch (SerializeType.values()[serializeType]) {
                            case JSON:
                                student = Util.jsonToObject(body, Student.class);
                                break;
                            case XML:
                                break;
                        }
                        log.debug("{} {} {} {} {} {}", length, serializeType, crc, body, buffer, student);
                    } else {
                        log.error("数据校验失败");
                    }

                }
                iterator.remove();
            }
        }

        selector.close();
        channel.close();
    }
}
