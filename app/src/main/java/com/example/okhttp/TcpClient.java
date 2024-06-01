package com.example.okhttp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

public class TcpClient {

    public static String ip = "192.168.1.23";
    public static int port = 9999;
    public static boolean connected = false;
    public static Socket socket = null;
    public static OutputStream outputstream = null;
    public static InputStream inputStream = null;
    public static long lastheartresponse = 0;

    public static void start() {
        servicethread();
    }

    public static Long getTimestamp() {
        Date date = new Date();
        if (null == date) {
            return (long) 0;
        }
        String timestamp = String.valueOf(date.getTime());
        return Long.valueOf(timestamp);
    }

    public static void close() {
        try {
            if (socket != null) {
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        inputStream = null;
        outputstream = null;
        connected = false;
    }

    public static void sendmsg(final String msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (connected == false) {
                } else {
                    try {
                        if (outputstream != null) {
                            String crypt = msg;
                            outputstream.write(crypt.getBytes("utf-8"));
                            outputstream.flush();
                        }

                    } catch (IOException e) {
                        close();
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    public static void heartthread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    long currenttime = getTimestamp();
                    if (lastheartresponse != 0) {
                        long offset = currenttime - lastheartresponse;
                        int seconds = (int) (offset / 1000);
                        if (seconds > 10) {
                            close();
                        }
                    }
                    try {
                        Thread.currentThread().sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();
    }

    public static void receivethread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int arraysize = 1024;
                byte[] content = new byte[arraysize];
                while (true) {
                    if (inputStream != null) {
                        try {
                            int count = inputStream.read(content);
                            if (count > 0 && count < arraysize) {
                                byte[] tmparray = new byte[count];
                                System.arraycopy(content, 0, tmparray, 0, count);
                                String str = new String(tmparray, "utf-8");

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            close();
                            break;
                        }
                    } else {
                        close();
                        break;
                    }
                }
            }
        }).start();
    }

    public static void servicethread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                heartthread();
                while (true) {
                    if (connected == false) {
                        try {
                            socket = new Socket(ip, port);
                            socket.setSoTimeout(10*1000);
                            connected = true;
                            outputstream = socket.getOutputStream();
                            inputStream = socket.getInputStream();
                            receivethread();
                        } catch (IOException e) {
                            e.printStackTrace();
                            connected = false;
                            socket = null;
                            outputstream = null;
                            inputStream = null;
                        }
                    }
                    if (outputstream != null) {
                        try {
                            JSONObject object = new JSONObject();
                            object.put("msgtype", "heart");
                            sendmsg(object.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.currentThread().sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }
}
