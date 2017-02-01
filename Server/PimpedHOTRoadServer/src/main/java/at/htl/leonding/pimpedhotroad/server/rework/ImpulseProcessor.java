package at.htl.leonding.pimpedhotroad.server.rework;

import at.htl.leonding.pimpedhotroad.logger.FileLogger;
import at.htl.leonding.pimpedhotroad.model.Impulse;
import at.htl.leonding.pimpedhotroad.server.*;
import com.pi4j.io.gpio.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by michael on 1/26/17.
 */
public class ImpulseProcessor extends Thread {


    public interface ImpulseProcessorListener{
        void onImpulseReceived(Socket socket, Impulse receivedImpulse);
        void onImpulseProcessed(Socket socket, Impulse processedImpulse);
        void onStreamDisconnected(Socket socket);
    }

    private Socket socket;
    private List<ImpulseProcessorListener> listeners;
    private boolean running = true;

    private final DirectoryPlayer player;
    private final GpioController gpio;

    private volatile GpioPinDigitalOutput PWMA;
    private volatile GpioPinDigitalOutput AIN1;
    private volatile GpioPinDigitalOutput AIN2;
    private volatile GpioPinDigitalOutput PWMB;
    private volatile GpioPinDigitalOutput BIN1;
    private volatile GpioPinDigitalOutput BIN2;
    private volatile GpioPinDigitalOutput STBY;

    public ImpulseProcessor(Socket socket, DirectoryPlayer player){
        this.socket = socket;
        this.listeners = new ArrayList<>();
        this.player = player;

        gpio = GpioFactory.getInstance();

        PWMA = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00);
        AIN2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
        AIN1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02);
        PWMB = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03);
        BIN1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04);
        BIN2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05);
        STBY = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06);
    }

    public void addListener(ImpulseProcessorListener listener){
        if(listener != null && listeners.contains(listener) == false) {
            listeners.add(listener);
        }
    }

    public void removeListener(ImpulseProcessorListener listener){
        if(listener != null){
            listeners.remove(listener);
        }
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

            while(running == true) {
                Object inputObject = objectInputStream.readObject();

                if (inputObject instanceof Impulse) {
                    Impulse impulse = (Impulse) inputObject;

                    for (ImpulseProcessorListener listener :
                            listeners) {
                        listener.onImpulseReceived(socket, impulse);
                    }

                    processImpulse(impulse);

                    for (ImpulseProcessorListener listener :
                            listeners) {
                        listener.onImpulseProcessed(socket, impulse);
                    }
                }else{
                    FileLogger.getInstance().log(this.getClass(),
                            "Got object that is not an impulse");
                }
            }

            objectInputStream.close();
            inputStream.close();

            objectInputStream = null;
            inputStream = null;
        } catch (IOException e) {
            e.printStackTrace();
            FileLogger.getInstance().log(this.getClass(), e.getMessage());
            running = false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            FileLogger.getInstance().log(this.getClass(), e.getMessage());
        }

        for (ImpulseProcessorListener listener :
                listeners) {
            listener.onStreamDisconnected(socket);
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
    private synchronized void processImpulse(Impulse impulse) {
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
    private synchronized void processMusicImpulse(Impulse impulse) {
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
            Logger.getLogger(at.htl.leonding.pimpedhotroad.server.VehicleServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
