package playRecGst;

import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Pipeline;

 
public final class SoundRecorder extends Pipeline {

    //Declaration of the pipeline elements.
    final Element autoaudiosrc = ElementFactory.make("dshowaudiosrc", "autoaudiosrc");
    final Element audioconvert = ElementFactory.make("audioconvert", "audioconvert");
    final Element vorbisenc = ElementFactory.make("vorbisenc", "vorbisenc");
    final Element oggmux = ElementFactory.make("oggmux", "oggmux");
    final Element filesink = ElementFactory.make("filesink", "filesink");
    int counter=0;
    //String filePath;

    //Declaration of the relative file path.
    private final String filePath = "tmp.ogg";

    //Constructor
    SoundRecorder() {
        
        //Call the parent constructor of the pipeline.
        super("Capture");
        
        //Create the pipeline and link the elements
        addMany(autoaudiosrc, audioconvert, vorbisenc, oggmux, filesink);
        Pipeline.linkMany(autoaudiosrc, audioconvert, vorbisenc, oggmux, filesink);
        
        //Set the file folder output for filesink element.
        filesink.set("location", this.filePath);
    }

    //**************************************************************************
    
    /**
     * Get the path of the captured audio file
     *
     * @return the full path of the recorded file
     */
    public String getFilePath() {
        return filePath;
    }
    
    //**************************************************************************
}
