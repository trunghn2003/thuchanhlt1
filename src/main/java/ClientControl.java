import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientControl {
    private int serverPort = 5555;
    private int clientPort = 6666;
    private String serverHost = "localhost";
    private DatagramSocket myClient;

    public ClientControl() {
    }

    public void openConnection() {
        try {

            myClient = new DatagramSocket(clientPort);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void closeConnection() {
        try {

            myClient.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendData(User user) {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(user);
            oos.flush();
            InetAddress IPAddress =

                    InetAddress.getByName(serverHost);
            byte[] sendData = baos.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(sendData,

                    sendData.length, IPAddress, serverPort);
            System.out.println("sendPacket "+ sendPacket.getData());

            myClient.send(sendPacket);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Object receiveData() {
        Object result = null;
        try {
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            myClient.receive(receivePacket);
            ByteArrayInputStream bais = new ByteArrayInputStream(receiveData);
            ObjectInputStream ois = new ObjectInputStream(bais);
            result = ois.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
}