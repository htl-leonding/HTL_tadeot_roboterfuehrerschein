package at.htl.leonding.pimpedhotroad.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by michael on 1/26/17.
 */
public class FileLogger {
    private static FileLogger instance;

    public static FileLogger getInstance(){
        if(instance == null){
            instance = new FileLogger();
        }
        return instance;
    }

    private File logFile;

    private FileLogger(){
        logFile = new File("log");
        if(logFile.exists() == false){
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void log(Class clazz, String message){
        StringBuilder builder = new StringBuilder();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS", Locale.ENGLISH);

        builder.append(sdf.format(date));
        builder.append(" - ");
        builder.append(clazz.getSimpleName());
        builder.append(System.lineSeparator());
        builder.append(message);
        builder.append(System.lineSeparator());
        builder.append("\r\n");

        try {
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(
                            logFile, true
                    )
            );

            writer.write(builder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println(builder.toString());
            e.printStackTrace();
        }
    }
}
