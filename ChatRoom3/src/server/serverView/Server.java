package server.serverView;

import server.serverControl.ServerConnection;
import server.serverControl.ServerMessageThread;
import util.CharacterUtil;


import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tonggezhu on 6/22/17.
 */
public class Server extends JFrame {

    private JLabel jLabel1;
    private JLabel serverStatusJL;
    private JLabel jLabel3;

    private JButton jButton;

    private JPanel serverInforJP1;
    private JPanel serverInforJP2;
    private JPanel userListJP;

    private JScrollPane jScrollPane;

    private JTextArea userlistJT;

    private JTextField portJF;

    private ServerConnection serverConnection;

    private Map<String, ServerMessageThread> usersList = new HashMap<>(); //<username, port>

    public Server() {


        initComponents();
    }

    public void initComponents() {

        serverInforJP1 = new JPanel();
        serverInforJP2 = new JPanel();
        userListJP = new JPanel();

        jLabel1 = new JLabel("Server Status: ");
        serverStatusJL = new JLabel("Disconnect");
        serverStatusJL.setForeground(new java.awt.Color(245, 0, 0));

        jLabel3 = new JLabel("Port No.");

        portJF = new JTextField(10);
        jButton = new JButton("Start Server");
        jButton.addActionListener(new InitServerButtonListener());

        userlistJT = new JTextArea();
        userlistJT.setEditable(false);
        userlistJT.setColumns(25);
        userlistJT.setRows(7);
        userlistJT.setForeground(new java.awt.Color(0, 0, 245));

        serverInforJP1.add(jLabel1);
        serverInforJP1.add(serverStatusJL);
        serverInforJP2.add(jLabel3);
        serverInforJP2.add(portJF);
        serverInforJP2.add(jButton);

        jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(userlistJT);

        serverInforJP1.setBorder(BorderFactory.createTitledBorder("Server Information"));
        serverInforJP2.setBorder(BorderFactory.createTitledBorder(""));
        userListJP.setBorder(BorderFactory.createTitledBorder("Online Users List"));

        userListJP.add(jScrollPane);

        this.getContentPane().add(BorderLayout.NORTH, serverInforJP1);
        this.getContentPane().add(BorderLayout.CENTER, serverInforJP2);

        this.getContentPane().add(BorderLayout.SOUTH, userListJP);

        this.setTitle("Server");
        this.setResizable(false);
        this.pack();
        this.setVisible(true);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (serverStatusJL.getText().equals("Disconnect")) {
                    System.exit(0);
                } else {
                    serverConnection.stopServer();
                    System.exit(0);
                }
            }
        });
    }

    private class InitServerButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {


            if (jButton.getText().equals("Start Server")) {
                String hostPort = portJF.getText();


                if (CharacterUtil.isEmpty(hostPort)) {
                    JOptionPane.showMessageDialog(Server.this,
                            "Please enter the Port number", "Warning", JOptionPane.WARNING_MESSAGE);
                    Server.this.portJF.requestFocus();
                    return;
                }

                if (!CharacterUtil.isNumber(hostPort)) {
                    JOptionPane.showMessageDialog(Server.this,
                            "Port No. must be numbers", "Warning", JOptionPane.WARNING_MESSAGE);
                    Server.this.portJF.requestFocus();
                    return;
                }

                if (!CharacterUtil.isPortCorrect(hostPort)) {
                    JOptionPane.showMessageDialog(Server.this,
                            "Port No. must between 1024 and 65535", "Warning", JOptionPane.WARNING_MESSAGE);
                    Server.this.portJF.requestFocus();
                    return;
                }

                int port = Integer.valueOf(hostPort);

                serverConnection = new ServerConnection(Server.this, port);
                serverConnection.start();

            } else {
                serverConnection.stopServer();
                jButton.setText("Start Server");
                serverStatusJL.setText("Disconnect");

            }
        }
    }

    public JLabel getServerStatusJL() {
        return serverStatusJL;
    }

    public JButton getjButton() {
        return jButton;
    }

    public JTextArea getUserlistJT() {
        return userlistJT;
    }

    public Map<String, ServerMessageThread> getUsersList() {
        return usersList;
    }

    public static void main(String[] args) {

//        try {
//            UIManager.setLookAndFeel(new MetalLookAndFeel());
//            new Server();
//        } catch (UnsupportedLookAndFeelException e) {
//            e.printStackTrace();
//        }
        new Server();

    }

}
