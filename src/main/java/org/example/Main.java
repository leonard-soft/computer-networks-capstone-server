package org.example;

import org.example.jpa.DatabaseTest;
import org.example.logs.ManageLogs;
import org.example.tcp.TcpService;

public class Main {
    public static void main(String[] args) {
        ManageLogs manageLogs = new ManageLogs();
        // DatabaseTest databaseTest = new DatabaseTest();
        //databaseTest.databaseTest();

        TcpService tcpService = new TcpService(5000);

        /**
         * This is the tcp service thread
         * with this we can receive messages
         * and send responses.
         */
        new Thread(() -> {
            try {
                manageLogs.saveLog("INFO", "TCP service started");
                tcpService.run();
            }catch (Exception e) {
                manageLogs.saveLog("ERROR", "TCP service error: " + e.getMessage());
            }
        }).start();

    }
}