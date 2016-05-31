package playRecGst;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gstreamer.Gst;


public final class Controller {

    private SoundRecorder capture = null;
    private SoundPlayer player = null;
    private String inboxPath;
    private String savedPath;
    private String greetingPath;

    /**
     * Constructor which instantiate a SoundRecorder element, a Sound player
     * element and defaults folders.
     */
    public Controller() {
        //Instantiate a new sound recorder.
        capture = new SoundRecorder();

        //Instantiate a new audio player.
        player = new SoundPlayer();

        //Initiate default InBox/Saved folders.
        createGreetingFolder();
        createInBoxFolder();
        createSavedFolder();

    }
    //**************************************************************************
    /**
     * Create the Greeting folder for greeting messages.
     */
    private void createGreetingFolder() {
        File dir = new File(Constants.GREETING_PATH);
        dir.mkdir();
        greetingPath = dir.getAbsolutePath();
    }

    //**************************************************************************
    /**
     * Create the InBox folder for new messages.
     */
    private void createInBoxFolder() {
        File dir = new File(Constants.INBOX_PATH);
        dir.mkdir();
        inboxPath = dir.getAbsolutePath();
    }

    /**
     * Get the InBox folder path.
     *
     * @return the InBox folder path as a string.
     */
    public String getInBoxFolderPath() {
        return inboxPath;
    }

    
    
    /**
     * Set the InBox folder path.
     *
     * @param folderPath is new path for InBox folder.
     */
    public void setInBoxFolderPath(String folderPath) {
        this.inboxPath = folderPath;
    }

    //**************************************************************************
    /**
     * Create the Saved folder for saved messages.
     */
    public void createSavedFolder() {
        File dir = new File(Constants.SAVED_PATH);
        dir.mkdir();
        savedPath = dir.getAbsolutePath();
    }

    /**
     * Get the Saved folder path.
     *
     * @return the Saved folder path as a string.
     */
    public String getSavedFolderPath() {
        return savedPath;
    }

    /**
     * Set the Saved folder path.
     *
     * @param folderPath is new path for Saved folder.
     */
    public void setSavedFolderPath(String folderPath) {
        this.savedPath = folderPath;
    }

    //**************************************************************************
    /**
     * Save the new greeting message.
     *
     * @param fullpath is the fullpath of the new greeting message.
     */
    public void saveAs(String fullpath) {
        File f = new File(capture.getFilePath());
        String pathWithoutFile = fullpath.substring(0, fullpath.lastIndexOf(File.separator));
        File p = new File(pathWithoutFile);
        if (f.exists() && !f.isDirectory() && p.exists() && p.isDirectory()) {
            if (fullpath.endsWith(".ogg")) {
                try {
                    //This "move" method is used in order to be able to replace existing file.
                    Files.move(Paths.get(capture.getFilePath()), Paths.get(fullpath), REPLACE_EXISTING);
                } catch (IOException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                //NOTE: This "remaeTo" is no able to replace existing file.
                f.renameTo(new File(fullpath + ".ogg"));
            }
        }
    }

    //**************************************************************************
    /**
     * Start capturing audio with specified quality to temp file
     */
    public void captureStart() {
        capture.play();
    }

    /**
     * Stop capturing audio
     */
    public void captureStop() {
        capture.stop();
    }

    //**************************************************************************
    /**
     * Play the draft greeting message.
     */
    public void playerPlayDraftGreeting() {
        System.out.print(capture.getFilePath() + "rtrtrt");
        playerPlayFile(capture.getFilePath());
    }
    public String getLastAudio(){
        return capture.getFilePath();
    }

    /**
     * Start playing audio file from path
     *
     * @param path full path to file
     */
    public void playerPlayFile(String path) {
        File f = new File(path);
        if (f.exists() && !f.isDirectory()) {
            player.playFile(path);
        }
    }

    /**
     * Play audio if player is paused
     */
    public void playerPlay() {
        if (player.isPaused()) {
            player.play();
        }
    }

    /**
     * Stop playing audio
     */
    public void playerStop() {
        player.stop();
    }

    /**
     * Pause playing audio
     */
    public void playerPause() {
        player.pause();
    }

    //**************************************************************************
    /**
     * Test is the player is playing a sound.
     *
     * @return true or false
     */
    public boolean playerIsPlaying() {
        return player.isPlaying();
    }

    /**
     * Test is the player is in pause mode.
     *
     * @return true or false
     */
    public boolean playerIsPaused() {
        return player.isPaused();
    }

    /**
     * Get the duration of the playing sound.
     *
     * @return the duration in seconds
     */
    public long playerGetDuration() {
        return player.getDuration();
    }

    /**
     * Get the position of the playback.
     *
     * @return the position in seconds.
     */
    public long playerGetPosition() {
        return player.getPostion();
    }

    //**************************************************************************
    /**
     * Set the player event listener for catching messages
     *
     * @param e is the event listener
     */
    public void setPlayerListener(EventListener e) {
        player.setListener(e);
    }

    /**
     * Get the current Time and Date
     *
     * @return the Date as a string
     */
    public String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * Quit an QST instance
     */
    public void quitGST() {
        Gst.quit();
    }

    //**************************************************************************
}
