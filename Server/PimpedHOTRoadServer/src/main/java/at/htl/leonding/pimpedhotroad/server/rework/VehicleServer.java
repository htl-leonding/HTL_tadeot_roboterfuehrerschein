package at.htl.leonding.pimpedhotroad.server.rework;

import at.htl.leonding.pimpedhotroad.logger.FileLogger;
import at.htl.leonding.pimpedhotroad.model.Impulse;
import at.htl.leonding.pimpedhotroad.server.DirectoryPlayer;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Logger;

/**
 * Created by michael on 1/26/17.
 */
public class VehicleServer extends Thread implements ImpulseProcessor.ImpulseProcessorListener {
    public static final int DEFAULT_PORT = 13730;
    public static final String DEFAULT_MUSIC_DIR = "/home/pi/music/";

    private static VehicleServer instance = null;

    public static VehicleServer getInstance(){
        if(instance == null){
            instance = new VehicleServer();
        }
        return instance;
    }


    private boolean running = false;

    private DirectoryPlayer player;


    private ServerSocket serverSocket;
    private Executor executor;

    private VehicleServer(){
        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
        } catch (IOException e) {
            e.printStackTrace();
            FileLogger.getInstance().log(this.getClass(), e.getMessage());
            serverSocket = null;
        }

        player = new DirectoryPlayer(DEFAULT_MUSIC_DIR);
        executor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        while(running == true){
            try {
                Socket clientSocket = serverSocket.accept();
                ImpulseProcessor impulseProcessor = new ImpulseProcessor(clientSocket, player);

                impulseProcessor.addListener(this);

                executor.execute(impulseProcessor);
            } catch (IOException e) {
                e.printStackTrace();
                FileLogger.getInstance().log(this.getClass(), e.getMessage());
            }
        }
        instance = new VehicleServer();
    }

    @Override
    public synchronized void start() {
        if(running == false) {
            running = true;
            super.start();
        }
    }

    public synchronized void stopServer(){
        running = false;
    }

    @Override
    public void onImpulseReceived(Socket socket, Impulse receivedImpulse) {
        FileLogger.getInstance().log(this.getClass(),
                "Received impulse: " +
                receivedImpulse.name()
         );
    }

    @Override
    public void onImpulseProcessed(Socket socket, Impulse processedImpulse) {
        FileLogger.getInstance().log(this.getClass(),
                "Processed impulse: " +
                        processedImpulse.name()
        );
    }

    @Override
    public void onStreamDisconnected(Socket socket) {
        FileLogger.getInstance().log(this.getClass(),
                "Lost connection with: " +
                socket.getInetAddress().getHostAddress() +
                ":" +
                socket.getPort()
        );
    }
}
