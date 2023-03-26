package org.example;

import lombok.extern.slf4j.Slf4j;
import sun.nio.cs.StandardCharsets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;

@Slf4j
public class BlockingIOServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(8080));
        ArrayList<SocketChannel> socketChannels = new ArrayList<>();
        while (true) {
            // blocking method
            log.debug("accept...");
            SocketChannel sc = ssc.accept();
            log.debug("client connected {}", sc);
            socketChannels.add(sc);
            for (SocketChannel socketChannel : socketChannels) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(16);
                log.debug("read...");
                // blocking method
                socketChannel.read(byteBuffer);
                byteBuffer.flip();
                log.debug("data read {}",  Charset.defaultCharset().decode(byteBuffer).toString());
            }
        }
    }
}
