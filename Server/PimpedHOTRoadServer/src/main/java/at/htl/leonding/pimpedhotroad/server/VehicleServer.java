package at.htl.leonding.pimpedhotroad.server;

import at.htl.leonding.pimpedhotroad.model.Impulse;
import com.pi4j.io.gpio.*;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Server thread implementation for the vehicle. It only receives impulses
 * (Impulse.java, see PimpedHOTRoadLib) and controls the RPi's GPIO pins.
 *
 * @author Bernard Marijanovic
 */
public class VehicleServer extends Thread {

    public static final int DEFAULT_PORT = 13730;
    public static final String DEFAULT_MUSIC_DIR = "/home/pi/music/";

    private final ServerSocket serverSocket;
    private Socket socket; // Single client server
    private ObjectInputStream input;

    DirectoryPlayer player;

    private final GpioController gpio;
    private GpioPinDigitalOutput PWMA;
    private GpioPinDigitalOutput AIN1;
    private GpioPinDigitalOutput AIN2;
    private GpioPinDigitalOutput PWMB;
    private GpioPinDigitalOutput BIN1;
    private GpioPinDigitalOutput BIN2;
    private GpioPinDigitalOutput STBY;

    private boolean running;

    /**
     * Default constructor.
     *
     * @param port The port to which the car server should listen to
     * @param musicDirectory The directory containing all music files (.wav)
     * @throws IOException Networking error
     */
    public VehicleServer(int port, String musicDirectory) throws IOException {
        serverSocket = new ServerSocket(port);

        player = new DirectoryPlayer(musicDirectory);

        gpio = GpioFactory.getInstance();

        PWMA = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00);
        AIN2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
        AIN1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02);
        PWMB = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03);
        BIN1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04);
        BIN2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05);
        STBY = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06);
    }

    /**
     * Main logic for the server.
     */
    @Override
    public synchronized void start() {
        System.out.println("Server online: "
                + serverSocket.getInetAddress().getHostAddress()
                + ":" + serverSocket.getLocalPort());

        while (true) // "'Never quits!', des hosd ned söwa gschrim!" - Stuetz 2k14 
        {
            try {
                socket = serverSocket.accept();

                System.out.println("Client connected: "
                        + socket.getInetAddress().getHostAddress()
                        + ":" + socket.getLocalPort());

                running = true;
                input = new ObjectInputStream(socket.getInputStream());

                while (running) {
                    processImpulse((Impulse) input.readObject());
                }

                input.close();
                socket.close();

                System.out.println("Client disconnected.");

            } catch (Exception ex) {
                System.out.println("Error: Some problems happened. Quitting...");
                Logger.getLogger(VehicleServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Function to process all received signals from the client.
     *
     * Needs to be altered everytime the vehicle is modified. Currently: 2
     * motors with 2 wheels and a GHETTOBLASTAH!
     *
     * @param impulse Single impulse
     */
    private void processImpulse(Impulse impulse) {
        System.out.println("Impulse received: " + impulse.toString());

        switch (impulse) {
            case FORWARD:
                AIN1.setState(PinState.HIGH);
                AIN2.setState(PinState.LOW);
                BIN1.setState(PinState.HIGH);
                BIN2.setState(PinState.LOW);
                STBY.setState(PinState.HIGH);
                PWMA.setState(PinState.HIGH);
                PWMB.setState(PinState.HIGH);
                break;
            case RIGHT:
                AIN1.setState(PinState.LOW);
                AIN2.setState(PinState.LOW);
                BIN1.setState(PinState.HIGH);
                BIN2.setState(PinState.LOW);
                STBY.setState(PinState.HIGH);
                PWMA.setState(PinState.LOW);
                PWMB.setState(PinState.HIGH);
                break;
            case BACKWARD:
                AIN1.setState(PinState.LOW);
                AIN2.setState(PinState.HIGH);
                BIN1.setState(PinState.LOW);
                BIN2.setState(PinState.HIGH);
                STBY.setState(PinState.HIGH);
                PWMA.setState(PinState.HIGH);
                PWMB.setState(PinState.HIGH);
                break;
            case LEFT:
                AIN1.setState(PinState.HIGH);
                AIN2.setState(PinState.LOW);
                BIN1.setState(PinState.LOW);
                BIN2.setState(PinState.LOW);
                STBY.setState(PinState.HIGH);
                PWMA.setState(PinState.HIGH);
                PWMB.setState(PinState.LOW);
                break;
            case STOP:
                AIN1.setState(PinState.LOW);
                AIN2.setState(PinState.LOW);
                BIN1.setState(PinState.LOW);
                BIN2.setState(PinState.LOW);
                STBY.setState(PinState.LOW);
                PWMA.setState(PinState.LOW);
                PWMB.setState(PinState.LOW);
                break;
            case QUIT:
                AIN1.setState(PinState.LOW);
                AIN2.setState(PinState.LOW);
                BIN1.setState(PinState.LOW);
                BIN2.setState(PinState.LOW);
                STBY.setState(PinState.LOW);
                PWMA.setState(PinState.LOW);
                PWMB.setState(PinState.LOW);
                processMusicImpulse(Impulse.PAUSE_SONG);
                running = false;
                break;
            case PLAY_SONG:
            case PAUSE_SONG:
            case NEXT_SONG:
            case PREV_SONG:
                processMusicImpulse(impulse);
                break;
        }
    }

    /**
     * Processes a music impulse for the vehicle.
     *
     * @param impulse Music impulse
     */
    private void processMusicImpulse(Impulse impulse) {
        try {
            switch (impulse) {
                case PLAY_SONG:
                    player.play();
                    break;

                case PAUSE_SONG:
                    player.pause();
                    break;
                case NEXT_SONG:
                    player.next();
                    break;
                case PREV_SONG:
                    player.prev();
                    break;
            }
        } catch (Exception ex) {
            System.out.println("Music playback error! Doing nothing...");
            Logger.getLogger(VehicleServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
