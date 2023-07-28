package cn.edu.sustech.cs209.chatting.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Client Handler.
 */
public class ClientHandler implements Runnable {
    private Socket client;
    private String clientId;
    private Server server;

    public ClientHandler(Socket client, Server server) {
        this.client = client;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();

            // Send user list
            sendMessage("GETUSERS:");

            // Read client id
            byte[] id = new byte[20];
            int clientIdLen = in.read(id);
            clientId = new String(id, 0, clientIdLen);
            server.getClientList().put(clientId, client);

            // Update user list
            updateUserList();

            // Read the message from client
            byte[] msg = new byte[1024];
            int msgLen;
            while ((msgLen = in.read(msg)) != -1) {
                String msgStr = new String(msg, 0, msgLen);
                System.out.println("Client: " + msgStr);

                // send message to target client
                sendMessage(msgStr);
            }

        } catch (Exception e) {
            System.out.println("ClientHandler Error.");
        } finally {
            try {
                if (client.equals(server.getClientList().get(clientId))) {
                    server.getClientList().remove(clientId);
                    userLogout();
                }
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Send message to client.
     *
     * @param msg message from client
     * @return
     */
    public boolean sendMessage(String msg) {
        try {
            String Head = msg.split(":")[0];
            if (Head.equals("MSG")) {
                String targetClientId = msg.split(":")[1];
                String msgBody = msg.substring(4 + targetClientId.length() + 1);
                String msgContent = "MSG:" + clientId + ":" + msgBody;

                Socket targetClient = server.getClientList().get(targetClientId);
                if (targetClient == null) {
                    System.out.println("Target Client Not Found.");
                    return false;
                }
                OutputStream out = targetClient.getOutputStream();
                out.write(msgContent.getBytes());
            } else if (Head.equals("GRP")) {
                String msgBody = msg.substring(4);
                // Get users in the group
                String[] users = msgBody.split(":")[2].split(",");
                // send msg to users
                for (String user : users) {
                    if (user.equals(clientId)) {
                        continue;
                    }
                    Socket targetClient = server.getClientList().get(user);
                    if (targetClient == null) {
                        System.out.println("Target Client Not Found.");
                        continue;
                    }
                    OutputStream out = targetClient.getOutputStream();
                    out.write(msg.getBytes());
                    System.out.println("Send to " + user);
                }


            } else if (Head.equals("GETUSERS")) {
                String usersStr = server.getClientList().keySet().stream().collect(Collectors.joining(","));
                String msgContent = "USERS:" + usersStr;
                System.out.println(msgContent);
                OutputStream out = client.getOutputStream();
                out.write(msgContent.getBytes());
            } else if (Head.equals("USER:logout")) {
                server.getClientList().remove(clientId);
                client.close();
            }

            return true;
        } catch (Exception e) {
            System.out.println("Send Message Error.");
            return false;
        }
    }

    private void userLogin() {
        HashMap<String, Socket> clientList = server.getClientList();
        String msg = "USER:" + clientId + ":login";
        System.out.println(msg);
        clientList.forEach((k, v) -> {
            try {
                v.getOutputStream().write(msg.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void userLogout() {
        HashMap<String, Socket> clientList = server.getClientList();
        String msg = "USER:" + clientId + ":logout";
        System.out.println(msg);
        clientList.forEach((k, v) -> {
            try {
                v.getOutputStream().write(msg.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void updateUserList() {
        HashMap<String, Socket> clientList = server.getClientList();
        String userList = "USERS:" + clientList.keySet().stream().collect(Collectors.joining(","));
        System.out.println(userList);
        clientList.forEach((k, v) -> {
            try {
                v.getOutputStream().write(userList.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
