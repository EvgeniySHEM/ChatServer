package ru.avalon.javapp.devj130.chatserver;

import ru.avalon.javapp.devj130.chatlibrary.ClientMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientThread extends Thread{
    private final MainFrame mainFrame;
    private final Socket socket;
    private final String userName;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;

    public ClientThread(MainFrame mainFrame, Socket socket) throws IOException {
        this.mainFrame = mainFrame;
        this.socket = socket;

        this.inputStream = new ObjectInputStream(socket.getInputStream());
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.userName = this.inputStream.readUTF();
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String msg = inputStream.readUTF();
                mainFrame.messageReceived(this, msg);
            } catch (IOException e) {
                break;
            }
        }

        mainFrame.clientDisconnected(this);

        try {
            socket.close();
        } catch (IOException e) {
        }
    }

    @Override
    public String toString() {
        return userName + '@' + socket.getInetAddress();
    }

    public void sendMsg(ClientMessage cliMsg) {
        try {
            outputStream.writeObject(cliMsg);
        } catch (IOException e) {
        }
    }
}
