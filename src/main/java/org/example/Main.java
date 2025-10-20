package org.example;

import org.example.jpa.DatabaseTest;
import org.example.logs.ManageLogs;
import org.example.tcp.TcpService;
import org.example.udp.UdpService;

public class Main {
    public static void main(String[] args) {
        ManageLogs manageLogs = new ManageLogs();

        try {
            // Start UDP Service in a new thread
            UdpService udpService = new UdpService();
            new Thread(udpService::listen).start();

            // Start TCP Service in a new thread, passing the UdpService instance
            TcpService tcpService = new TcpService(5000, udpService);
            new Thread(() -> {
                try {
                    tcpService.run();
                } catch (Exception e) {
                    manageLogs.saveLog("ERROR", "TCP service error: " + e.getMessage());
                }
            }).start();

        } catch (Exception e) {
            manageLogs.saveLog("FATAL", "Failed to start services: " + e.getMessage());
        }
    }
}