package org.example.udp;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

@Slf4j
public class CustomProtocolUDPClient {
    public static void main(String[] args) throws IOException {
        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.configureBlocking(false);
            log.debug("UDP客户端启动");
            InetSocketAddress address = new InetSocketAddress("127.0.0.3", 8080);
            Student student = new Student("Jack", 18);
            while (true) {
                // 1字节
                byte serializeType = (byte) SerializeType.JSON.ordinal();
                String body = Util.objectToJson(student);
                byte[] bodyBytes = body.getBytes();
                // 数据校验 crc16 两个字节 16个二进制位
                short crc = (short) Util.getCRC16(bodyBytes);
                // 2字节
                short length = (short) (2 + 1 + 2 + bodyBytes.length);
                ByteBuffer buffer = ByteBuffer.allocate(length);
                buffer.put(Util.shortToByte2(length));
                buffer.put(serializeType);
                buffer.put(Util.shortToByte2(crc));
                buffer.put(bodyBytes);
                buffer.flip();
                log.debug("{} {} {} {} {} {}", length, serializeType, crc, body, buffer, student);
                channel.send(buffer, address);
                Thread.sleep(5000);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
