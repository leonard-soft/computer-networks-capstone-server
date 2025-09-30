package org.example;

import org.example.jpa.DatabaseTest;
import org.example.tcp.TcpService;

public class Main {
    public static void main(String[] args) {
        
        DatabaseTest databaseTest = new DatabaseTest();
        databaseTest.databaseTest();   

        TcpService tcpService = new TcpService(5000);

        /**
         * This is the tcp service thread
         * with this we can receive messages
         * and send responses.
         */
        new Thread(() -> {
            try {
                tcpService.run();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }
}