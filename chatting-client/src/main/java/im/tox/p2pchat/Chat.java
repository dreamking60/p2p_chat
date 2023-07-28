package im.tox.p2pchat;

import cn.edu.sustech.cs209.chatting.common.Message;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Chat class.
 */
public class Chat {
    private String clientUser;
    private String chatName;
    private ObservableList<Message> messages;
    private ObservableList<String> participant;
    private List<String> members;

    public boolean isGroupChat = false;

    /**
     * Create a new chat with the given username.
     *
     * @param clientUser the user who is using the client
     * @param chatName the name of the chat
     */
    public Chat(String clientUser, String chatName) {
        this.clientUser = clientUser;
        this.chatName = chatName;
        messages = FXCollections.observableArrayList();
    }

    /**
     * Create a new chat with the given username and participant.
     *
     * @param clientUser the user who is using the client
     * @param chatName the name of the chat
     * @param participant the participant of the chat
     */
    public Chat(String clientUser, String chatName, List<String> participant) {
        this.clientUser = clientUser;
        this.chatName = chatName;
        this.members = participant;
        this.participant = FXCollections.observableArrayList();
        this.participant.addAll(participant);
        messages = FXCollections.observableArrayList();
        isGroupChat = true;
    }


    public String getClientUser() {
        return clientUser;
    }

    public String getChatName() {
        return chatName;
    }

    public ObservableList<String> getParticipant() {
        return participant;
    }

    public ObservableList<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public void addMessage(Long timestamp, String data) {
        messages.add(new Message(timestamp, clientUser, chatName, data));
    }

    public void getMessage(Long timestamp, String data) {
        messages.add(new Message(timestamp, chatName, clientUser, data));
    }

    public void getMessage(Long timestamp, String data, String sendBy) {
        messages.add(new Message(timestamp, sendBy, chatName, data));
    }
}
