package ClientServer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class UserDataBase  {
    private final ArrayList<User> users;
    private String fileName = "chat_users.xml";

    public UserDataBase() {
        this.users = new ArrayList<>();
    }

    public void addUser(String name, String password) throws UserDataBaseException {
        User user = new User(name, password);
//        System.out.println(name + " " + password);

        if (users.contains(user))
            throw new UserDataBaseException("User already exists.");

        users.add(user);
    }

    public User getUser(String name) throws UserDataBaseException {
        Optional<User> user = users.stream().filter(x -> x.getName().equals(name)).findFirst();

        if (user.isEmpty())
            throw new UserDataBaseException("User not found.");

        return user.get();
    }

    public boolean hasUser(String name) {
        try {
            return getUser(name) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public void load() throws ParserConfigurationException, UserDataBaseException, IOException, SAXException {
        users.clear();

        File file = new File(fileName);

        DocumentBuilderFactory database = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = database.newDocumentBuilder();
        Document doc = builder.parse(file);

        Element root = doc.getDocumentElement();

        if (root == null) return;

        root.normalize();

        NodeList nodeList = root.getElementsByTagName("user");

        for (int i=0;i< nodeList.getLength();i++) {
            Node node = nodeList.item(i);
            addUser(node.getAttributes().getNamedItem("name").getNodeValue(), node.getAttributes().getNamedItem("password").getNodeValue());
        }
    }

    public void save() throws ParserConfigurationException, TransformerException, FileNotFoundException {
        File file = new File(fileName);

        DocumentBuilderFactory database = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = database.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement("user-list");

        for (User user: users) {
            Element element = doc.createElement("user");
            element.setAttribute("name", user.getName());
            element.setAttribute("password", user.getPassword());
            root.appendChild(element);
        }

        doc.appendChild(root);

        FileOutputStream output = new FileOutputStream(file);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);

        transformer.transform(source, result);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ArrayList<User> getUsers() {
        return users;
    }
}
