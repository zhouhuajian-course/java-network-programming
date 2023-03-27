package org.example.udp;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

@Slf4j
public class UDPClient {
    public static void main(String[] args) throws IOException {
        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.configureBlocking(false);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            Scanner scanner = new Scanner(System.in);
            log.debug("UDP客户端启动");
            log.debug(">> 请输入内容：");
            InetSocketAddress address = new InetSocketAddress("127.0.0.3", 8080);
            while (scanner.hasNext()) {
                // 不包括末尾换行符
                String input = scanner.next();
                // log.debug("{}", input.length());
                buffer.put(input.getBytes());
                buffer.flip();
                channel.send(buffer, address);
                buffer.clear();
            }
        }
    }
}
