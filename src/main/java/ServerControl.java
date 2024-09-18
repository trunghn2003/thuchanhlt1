import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//import udp.client.User;
public class ServerControl {
    private Connection con;
    private DatagramSocket myServer;
    private int serverPort = 5555;
    private DatagramPacket receivePacket = null;
    public ServerControl(){
        getDBConnection("thuchanh1", "root", "trunghn2003");
        openServer(serverPort);
        System.out.println("Server da dc bat tren cong  " + serverPort);
        while(true){
            listenning();
        }
    }
    private void getDBConnection(String dbName, String username, String
            password){

        String dbUrl = "jdbc:mysql://localhost:3307/" + dbName;
        String dbClass = "com.mysql.jdbc.Driver";
        try {
            Class.forName(dbClass);
            con = DriverManager.getConnection (dbUrl, username, password);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    private void openServer(int portNumber){
        try {
            myServer = new DatagramSocket(portNumber);
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    private void listenning(){
        User user = receiveData();
        String result = "false";
        List<User> userList = null;
        if(checkUser(user)){
            result = "ok";
            userList = getAllUsers();
        }
        sendData(result, userList);
    }

    private List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String query = "SELECT * FROM users";
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                userList.add(new User(username, password));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }
    private void sendData(String result,  List<User> userList){
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            if ("ok".equals(result) && userList != null) {
                oos.writeObject(userList);
            } else {
                oos.writeObject(result);
            }

            oos.flush();
            InetAddress IPAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();
            byte[] sendData = baos.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, clientPort);
            myServer.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private User receiveData(){
        User user = null;
        try {

            byte[] receiveData = new byte[1024];

            receivePacket = new
                    DatagramPacket(receiveData, receiveData.length);
            myServer.receive(receivePacket);
            ByteArrayInputStream bais = new
                    ByteArrayInputStream(receiveData);

            ObjectInputStream ois = new ObjectInputStream(bais);
            user = (User)ois.readObject();
            System.out.println("server receive packet: " + user);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    private boolean checkUser(User user) {
        String query = "SELECT * FROM users WHERE username = ? and password = ?";
        try {
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());


            ResultSet rs = pstmt.executeQuery();
            System.out.println(rs);


            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}