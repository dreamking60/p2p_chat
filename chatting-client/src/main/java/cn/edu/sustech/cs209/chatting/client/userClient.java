package cn.edu.sustech.cs209.chatting.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

/**.
 * user client class
 */
public class userClient implements Runnable {
    private String username;
    private Socket client;
    private Controller controller;
    private String[] users;

    /**.
     * constructor
     *
     * @param client client
     * @param username unique id of client
     * @param controller controller of window
     */
    public userClient(Socket client, String username, Controller controller) {
        this.client = client;
        this.username = username;
        this.controller = controller;
    }

    /**
     * run method.
     */
    @Override
    public void run() {
        try {
            InputStream in = client.getInputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) != -1) {
                String msg = new String(buf, 0, len);
                System.out.println(msg);
                getMessage(msg);
            }


        } catch (Exception e) {
            System.out.println("Client Error");
        }
    }

    /**.
     * receive message from server
     *
     * @param msg message
     */
    public boolean getMessage(String msg) {
        try {
            String head = msg.split(":")[0];
            if (head.equals("MSG")) {
                String sendBy =  msg.split(":")[1];
                String msgBody = msg.substring(4 + sendBy.length() + 1);
                Platform.runLater(() -> controller.updateMsg(sendBy, msgBody));

            } else if (head.equals("USERS")) {
                String userList = msg.substring(6);
                users = userList.split(",");
                Platform.runLater(() -> controller.updateUsers(users));

            } else if (head.equals("GRP")) {
                String sendBy = msg.split(":")[1];
                String sendToGroup = msg.split(":")[2];
                String[] participant = msg.split(":")[3].split(",");
                String msgBody = msg.substring(4 + sendBy.length() + 1 + sendToGroup.length() + 1 + msg.split(":")[3].length() + 1);
                Platform.runLater(() -> controller.updateMsg(sendBy, msgBody, sendToGroup, participant));

            } else if (head.equals("USER")) {
                String sendBy = msg.split(":")[1];
                String cond = msg.split(":")[2];
                if (cond.equals("login")) {
                    Platform.runLater(() -> controller.userLogin(sendBy));
                } else if (cond.equals("logout")) {
                    Platform.runLater(() -> controller.userLogout(sendBy));
                }

            } else if (head.equals("SERVER")) {
                String cond = msg.split(":")[1];
                if (cond.equals("close")) {
                    Platform.runLater(() -> controller.serverClose());
                }
            }

            return true;
        } catch (Exception e) {
            System.out.println("Get Message Error.");
            return false;
        }
    }

}
