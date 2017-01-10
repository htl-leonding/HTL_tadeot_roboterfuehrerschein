package at.htl.leonding.pimpedhotroad.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * A music player, which reads audio files from a directory.
 * 
 * @author Bernard Marijanovic
 */
public class DirectoryPlayer {

    private File[] files;

    private Clip clip;

    private int index;
    private int pausedOnFrame;

    public DirectoryPlayer(String directory) {
        File f = new File(directory);
        if (f.isDirectory()) {
            files = f.listFiles();
        }
        index = 0;
    }

    public void play() throws FileNotFoundException, UnsupportedAudioFileException, IOException, LineUnavailableException {
        if (clip == null) {
            initPlayer();

            clip.start();
        } else if (!clip.isRunning() && pausedOnFrame < clip.getFrameLength()) {
            clip.setFramePosition(pausedOnFrame);

            clip.start();
        }
    }

    public void pause() {
        if (clip != null && clip.isRunning()) {
            pausedOnFrame = clip.getFramePosition();
            clip.stop();
        }
    }

    public void next() throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        changeSong(+1);
    }

    public void prev() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        changeSong(-1);
    }

    private void changeSong(int offset) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        if (clip == null) {
            play();
        } else {
            pausedOnFrame = 0;
            clip.stop();
            clip.close();
            
            index = index + offset >= files.length
                    ? 0
                    : (index + offset < 0
                            ? files.length - 1
                            : index + offset);
            initPlayer();

            clip.start();
        }
    }

    private void initPlayer() throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(files[index]);
        AudioFormat format = audioStream.getFormat();

        DataLine.Info info = new DataLine.Info(Clip.class, format);

        clip = (Clip) AudioSystem.getLine(info);
        clip.open(audioStream);
    }
}
