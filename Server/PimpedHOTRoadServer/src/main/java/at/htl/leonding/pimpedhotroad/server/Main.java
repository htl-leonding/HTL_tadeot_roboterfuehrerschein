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
        VehicleServer.getInstance().setDaemon(false);
        VehicleServer.getInstance().start();
        while(true){
            while(VehicleServer.getInstance().isRunning() || VehicleServer.getInstance().isAlive()){
                Thread.sleep(1000);
            }

            VehicleServer.getInstance().start();
        }
    }
}
