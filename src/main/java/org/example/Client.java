package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8080));
        System.out.println("add a breakpoint here, and use evaluate expressionion.");
        // sc.write(Charset.defaultCharset().encode("abc"))
        // sc.write(Charset.defaultCharset().encode("123"))
    }
}
