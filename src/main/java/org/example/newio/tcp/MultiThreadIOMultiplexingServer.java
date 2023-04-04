package org.example.newio.tcp;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class MultiThreadIOMultiplexingServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(8080));
        // 必须设置为非阻塞 不然 IllegalBlockingModeException
        ssc.configureBlocking(false);
        Selector bossSelector = Selector.open();
        ssc.register(bossSelector, SelectionKey.OP_ACCEPT);
        AtomicInteger index = new AtomicInteger();
        Worker[] workers = new Worker[]{new Worker(), new Worker()};
        // blocking
        while (true) {
            log.debug("boss selecting...");
            bossSelector.select();
            Set selectedKeys = bossSelector.selectedKeys();
            Iterator iter = selectedKeys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = (SelectionKey) iter.next();
                iter.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    log.debug("client connected {}", sc);
                    Worker selectedWorker = workers[Math.abs(index.getAndIncrement() % 2)];
                    selectedWorker.register(sc);
                }
            }
        }
    }

    static class Worker implements Runnable {

        static AtomicInteger count = new AtomicInteger();
        Thread thread = null;
        Selector selector = null;
        ConcurrentLinkedQueue<SocketChannel> queue = null;

        Worker() throws IOException {
            thread = new Thread(this, "worker-" + count.getAndIncrement());
            selector = Selector.open();
            queue = new ConcurrentLinkedQueue<>();
            thread.start();
        }

        public void register(SocketChannel sc) {
            queue.add(sc);
            selector.wakeup();
        }

        @Override
        public void run() {
            try {
                while (true) {
                    log.debug("worker selecting...");
                    selector.select();
                    SocketChannel channel = queue.poll();
                    if (channel != null) {
                        // 必须设置为非阻塞 不然 IllegalBlockingModeException
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ);
                    }
                    Set selectedKeys = selector.selectedKeys();
                    Iterator iterator = selectedKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = (SelectionKey) iterator.next();
                        iterator.remove();
                        if (key.isReadable()) {
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
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
