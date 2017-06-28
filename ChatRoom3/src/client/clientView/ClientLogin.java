package client.clientView;

import client.clientControl.ClientConnection;
import server.serverView.Server;
import util.CharacterUtil;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by tonggezhu on 6/22/17.
 */
public class ClientLogin extends JFrame {

    private JPanel titleJP;
    private JPanel jPanel;

    private JLabel titleJL;
    private JLabel usernameJL;
    private JLabel ipJL;
    private JLabel portNoJL;

    private JTextField usernameJF;
    private JTextField ipJF;
    private JTextField portNoJF;

    private JButton loginJB;
    private JButton resetJB;

    private ClientConnection clientConnection;

    public ClientLogin() {
        clientLoginInitComponents();
    }

    private void clientLoginInitComponents() {

        jPanel = new JPanel();
        jPanel.setBorder(BorderFactory.createTitledBorder("User Login"));

        titleJP = new JPanel();
        titleJL = new JLabel("CS513 Chat room");
        titleJL.setFont(new java.awt.Font("Dialog", 1, 15));
        titleJL.setForeground(Color.blue);
        titleJP.add(titleJL);

        usernameJL = new JLabel("Username");
        ipJL = new JLabel("IP Address");
        portNoJL = new JLabel("Port No.");

        usernameJL.setPreferredSize(new Dimension(80, 10));
        ipJL.setPreferredSize(new Dimension(80, 10));
        portNoJL.setPreferredSize(new Dimension(80, 10));

        loginJB = new JButton("Login");
        resetJB = new JButton("Reset");

        loginJB.addActionListener(new LoginButtonListener());
        resetJB.addActionListener(new ResetButtonListener());

        usernameJF = new JTextField(15);
        ipJF = new JTextField(15);
        portNoJF = new JTextField(15);

        ipJF.setText("localhost");
        portNoJF.setText("5555");


        jPanel.add(usernameJL);
        jPanel.add(usernameJF);
        jPanel.add(ipJL);
        jPanel.add(ipJF);
        jPanel.add(portNoJL);
        jPanel.add(portNoJF);
        jPanel.add(loginJB);
        jPanel.add(resetJB);

        this.getContentPane().add(BorderLayout.NORTH, titleJP);

        this.getContentPane().add(BorderLayout.CENTER, jPanel);
        this.setSize(300, 200);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("User Login");
        this.setAlwaysOnTop(true);
        this.setResizable(false);
        this.setVisible(true);


    }

    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = ClientLogin.this.usernameJF.getText();
            String port = ClientLogin.this.portNoJF.getText();
            String ip = ClientLogin.this.ipJF.getText();

            if (CharacterUtil.isEmpty(username)) {
                JOptionPane.showMessageDialog(ClientLogin.this,
                        "Please enter username", "Warning", JOptionPane.WARNING_MESSAGE);
                usernameJF.requestFocus();
                return;
            }

            if (!CharacterUtil.isUsernameLegal(username)) {
                JOptionPane.showMessageDialog(ClientLogin.this,
                        "The length of username must be larger than 3 and only consisted of number and characters", "Warning", JOptionPane.WARNING_MESSAGE);
                usernameJF.requestFocus();
                return;
            }

            if (CharacterUtil.isEmpty(ip)) {
                JOptionPane.showMessageDialog(ClientLogin.this,
                        "Please enter IP address", "Warning", JOptionPane.WARNING_MESSAGE);
                ipJF.requestFocus();
                return;
            }

            if (!CharacterUtil.isIPLegal(ip)) {
                JOptionPane.showMessageDialog(ClientLogin.this,
                        "IP address is illegal", "Warning", JOptionPane.WARNING_MESSAGE);
                ipJF.requestFocus();
                return;
            }

            if (CharacterUtil.isEmpty(port)) {
                JOptionPane.showMessageDialog(ClientLogin.this,
                        "Please enter the Port number", "Warning", JOptionPane.WARNING_MESSAGE);
                portNoJF.requestFocus();
                return;
            }

            if (!CharacterUtil.isPortCorrect(port)) {
                JOptionPane.showMessageDialog(ClientLogin.this,
                        "Port No. must be numbers", "Warning", JOptionPane.WARNING_MESSAGE);
                portNoJF.requestFocus();
                return;
            }

            int p = Integer.valueOf(port);

            clientConnection = new ClientConnection(ClientLogin.this, username, ip, p);
            clientConnection.start();

        }
    }

    private class ResetButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ClientLogin.this.usernameJF.setText("");
            ClientLogin.this.portNoJF.setText("");
            ClientLogin.this.ipJF.setText("");
        }
    }

    public static void main(String[] args) {

        new ClientLogin();

//        try {
//            UIManager.setLookAndFeel(new MetalLookAndFeel());
//            new ClientLogin();
//        } catch (UnsupportedLookAndFeelException e) {
//            e.printStackTrace();
//        }
    }

}
