package com.mudgame;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * MUD游戏网络客户端
 */
public class NetworkGameClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private BufferedReader userInput;
    private boolean running = true;
    
    public static void main(String[] args) {
        // 设置控制台输出编码为UTF-8
        try {
            System.setProperty("file.encoding", "UTF-8");
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
            System.setErr(new PrintStream(System.err, true, "UTF-8"));
        } catch (Exception e) {
            System.err.println("Warning: Failed to set UTF-8 encoding: " + e.getMessage());
        }
        NetworkGameClient client = new NetworkGameClient();
        String serverAddress = "10.42.110.251";//在这里进行更改连接主机10.42.110.251
        int port = 4444;
        
        if (args.length >= 1) {
            serverAddress = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("无效的端口号，使用默认端口8888");
            }
        }
        
        client.connect(serverAddress, port);
    }
    
    public void connect(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            // 设置控制台输入编码为UTF-8
            try {
                System.setProperty("file.encoding", "UTF-8");
                java.lang.reflect.Field charset = java.nio.charset.Charset.class.getDeclaredField("defaultCharset");
                charset.setAccessible(true);
                charset.set(null, StandardCharsets.UTF_8);
            } catch (Exception e) {
                // 如果反射失败，使用默认方式
            }
            userInput = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            
            System.out.println("已连接到服务器: " + serverAddress + ":" + port);
            System.out.println("========================================");
            
            // 启动读取服务器消息的线程
            Thread readThread = new Thread(() -> {
                try {
                    String message;
                    while (running && (message = in.readLine()) != null) {
                        System.out.println(message);
                        // 如果是提示输入的命令，不需要额外处理
                    }
                } catch (IOException e) {
                    if (running) {
                        System.out.println("与服务器断开连接");
                    }
                }
            });
            readThread.start();
            
            // 主线程处理用户输入
            String userCommand;
            while (running && (userCommand = userInput.readLine()) != null) {
                if (userCommand.trim().equalsIgnoreCase("quit") || 
                    userCommand.trim().equalsIgnoreCase("exit")) {
                    out.println("9"); // 发送退出命令
                    break;
                }
                out.println(userCommand);
            }
            
            running = false;
            closeConnection();
            
        } catch (UnknownHostException e) {
            System.err.println("无法找到服务器: " + serverAddress);
            System.err.println("请确保服务器已启动，并且地址正确");
        } catch (IOException e) {
            System.err.println("连接服务器失败: " + e.getMessage());
            System.err.println("请确保服务器正在运行在 " + serverAddress + ":" + port);
        } catch (Exception e) {
            System.err.println("客户端错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void closeConnection() {
        running = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            if (userInput != null) userInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("客户端已断开");
    }
}

