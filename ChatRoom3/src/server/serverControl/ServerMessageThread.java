package server.serverControl;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import server.serverView.Server;
import util.XMLUtil;

import java.io.*;
import java.net.Socket;
import java.util.Collection;

import java.util.Set;

/**
 * Created by tonggezhu on 6/22/17.
 */
public class ServerMessageThread extends Thread {

    private Server server;
    private Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private volatile boolean flag = false;
    private String username;

    private String xmlMessgeIn;

    public ServerMessageThread(Server server, Socket socket, String username) {
        this.server = server;
        this.socket = socket;
        this.username = username;
        try {
            dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
            dataInputStream = new DataInputStream(this.socket.getInputStream());
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateUserList() {
        Set<String> users = this.server.getUsersList().keySet();

        String str = "";

        for (String user : users) {
            str += user + '\n';
        }

        this.server.getUserlistJT().setText(str);

        String xml = XMLUtil.constructUserList(users);

        Collection<ServerMessageThread> collection = this.server.getUsersList().values();

        for (ServerMessageThread smt : collection) {
            smt.sendMessage(xml);

        }
    }

    public void sendMessage(String xml) {

        try {
            dataOutputStream.writeUTF(xml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        loop:
        while (flag) {

            try {

                socket.sendUrgentData(0xFF);

                xmlMessgeIn = dataInputStream.readUTF();

                String type = XMLUtil.extractType(xmlMessgeIn);

                switch (type) {

                    case "CS2":

                        for (ServerMessageThread smt : server.getUsersList().values()) {

                            smt.dataOutputStream.writeUTF(xmlMessgeIn);

                        }
                        break;

                    case "CS3":
                        String username = XMLUtil.extractMessageReceiver(xmlMessgeIn);
                        server.getUsersList().get(username).dataOutputStream.writeUTF(xmlMessgeIn);
                        break;

                    case "CS4":
                        String username4 = XMLUtil.extractUsername(xmlMessgeIn);

                        ServerMessageThread smt = this.server.getUsersList().get(username4);

                        smt.dataOutputStream.writeUTF(xmlMessgeIn);

                        smt.dataOutputStream.close();
                        smt.dataInputStream.close();
                        smt.socket.close();
                        flag = false;

                        server.getUsersList().remove(username4);

                        String msg = username4 + " left the chat room.";

                        String xmlOut = XMLUtil.constructMessage2All(msg, "SYSTEM");

                        System.out.println(xmlOut);
                        for (ServerMessageThread smt2 : server.getUsersList().values()) {
                            smt2.dataOutputStream.writeUTF(xmlOut);
                        }

                        updateUserList();

                        break loop;
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                ServerMessageThread serverMessageThread = server.getUsersList().get(username);


                try {
                    serverMessageThread.dataOutputStream.close();
                    serverMessageThread.dataInputStream.close();
                    serverMessageThread.socket.close();
                    server.getUsersList().remove(username);

                    String msg = username + " is offline.";

                    String xmlOut = XMLUtil.constructMessage2All(msg, "SYSTEM");

                    for (ServerMessageThread smt2 : server.getUsersList().values()) {
                        smt2.dataOutputStream.writeUTF(xmlOut);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                flag = false;


                updateUserList();

                System.out.println(username + " is offline");

                break loop;

            }

        }
    }

    public Socket getSocket() {
        return socket;
    }

}
