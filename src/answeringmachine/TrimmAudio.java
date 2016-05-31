/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package answeringmachine;

import jAudioFeatureExtractor.jAudioTools.AudioSamples;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
 
 class TrimmAudio {

    static void TrimmAudio(String filePath, double greetingTime)  {
        try {
            // read the file into an AudioSamples object
            AudioSamples as = new AudioSamples(new File(filePath), "", false);
            
            // get the audio from 15 to 30 seconds
            double[][] samples = as.getSamplesChannelSegregated(greetingTime, as.getDuration());
            
            // discard the rest of the samples
            as.setSamples(samples);
            
            // write the samples to a .wav file
            as.saveAudio(new File(filePath), true, AudioFileFormat.Type.WAVE, false);
        } catch (Exception ex) {
            Logger.getLogger(TrimmAudio.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
