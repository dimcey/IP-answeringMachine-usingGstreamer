package main;

//import the playRecGst.
import answeringmachine.SIP;
import playRecGst.*;

//import the gui
import gui.GUI;
import java.io.IOException;

//import GStreamer
import org.gstreamer.Gst;

 
public class Main {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        
        //*****************************************************************start
        //Initialize the gstreamer framwork.
        args = Gst.init("AnsweringMachine",args); // new String[] { "--gst-debug-level=3","--gst-debug-no-color" }
        System.out.println("GStreamer OK.");
        //*******************************************************************end
        
        //*****************************************************************start
        //Instantiate SIP abstraction.
        final SIP sip = new SIP();
        System.out.println("SIP OK.");
        //*******************************************************************end
        
        //*****************************************************************start
        //Instantiate a Controller.
        final Controller controller = new Controller();
        System.out.println("Controller OK.");
        //*******************************************************************end
        
               
        //*****************************************************************start
        //Instantiate a GUI.
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                GUI gui = new GUI(controller);
                gui.setVisible(true);
                sip.setGui(gui);
                System.out.println("Application initialisation OK. \n");
            }
        });
        //*******************************************************************end
        
    }
}
