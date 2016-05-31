/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pipelines;

import org.gstreamer.Bus;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.GstObject;
import org.gstreamer.Pipeline;
import org.gstreamer.elements.good.RTPBin;

public class Transmitter extends Pipeline { 
    
    //Declaration and manufacture of pipeline elements.
    final Element source  = ElementFactory.make("filesrc", null);
    final Element decoder   = ElementFactory.make("mad", null);
    final Element convertor = ElementFactory.make("audioconvert", null);
    final Element resampler = ElementFactory.make("audioresample", null);
    final Element mulawEncoder =  ElementFactory.make("mulawenc", null);
    final Element rtpPcMuPay = ElementFactory.make("rtppcmupay", null);
    final RTPBin  rtpBin = new RTPBin("RTPBin");
    final Element udpSink =  ElementFactory.make("udpsink", null);
    
    /**
     * Transmit the greeting to the caller.
     * @param filePath is the path of the greeting message file.
     * @param ip is the IP of the caller.
     * @param port is the port of the caller.
     */
    public Transmitter(String filePath, String ip, int port) {
        
        //Call parent constructor of Pipeline
        super();
        String exit = ip;
        if (ip.contains("@")){
            
            String[] tmp=ip.split("@");
            String tmpIP = tmp[1];
            System.out.println(tmpIP);
            ip=tmpIP;
        }
        //set parameters of some elements.
        source.set("location", filePath);      
        udpSink.set("host", ip);
        udpSink.set("port", port);
        udpSink.set("async", false);
        
        rtpBin.getRequestPad("send_rtp_sink_0");
        
        //Add elements to the pipeline
        addMany(source, decoder, convertor, resampler, mulawEncoder, rtpPcMuPay, rtpBin, udpSink);


        linkMany(source, decoder, convertor, resampler, mulawEncoder, rtpPcMuPay, rtpBin, udpSink);  

        //Console log
        System.out.println("Transmitter Pipeline " + filePath + " sending to " + exit + ":" + port + " OK.");
        getBus().connect(new Bus.ERROR() {
            @Override
            public void errorMessage(GstObject source, int code, String message) {
                System.out.println(code + message + source.getName());      
            }
        });
        getBus().connect(new Bus.WARNING() {
            @Override
            public void warningMessage(GstObject source, int code, String message) {
                System.out.println(code + message + source.getName());
            }
        });
    }    
    
}
