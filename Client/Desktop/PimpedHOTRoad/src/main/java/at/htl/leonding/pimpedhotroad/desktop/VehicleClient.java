package at.htl.leonding.pimpedhotroad.desktop;

import at.htl.leonding.pimpedhotroad.model.Impulse;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.Socket;

/**
 * @author Bernard Marijanovic
 */
public class VehicleClient implements Serializable {

    public static final String DEFAULT_IP = "10.10.0.1";
    public static final int DEFAULT_PORT = 13730;

    private Socket socket;
    private ObjectOutputStream stream;

    public void send(Impulse impulse) throws IOException {
        if (!socket.isClosed()) {
            stream.writeObject(impulse);
        }else{
            connect(socket.getInetAddress().getHostAddress(), socket.getPort());
        }
    }

    public String getIpAddress() {
        return socket.getInetAddress().toString();
    }

    public void connect(String ipAddress, int port) throws IOException {
        socket = new Socket(ipAddress, port);
        stream = new ObjectOutputStream(socket.getOutputStream());
    }

    public void disconnect() throws IOException {
        send(Impulse.QUIT);
        stream.close();
        socket.close();
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public void reconnect() {
        try {
            disconnect();
            connect(socket.getInetAddress().getHostAddress(), socket.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
