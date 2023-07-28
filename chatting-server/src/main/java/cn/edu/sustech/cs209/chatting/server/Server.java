package cn.edu.sustech.cs209.chatting.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Server class.
 */
public class Server {
    private int port = 8000;
    private ServerSocket server = null;
    private HashMap<String, Socket> clientList;

    /**
     * Server Constructor.
     *
     * @param port socket port
     */
    public Server(int port) {
        this.port = port;
        this.clientList = new HashMap<>();
    }

    /**
     * Server start.
     */
    public void start() {
        try {
            server = new ServerSocket(port);
            System.out.println("Server init!");

            while (true) {
                Socket client = server.accept();
                System.out.println("Client " + client + " connected.");
                new Thread(new ClientHandler(client, this)).start();
            }

        } catch (IOException e) {
            System.out.println("Server Error 1.");
        } finally {
            try {
                if (server != null) {
                    server.close();
                }
            } catch (IOException e) {
                System.out.println("Server Error 2.");
            }
        }
    }

    public void sendOfflineMessage() {
        for (Socket socket : getClientList().values()) {
            try {
                OutputStream out = socket.getOutputStream();
                out.write("SERVER:close".getBytes());
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        getClientList().clear();
    }


    public HashMap<String, Socket> getClientList() {
        return clientList;
    }

}
