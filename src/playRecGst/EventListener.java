package playRecGst;

import java.util.EventObject;

/**
 * The Class implement an event listener usable by the GUI to take action 
 * according to an event generated on the messageBus of the pipeline 
 * SoundPlayer.
 * 
 * ****************************************************************************
 * Example:
 * 
 *          EventListener myGuiListener = new EventListener() {
 *              @Override
 *              public void fireEvent(EventObject e) {
 * 
 *                  //If this listener listen the codeMessage (X).
 *                  if (e.getSource().equals(X)) {
 * 
 *                      //Do something ...
 *                  }
 *              }
 *          };
 *          
 *          //Define myGuiListener as the listener for the SoundPlayer events.
 *          this.controller.setPlayerListener(myGuiListener);
 * ****************************************************************************
 * 
 */
public interface EventListener {

    /**
     * This method fire an event locally.
     * 
     * @param e is the event to fire
     */
    void fireEvent(EventObject e);
}
