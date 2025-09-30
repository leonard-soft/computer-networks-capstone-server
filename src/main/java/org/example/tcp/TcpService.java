package org.example.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import com.google.gson.Gson;

public class TcpService {

    private int port;

    /**
     * A simple constructor to initialize the tcp 
     * service protocol.
     * 
     * @param port integer value that represents port
     */
    public TcpService(int port) {
        this.port = port;
    }

    /**
     * this function initialize the tcp service
     * and listen the commands from the client.
     * 
     * @throws Exception for ServerSocket Mistakes
     */
    public void run() throws Exception {

        /* this is from build a tcp socket */
        ServerSocket tcpSocket = new ServerSocket(this.port);

        /* showing if the server is running */
        System.out.println("TCP socket: listening in port " + this.port);

        while (true) {

            /* this is blocked waiting a client connection */
            Socket client = tcpSocket.accept();

            System.out.println("Client Connection:" + client.getInetAddress());
            
            /* thread to attend a long quantity of request */
            new Thread(() -> {
                try 
                {
                    /* Reading the client serialized message */
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(client.getInputStream())
                    );

                    /** get the client message */
                    String line = reader.readLine();
                
                    if (line != null) {
                        /*commands block */
                    }
                } 
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally {
                    try {
                        /** 
                         * this can be generate an exception for
                         * this reason this is into the try catch
                         * block
                         */
                        client.close();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

            }).start();
        }  
    }
}
