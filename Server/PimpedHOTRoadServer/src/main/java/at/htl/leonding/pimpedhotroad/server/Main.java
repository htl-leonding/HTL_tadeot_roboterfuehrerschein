package at.htl.leonding.pimpedhotroad.server;

import java.io.IOException;

/**
 * Yeah, the main class.
 *
 * @author Bernard Marijanovic
 */
public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {
        VehicleServer cs = new VehicleServer(VehicleServer.DEFAULT_PORT,
                VehicleServer.DEFAULT_MUSIC_DIR);
        cs.start();
    }
}
