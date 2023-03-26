package org.example;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;

@Slf4j
public class NonBlockingIOServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(8080));
        ssc.configureBlocking(false);
        ArrayList<SocketChannel> socketChannels = new ArrayList<>();
        while (true) {
            // non-blocking
            // log.debug("accept...");
            SocketChannel sc = ssc.accept();
            if (sc != null) {
                sc.configureBlocking(false);
                log.debug("client connected {}", sc);
                socketChannels.add(sc);
            }
            for (SocketChannel socketChannel : socketChannels) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(16);
                // log.debug("read...");
                // non-blocking
                int read = socketChannel.read(byteBuffer);
                if (read > 0) {
                    byteBuffer.flip();
                    log.debug("data read {}",  Charset.defaultCharset().decode(byteBuffer).toString());
                }
            }
        }
    }
}
