package com.example.okhttp;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;
import java.util.TreeMap;

public class UdpClient {
    public static final int DEST_PORT = 8888;
    public static final String DEST_IP = "192.168.1.23";
    public static final int DATA_LEN = 4096;
    public static byte[] inBuff = new byte[DATA_LEN];
    public static DatagramSocket socket;

    static {
        try {
            socket = new DatagramSocket();
            receivethread();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static DatagramPacket inPacket = new DatagramPacket(inBuff, inBuff.length);
    public static DatagramPacket outPacket = null;

    public static void udpsend(String content) {
        try {
            outPacket = new DatagramPacket(new byte[0], 0, InetAddress.getByName(DEST_IP), DEST_PORT);
            byte[] buff = content.getBytes();
            outPacket.setData(buff);
            socket.send(outPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void receivethread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        socket.receive(inPacket);
                        Log.i("udpreceive", new String(inBuff, 0, inPacket.getLength()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    udpsend("i am from udpclient!");
                }
            }
        }).start();

    }
}