/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pipelines;

import org.gstreamer.Caps;
import org.gstreamer.Element;
import static org.gstreamer.Element.linkMany;
import org.gstreamer.ElementFactory;
import org.gstreamer.Pad;
import org.gstreamer.Pipeline;
  
public class Receiver extends Pipeline {

    //Declaration and Manufacture of elements.
    final Element udpSource = ElementFactory.make("udpsrc", null);
    final Element rtpBin = ElementFactory.make("gstrtpbin", null);
    final Element rtpPcMuDepay = ElementFactory.make("rtppcmudepay", null);
    final Element mulawDecoder = ElementFactory.make("mulawdec", null);
    final Element audioResample = ElementFactory.make("audioresample", null);
    final Element audioConvert = ElementFactory.make("audioconvert", null);
    final Element wavEncoder = ElementFactory.make("wavenc", null);
    final Element sink = ElementFactory.make("filesink", null);

    private int port = 0;

    /**
     * Receiver pipeline to receive the voice message.
     * @param filePath location where to store the voice message.
     */
    public Receiver(String filePath) {
        super();
        //choose random unused port
        udpSource.set("port", 0);

        udpSource.getStaticPad("src").setCaps(
                Caps.fromString("application/x-rtp,"
                        + "media=(string)audio,"
                        + "clock-rate=(int)8000,"
                        + "encoding-name=(string)PCMU, "
                        + "payload=(int)0"));

        sink.set("location", filePath);
        addMany(udpSource, rtpBin, rtpPcMuDepay, mulawDecoder, audioResample,
                audioConvert, wavEncoder, sink);


        rtpBin.connect(new Element.PAD_ADDED() {
            @Override
            public void padAdded(Element elmnt, Pad pad) {
                if (pad.getName().startsWith("recv_rtp_src")) {
        
                    System.out.println("Pad added: " + pad);
                    pad.link(rtpPcMuDepay.getStaticPad("sink"));

                }
            }
        });
        rtpBin.connect(new Element.PAD_REMOVED() {
            @Override
            public void padRemoved(Element elmnt, Pad pad) {
                     System.out.println("Pad unlinked: " + pad);          

            }      
        });
        
        udpSource.getStaticPad("src").link(rtpBin.getRequestPad("recv_rtp_sink_0"));
        
        //Link elements together.
        linkMany(rtpPcMuDepay, mulawDecoder, audioResample, audioConvert, wavEncoder, sink);

        //pause - GST BUG becouse of port
        pause();
        port = (Integer) udpSource.get("port");
        

    }

    /**
     * Get the port of the receiver.
     * @return the port number.
     */
    public int getPort() {
        return port;
    }   

  
}