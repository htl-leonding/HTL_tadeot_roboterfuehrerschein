package at.htl.leonding.pimpedhotroad.android;

import at.htl.leonding.pimpedhotroad.model.Impulse;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;


/**
 * Client class for the vehicle.
 *
 * @author Bernard Marijanovic
 */
public class VehicleClient implements Serializable {

    private static VehicleClient INSTANCE;

    public static final String DEFAULT_IP = "10.10.0.1";
    public static final int DEFAULT_PORT = 13730;

    private Socket socket;
    private ObjectOutputStream stream;

    private VehicleClient() {
    }

    public synchronized static VehicleClient getInstance() {
        if (INSTANCE == null)
            INSTANCE = new VehicleClient();
        return INSTANCE;
    }

    /**
     * Initializes the singleton instance.
     *
     * @param ipAddress The address to connect to.
     * @throws IOException
     */
    public synchronized void initializeClient(String ipAddress, int port) throws IOException {
        socket = new Socket(ipAddress, port);
        stream = new ObjectOutputStream(socket.getOutputStream());
    }

    /**
     * Destroys the single instance of the client.
     *
     * @throws IOException
     */
    public synchronized static void destroyInstance() throws IOException {
        INSTANCE.send(Impulse.QUIT);
        INSTANCE.stream.close();
        INSTANCE.socket.close();
        INSTANCE = null;
    }

    /**
     * Function to send impulses to the car in order to control it.
     *
     * @param impulse Impulse to send to the car server.
     */
    public void send(Impulse impulse) throws IOException {
        System.out.println(impulse);
        stream.writeObject(impulse);
    }


    /**
     * Function to check if the client is initialized.
     *
     * @return Boolean wether the client is initialized or not.
     */
    public boolean isInitialized() {
        return socket != null && socket.isConnected();
    }

    /**
     * Returns the IP address of the server.
     *
     * @return IP adress as string
     */
    public String getIpAddress() {
        return socket.getInetAddress().toString();
    }
}
