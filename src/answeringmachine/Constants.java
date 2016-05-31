package answeringmachine;

 
public final class Constants {

    /**
     * Port number of the Answering Machine for the SIP communication.
     */
    public static final int      SIP_PORT                       = 5060;    

    /**
     * Protocol used by the Answering Machine for the SIP communication.
     */
    public static final String   SIP_PROTOCOL                   = "udp";  

    /**
     * Name of the Answering Machine for the SIP communication.
     */
    public static final String   SIP_NAME                       = "DummyName";
    
    /**
     * Path where to save new recorded voice messages.
     */
    public static final String   RECORDED_CALLS_PATH            = "InBox";

    /**
     * Filename for the new recorded voice messages.
     */
    public static final String   RECORDED_CALLS_FILENAME_FORMAT = "%s-%s.wav";
       
}
