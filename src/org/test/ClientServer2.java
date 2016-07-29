package org.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientServer2 {
    static BlockingQueue<Socket> bq = new ArrayBlockingQueue<Socket>(10);
    static ExecutorService pool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws Exception {
        alive();
        for (int i = 0; true; i++) {
            Socket s = bq.take();
            switch (i % 3) {
                case 0:
                    squid(8080, s);
                    break;
                case 1:
                    squid(8081, s);
                    break;
                case 2:
                    squid(8082, s);
                    break;
                default:
                    break;
            }
        }
    }

    public static void alive(){
        new Thread(){
            @Override
            public void run() {
                try {
                    while(true){
                        Socket socket = new Socket("127.0.0.1", 80);
                        PrintStream ps = new PrintStream(socket.getOutputStream());
                        ps.println(" ");
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));
                        br.readLine();
                        br.close();
                        ps.close();
                        socket.close();
                        sleep(5000);
                    }
                } catch (IOException e) {
                    try {
                        server();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static void server() throws Exception {
        final ServerSocket ss = new ServerSocket(80);
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    Socket s = null;
                    try {
                        s = ss.accept();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        bq.put(s);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public static void squid(final int port, final Socket s) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Socket socket = null;
                PrintStream ps = null;
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    String line = br.readLine();
                    System.out.println("来自客户端的数据：" + line);
                    ps = new PrintStream(s.getOutputStream());
                    ps.println("80正在转发！");
                    socket = new Socket("127.0.0.1", port);
                    ps = new PrintStream(socket.getOutputStream());
                    ps.println(line);
                    br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    System.out.println("来自服务器的数据：" + br.readLine());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        ps.close();
                        br.close();
                        s.close();
                        socket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        pool.execute(thread);
        //pool.submit(thread);
    }
}

