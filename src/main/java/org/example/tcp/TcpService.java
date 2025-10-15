package org.example.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.example.dto.*;
import org.example.dto.LoginResponseDTO;
import org.example.dto.RegisterResponseDTO;
import org.example.dto.Request;
import org.example.dto.RequestPayload;
import org.example.service.RoomService;
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

        /* This while loop is for the creation of new threads to handle new clients*/
        while (true) {

            /* this is blocked waiting a client connection */
            Socket client = tcpSocket.accept();

            System.out.println("Client Connection:" + client.getInetAddress());

            /* thread to attend a long quantity of request */
            new Thread(() -> {
                String username = null;

                try {
                    OutputStream out = client.getOutputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    Gson gson = new Gson();
                    UserService userService = new UserService();
                    RoomService  roomService = new RoomService();

                    while (true) {
                        String line = reader.readLine();
                        if (line == null) { // Disconnected client
                            if (username != null) {
                                userService.updateUserState(username, false);
                            }
                            break;
                        }

                        Request req = gson.fromJson(line, Request.class);
                        switch (req.getType()) {
                            case "register":
                                System.out.println("Request type: " + req.getType());
                                RequestPayload requestPayload = gson.fromJson(gson.toJson(req.getPayload()), RequestPayload.class);
                                userService.registerUser(requestPayload);

                                try {
                                    String message = "register completed succesfully";
                                    RegisterResponseDTO register = new RegisterResponseDTO(true, message);
                                    String jsonResponse = gson.toJson(register) + "\n";
                                    client.getOutputStream();
                                    out.write(jsonResponse.getBytes());
                                    out.flush();
                                } catch (Exception e) {
                                    RegisterResponseDTO errorResponse = new RegisterResponseDTO(false, "Error: " + e.getMessage());
                                    String jsonError = gson.toJson(errorResponse) + "\n";
                                    client.getOutputStream();
                                    out.write(jsonError.getBytes());
                                    out.flush();
                                }
                                break;
                            case "login":
                                try {
                                    String message;
                                    System.out.println("Request type: " + req.getType());
                                    requestPayload = gson.fromJson(gson.toJson(req.getPayload()), RequestPayload.class);
                                    if (requestPayload == null || requestPayload.username == null) {
                                        throw new IllegalArgumentException("Invalid payload: Username is missing");
                                    }
                                    boolean success = userService.loginUser(requestPayload);
                                    if (success) {
                                        message = "Login successful";
                                        username = requestPayload.username;
                                        userService.updateUserState(username, true);
                                    } else {
                                        message = "Invalid credentials";
                                    }
                                    LoginResponseDTO response = new LoginResponseDTO(success, message);
                                    String jsonResponse = gson.toJson(response) + "\n";
                                    out.write(jsonResponse.getBytes());
                                    out.flush();
                                } catch (IOException | JsonSyntaxException | IllegalArgumentException e) {
                                    LoginResponseDTO errorResponse = new LoginResponseDTO(false, "Error: " + e.getMessage());
                                    String jsonError = gson.toJson(errorResponse) + "\n";
                                    out.write(jsonError.getBytes());
                                    out.flush();
                                }

                                break;
                            case "get_online_users":
                                try {
                                    System.out.println("Request type: " + req.getType());
                                    List<String> onlineUsers = userService.getOnlineUsers();
                                    ConnectedUsersResponseDTO response = new ConnectedUsersResponseDTO(onlineUsers.toArray(new String[0]));
                                    String jsonResponse = gson.toJson(response) + "\n";
                                    out.write(jsonResponse.getBytes());
                                    out.flush();
                                } catch (IOException | RuntimeException e) {
                                    ConnectedUsersResponseDTO errorResponse = new ConnectedUsersResponseDTO(new String[0]);
                                    String jsonError = gson.toJson(errorResponse) + "\n";
                                    out.write(jsonError.getBytes());
                                    out.flush();
                                }
                                break;

                            case "create_game":
                                try {
                                    if (username == null) throw new IllegalArgumentException("Must be logged in");
                                    GameDTO game = roomService.createGameAndRegisterHost(username);
                                    RegisterResponseDTO response = new RegisterResponseDTO(true, "Game created: " + game.getGame_id());
                                    String jsonResponse = gson.toJson(response) + "\n";
                                    out.write(jsonResponse.getBytes());
                                    out.flush();
                                } catch (Exception e) {
                                    RegisterResponseDTO errorResponse = new RegisterResponseDTO(false, "Error: " + e.getMessage());
                                    String jsonError = gson.toJson(errorResponse) + "\n";
                                    out.write(jsonError.getBytes());
                                    out.flush();
                                }
                                break;

                            default:
                                System.out.println("Unknown request type: " + req.getType());
                        }
                    }
                } catch (IOException | JsonSyntaxException e) {
                    if (username != null) {
                        UserService  userService = new UserService();
                        userService.updateUserState(username, false);
                    }
                } finally {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
