package server.serverControl;

import server.serverView.Server;
import util.CharacterUtil;
import util.XMLUtil;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;

/**
 * Created by tonggezhu on 6/22/17.
 */
public class ServerConnection extends Thread {

    private Server server;
    private ServerSocket serverSocket;

    private volatile boolean flag;


    public ServerConnection(Server server, int port) {

        this.server = server;

        try {
            serverSocket = new ServerSocket(port);
            this.flag = true;
            server.getjButton().setText("Stop Server");
            server.getServerStatusJL().setText("Running");

            System.out.println("Is serverSocket closed: " + serverSocket.isClosed());
            System.out.println("Server info: " + serverSocket.getLocalSocketAddress());


        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(server,
                    "Port No. has been occupied", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }


    public void stopServer() {

        flag = false;

        try {

            for (ServerMessageThread smt : server.getUsersList().values()) {

                Socket socket = smt.getSocket();
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                String xmlout = XMLUtil.constructServerCloseXML();
                outputStream.writeUTF(xmlout);
                socket.getOutputStream().close();
                socket.getInputStream().close();

                socket.close();

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
                System.out.println("Is serverSocket closed: " + serverSocket.isClosed());
                System.out.println("Server info: " + serverSocket.getLocalSocketAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while (flag) {

            try {
                Socket socket = serverSocket.accept();

                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

                String loginXML = inputStream.readUTF();
                String username = XMLUtil.extractUsername(loginXML);

                if (!CharacterUtil.isDuplicate(server.getUsersList(), username)) {

                    String xmlout = XMLUtil.constructServerConfirmLoginXML("success");
                    outputStream.writeUTF(xmlout);


                    //create new Thread for chatting

                    ServerMessageThread serverMessageThread = new ServerMessageThread(server, socket, username);


                    this.server.getUsersList().put(username, serverMessageThread);

                    String msg = username + " logs into the Chat room.";

                    System.out.println(msg);

                    String xmlout2 = XMLUtil.constructMessage2All(msg, "SYSTEM");

                    Collection<ServerMessageThread> collection = this.server.getUsersList().values();

                    for (ServerMessageThread smt : collection) {
                        smt.sendMessage(xmlout2);
                    }

                    serverMessageThread.updateUserList();
                    serverMessageThread.start();

                } else {

                    String xmlout2 = XMLUtil.constructServerConfirmLoginXML("fail");
                    outputStream.writeUTF(xmlout2);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
