package playRecGst;

/**
 * The Class gather all the Constants variable used for our application.
 * 
 * @author   Martin
 * @reviewer Baptiste
 */
public final class Constants {
    
    /**
     * INBOX_PATH is the default path folder for new voice messages.
     */
    public static final String   INBOX_PATH        = "InBox";
    
    /**
     * SAVED_PATH is the default path folder for saved messages.
     */
    public static final String   SAVED_PATH         = "Saved";
    
    /**
     * GREETING_PATH is the default path folder for greeting messages.
     */
    public static final String   GREETING_PATH         = "Greeting";
    
    /**
     *MSG_PLAY_END is the code to notify that the paying file is finished.
     */
    public static final int      MSG_PLAY_END       = 1;
    
    /**
    * MSG_PLAY_ERROR is the code to notify an error when playing a file.
    */
    public static final int      MSG_PLAY_ERROR     = 2;        
    
}
