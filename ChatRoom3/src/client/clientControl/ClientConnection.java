package client.clientControl;

import client.clientView.ClientChat;
import client.clientView.ClientLogin;
import util.XMLUtil;

import javax.swing.*;
import java.io.*;
import java.net.Socket;


/**
 * Created by tonggezhu on 6/22/17.
 */
public class ClientConnection extends Thread {

    private ClientLogin clientLogin;
    private ClientChat clientChat;

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private String ip;
    private int port;
    private String username;

    private volatile boolean flag;

    public ClientConnection(ClientLogin clientLogin, String username, String ip, int port) {
        this.clientLogin = clientLogin;
        this.ip = ip;
        this.port = port;
        this.username = username;
        connect2Server();
        login();
    }

    public void connect2Server() {
        try {
            this.socket = new Socket(this.ip, this.port);
            this.inputStream = new DataInputStream(this.socket.getInputStream());
            this.outputStream = new DataOutputStream(this.socket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(clientLogin,
                    "Log in failed! \nPlease make sure the server is started " +
                            "\n and you type in the correct port number!", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void login() {
        try {

            String xml = XMLUtil.constructClientLoginXML(this.username);
            outputStream.writeUTF(xml);

            String xmlIn = inputStream.readUTF();

            String content = XMLUtil.extractContent(xmlIn);

            if (content.equals("success")) {
                clientChat = new ClientChat(this, username, port, ip);

                clientLogin.setVisible(false);

                flag = true;

            } else if (content.equals("fail")) {
                JOptionPane.showMessageDialog(clientLogin,
                        "Username has been used", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void sendMessage(String xml) {
        try {

            outputStream.writeUTF(xml);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        loop: while (flag) {

            try {

                //heartbeat，indicate server is online
                socket.sendUrgentData(0xFF);


                String xmlIn2 = inputStream.readUTF();

                String type = XMLUtil.extractType(xmlIn2);

                switch (type) {

                    case "CS2":
                        String msgPublic = XMLUtil.extractUsername(xmlIn2) + ": " + XMLUtil.extractContent(xmlIn2);
                        this.clientChat.getChatJTA().append(msgPublic+ '\n');
                        break;

                    case "CS3":
                        String msgPrivate = XMLUtil.extractMessageSender(xmlIn2) + " to you: " + XMLUtil.extractContent(xmlIn2);

                        this.clientChat.getPrivateChatJTA().append(msgPrivate+ '\n');
                        break;

                    case "CS4":
                        inputStream.close();
                        outputStream.close();
                        socket.close();
                        clientChat.setVisible(false);
                        clientLogin.setVisible(true);
                        flag = false;
                        break loop;

                    case "S5":
                        this.clientChat.setUserlist(XMLUtil.extractUserList(xmlIn2, username));
                        this.clientChat.getUserListJList().setListData(XMLUtil.extractUserList(xmlIn2, username));
                        break;

                    case "S6":

                        JOptionPane.showMessageDialog(clientChat,
                                "Server is shutting down", "Warning", JOptionPane.WARNING_MESSAGE);

                        inputStream.close();
                        outputStream.close();
                        socket.close();
                        clientChat.setVisible(false);
                        clientLogin.setVisible(true);
                        flag = false;
                        break loop;
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(clientChat,
                        "Server is offline", "Warning", JOptionPane.WARNING_MESSAGE);

                try {
                    inputStream.close();
                    outputStream.close();
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                clientChat.setVisible(false);
                clientLogin.setVisible(true);
                flag = false;
//                System.exit(0);
                break loop;

            }
        }
    }
}
