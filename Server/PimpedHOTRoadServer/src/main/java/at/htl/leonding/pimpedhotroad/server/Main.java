package at.htl.leonding.pimpedhotroad.server;

import at.htl.leonding.pimpedhotroad.server.rework.*;
import at.htl.leonding.pimpedhotroad.server.rework.VehicleServer;

import java.io.IOException;

/**
 * Yeah, the main class.
 *
 * @author Bernard Marijanovic
 */
public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {
//        VehicleServer cs = new VehicleServer(VehicleServer.DEFAULT_PORT,
//                VehicleServer.DEFAULT_MUSIC_DIR);
//        cs.start();
        boolean printedRunning = false;

        VehicleServer.getInstance().setDaemon(false);
        System.out.println("Starting server");
        VehicleServer.getInstance().start();
        while(true){
            while(VehicleServer.getInstance().isRunning() || VehicleServer.getInstance().isAlive()){
                if(printedRunning == false) {
                    System.out.println("Server running");
                    printedRunning = true;
                }
                Thread.sleep(1000);
            }

            printedRunning = false;
            System.out.println("Server crashed. Restarting");

            VehicleServer.getInstance().start();
        }
    }
}
