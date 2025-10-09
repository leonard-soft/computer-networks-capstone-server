package org.example.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.example.dto.LoginResponseDTO;
import org.example.dto.Request;
import org.example.dto.RequestPayload;
import org.example.service.UserService;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

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
                    UserService userService = new UserService();
                
                    if (line != null) {
                        
                        Gson gson = new Gson();
                        Request req = gson.fromJson(line, Request.class);

                        switch (req.getType()) {
                            case "register":
                                System.out.println("Request type: " + req.getType());
                                RequestPayload requestPayload = gson.fromJson(gson.toJson(req.getPayload()), RequestPayload.class);
                                userService.registerUser(requestPayload);
                                break;
                        
                            case "login":
                                try{
                                    String message;
                                    System.out.println("Request type: " + req.getType());
                                    requestPayload = gson.fromJson(gson.toJson(req.getPayload()), RequestPayload.class);
                                    if(requestPayload == null || requestPayload.username == null){
                                        throw new IllegalArgumentException("Invalid payload: Username is missing");
                                    }
                                    boolean success = userService.loginUser(requestPayload);

                                    if(success){
                                        message = "Login succesful";
                                        userService.updateUserState(requestPayload.username, true);
                                    }else{
                                        message = "Invalid credentials";
                                    }

                                    LoginResponseDTO response = new LoginResponseDTO(success, message);
                                    String jsonResponse = gson.toJson(response) + "\n";
                                    OutputStream out = client.getOutputStream();
                                    out.write(jsonResponse.getBytes());
                                    out.flush();
                                }catch(IOException | JsonSyntaxException | IllegalArgumentException e){
                                    LoginResponseDTO errorResponse = new LoginResponseDTO(false, "Error: " + e.getMessage());
                                    String jsonError = gson.toJson(errorResponse) + "\n";
                                    OutputStream out = client.getOutputStream();
                                    out.write(jsonError.getBytes());
                                    out.flush();
                                }

                                break;
                        
                            default:
                                System.out.println("Unknown request type: " + req.getType());
                        }
                    }
                } 
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                // It could generate a problem, because we need that thread keep working, CGaleano are going to commit it.
                /*
                finally {
                    
                    try {
                        /** 
                         * this can be generate an exception for
                         * this reason this is into the try catch
                         * block
                         *//*
                        client.close();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                */

            }).start();
        }  
    }
}
