package util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * Created by tonggezhu on 6/20/17.
 */
public class XMLUtil {

    /*
    * Client to Server
    * login CS1
    * message2all CS2
    * message2client CS3
    * clientquit CS4
    *
    * Server to Client
    * confirm login CS1
    * message2all CS2
    * message2client CS3
    * confirm clientquit CS4
    * usersList S5
    * server quit S6
    */

    private static Document constructXMLDocument() {

        Document document = DocumentHelper.createDocument();
        Element root = DocumentHelper.createElement("message");

        document.setRootElement(root);

        return document;
    }

    public static String extractUsername(String xml) {

        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new StringReader(xml));
            Element user = document.getRootElement().element("user");
            return user.getText();

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String extractType(String xml) {

        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new StringReader(xml));
            Element type = document.getRootElement().element("type");
            return type.getText();

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String extractContent(String xml) {
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new StringReader(xml));
            Element content = document.getRootElement().element("content");
            if (content != null) {
                return content.getText();
            } else return "";
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String constructClientLoginXML(String username) {
        Document document = constructXMLDocument();

        Element root = document.getRootElement();

        Element type = root.addElement("type");
        type.setText("CS1");

        Element user = root.addElement("user");
        user.setText(username);

        return document.asXML();

    }

    public static String constructServerConfirmLoginXML(String str) {
        Document document = constructXMLDocument();

        Element root = document.getRootElement();

        Element type = root.addElement("type");
        type.setText("CS1");
        Element message = root.addElement("content");
        message.setText(str);

        return document.asXML();

    }


    public static String constructUserList(Set<String> users) {
        Document document = constructXMLDocument();

        Element root = document.getRootElement();
        Element type = root.addElement("type");
        type.setText("S5");

        for (String user : users) {
            Element u = root.addElement("user");
            u.setText(user);
        }
        return document.asXML();
    }

    public static String[] extractUserList(String xml, String username) {

        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(new StringReader(xml));
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        List<String> list = new ArrayList<>();
        for (Iterator<Element> iterator = document.getRootElement().elementIterator("user"); iterator.hasNext(); ) {
            Element e = iterator.next();
            list.add(e.getText());
//            if (!e.getText().equals(username)) {
//                list.add(e.getText());
//            }
        }
        String[] usersList = new String[list.size()];
        usersList = list.toArray(usersList);
        return usersList;
    }


    public static String constructMessage2All(String msg, String username) {
        Document document = constructXMLDocument();

        Element root = document.getRootElement();

        Element type = root.addElement("type");
        type.setText("CS2");

        Element user = root.addElement("user");
        user.setText(username);

        Element message = root.addElement("content");
        message.setText(msg);

        return document.asXML();
    }

    public static String timeTrack(String username) {
        Document document = constructXMLDocument();

        Element root = document.getRootElement();

        Element type = root.addElement("type");
        type.setText("CST");

        Element user = root.addElement("user");
        user.setText(username);
        return document.asXML();
    }



    public static String constructMessageWisper(String msg, String sender, String receiver) {
        Document document = constructXMLDocument();

        Element root = document.getRootElement();

        Element type = root.addElement("type");
        type.setText("CS3");

        Element userSend = root.addElement("sender");
        userSend.setText(sender);

        Element userRev = root.addElement("receiver");
        userRev.setText(receiver);

        Element content = root.addElement("content");
        content.setText(msg);

        return document.asXML();
    }

    public static String extractMessageSender(String xml) {
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new StringReader(xml));
            Element sender = document.getRootElement().element("sender");
            return sender.getText();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String extractMessageReceiver(String xml) {
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new StringReader(xml));
            Element receiver = document.getRootElement().element("receiver");
            return receiver.getText();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String constructClientQuitXML(String username) {
        Document document = constructXMLDocument();
        Element root = document.getRootElement();
        Element user = root.addElement("user");
        user.setText(username);
        Element type = root.addElement("type");
        type.setText("CS4");
        return document.asXML();
    }

    public static String constructServerCloseXML() {
        Document document = constructXMLDocument();
        Element root = document.getRootElement();
        Element type = root.addElement("type");
        type.setText("S6");
        return document.asXML();
    }
}
