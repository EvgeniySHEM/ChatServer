package ru.avalon.javapp.devj130.chatserver;

import ru.avalon.javapp.devj130.chatlibrary.ClientMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Date;

public class MainFrame extends JFrame {
    private final DefaultListModel<ClientThread> clientsList;
    private final JTextArea chatMessages;

    public MainFrame() {
        super("Chat server");
        setBounds(300, 200, 700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        clientsList = new DefaultListModel<>();

        chatMessages = new JTextArea();
        chatMessages.setEditable(false);

        JSplitPane sp = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(new JList<>(clientsList)),
                new JScrollPane(chatMessages)
        );
        add(sp, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                sp.setDividerLocation(0.4);
                try {
                    ServerSockedThread sst = new ServerSockedThread(MainFrame.this);
                    sst.start();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(getContentPane(), ex.getMessage(), "Error starting application",
                            JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }
        });
    }

    public void clientConnected(ClientThread clientThread) {
        SwingUtilities.invokeLater(() -> {
            clientsList.addElement(clientThread);
        });
    }

    public void clientDisconnected(ClientThread clientThread) {
        SwingUtilities.invokeLater(() -> {
            clientsList.removeElement(clientThread);
        });
    }

    public void messageReceived(ClientThread clientThread, String msg) {
        String user = clientThread.toString();
        Date time = new Date();
        SwingUtilities.invokeLater(() -> {
            chatMessages.append(user + " " + time + "\n"
            + msg + "\n\n");
        });
        ClientMessage cliMsg = new ClientMessage(user, time, msg);
        ClientsListRetriever clr = new ClientsListRetriever();
        try {
            SwingUtilities.invokeAndWait(clr);
        } catch (Exception e) {
        }
        for (ClientThread client : clr.clients) {
            client.sendMsg(cliMsg);
        }
    }
    class ClientsListRetriever implements  Runnable {
        ClientThread[] clients;

        @Override
        public void run() {
            clients = new ClientThread[clientsList.size()];
            clientsList.copyInto(clients);
        }
    }

    public static void main(String[] args) {
        new MainFrame().setVisible(true);
    }
}
