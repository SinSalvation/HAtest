package org.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server2 {
    public static void main(String[] args)
            throws IOException {
        ServerSocket ss = new ServerSocket(8081);
        while (true) {
            Socket s = ss.accept();
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line = br.readLine();
            System.out.println("来自客户端的数据："+line);
            PrintStream ps = new PrintStream(s.getOutputStream());
            ps.println("8081建立连接！");
            br.close();
            ps.close();
            s.close();
        }
    }
}

