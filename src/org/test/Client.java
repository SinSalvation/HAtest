package org.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Client {
    public static void main(String[] args)
            throws IOException {
        for(int i=0;i<100;i++){
            new Thread(){
                @Override
                public void run(){
                    try {
                        Socket socket = new Socket("127.0.0.1", 80);
                        PrintStream ps = new PrintStream(socket.getOutputStream());
                        ps.println("请求80！");
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));
                        String line = br.readLine();
                        System.out.println("来自服务器的数据：" + line);
                        br.close();
                        ps.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }
}
