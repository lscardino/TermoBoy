package com.example.termoboy;

import org.apache.commons.net.time.TimeTCPClient;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.CountDownLatch;

public class MiraHoraInternet {
    private DateFormat dateFormat;
    private String resultado = "1997-07-07";
    private CountDownLatch countDownLatch;

    public MiraHoraInternet() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public MiraHoraInternet(String format) {
        dateFormat = new SimpleDateFormat(format);
    }

    public String getDate() {
        countDownLatch = new CountDownLatch(1);
        ThreadConnectURL connectURL = new ThreadConnectURL("Thread connect URL");
        connectURL.start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    private class ThreadConnectURL extends Thread {

        public ThreadConnectURL(String name) {
            super(name);
        }

        @Override
        public void run() {
            try {
                TimeTCPClient client = new TimeTCPClient();
                try {
                    client.setDefaultTimeout(30000);
                    client.connect("time.nist.gov");
                    resultado = dateFormat.format(client.getDate());
                } finally {
                    client.disconnect();
                    countDownLatch.countDown();
                }

            } catch (IOException e) {
                resultado = "1997-07-07";
                countDownLatch.countDown();
            }
        }
    }
}
