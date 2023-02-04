package ru.avalon.javapp.devj130.chatserver;

import ru.avalon.javapp.devj130.chatlibrary.Constants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSockedThread extends Thread {
    private final MainFrame mainFrame;
    private final ServerSocket serverSocket;

    public ServerSockedThread(MainFrame mainFrame) throws IOException {
        this.mainFrame = mainFrame;
        this.serverSocket = new ServerSocket(Constants.SERVER_PORT);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket client = serverSocket.accept();
                ClientThread clientThread = new ClientThread(mainFrame, client);
                mainFrame.clientConnected(clientThread);
                //TODO: notify mainframe about new client
                clientThread.start();
            } catch (IOException e) {

            }
        }
    }

}
