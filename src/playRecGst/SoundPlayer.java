package playRecGst;


import java.io.File;
import java.util.EventObject;
import java.util.concurrent.TimeUnit;

import org.gstreamer.Bus;

import org.gstreamer.GstObject;
import org.gstreamer.State;
import org.gstreamer.elements.PlayBin2;


public class SoundPlayer extends PlayBin2 {
    
    //Global variables declaration.
    EventListener listener = null;
    TimeUnit scaleUnit = TimeUnit.SECONDS;
    
    /**
     * Constructor defining several listener action from messagesBUS.
     */
    public SoundPlayer() {
        super("Player");
        getBus().connect(new Bus.EOS() {
            @Override
            public void endOfStream(GstObject source) {  
                sendToListener(Constants.MSG_PLAY_END);
            }
        });
        getBus().connect(new Bus.ERROR() {
            @Override
            public void errorMessage(GstObject source, int code, String message) {
                sendToListener(Constants.MSG_PLAY_ERROR);
                handleMessage("ERROR", code, message);        
            }
        });
        getBus().connect(new Bus.WARNING() {
            @Override
            public void warningMessage(GstObject source, int code, String message) {
                handleMessage("WARNING", code, message);
            }
        });
    }

    //**************************************************************************
    
    /**
     * Send message to the listener
     * @param message is a message code
     */
    public void sendToListener(int message) {
        if (listener != null) {
            listener.fireEvent(new EventObject(message));
        }   
    }
    
    /**
     * Set the listener of the SoundPlayer' messagesBUS
     * @param lst is the listener
     */
    public void setListener(EventListener lst) {
        this.listener = lst;
    }
    
    /**
     * Handle a Message from the messagesBUS by making a printout.
     * @param type is the type of of the message to handle
     * @param code is the code defined in constants
     * @param message is the message from the bus
     */
    private void handleMessage(String type, int code, String message) {
        System.out.println(String.format("%s(%d): %s", type, code, message));
    }
    
    //**************************************************************************
    
    /**
     * Safe play of specified file
     * @param path is the path of the file
     */
    public void playFile(String path) {
        if (isPlaying()) {
            stop();
        }
        setInputFile(new File(path));
        play();
    }
    
    //**************************************************************************
    
    /**
     * Return if yes or no the player is paused
     * @return true or false
     */
    public boolean isPaused() {
        return (getState() == State.PAUSED);
    }

    /**
     * Used to get the total length of the stream.
     * 
     * @return the duration of the stream. 
     */
    public long getDuration(){
        
        return queryDuration(scaleUnit);
    }
    
    /**
     * Used to get the current position of playback in the streams.
     * 
     * @return the playback position of the played sound.
     */
    public long getPostion(){
        
        return queryPosition(scaleUnit);
    }
     
    //**************************************************************************
}
