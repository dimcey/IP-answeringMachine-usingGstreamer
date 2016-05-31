/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package answeringmachine;

import configuration.Config;
import gui.GUI;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sdp.Media;
import javax.sdp.MediaDescription;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;
import javax.sip.DialogState;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionState;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.TransportNotSupportedException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.header.ContactHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ToHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

 
public class SIP implements SipListener {

    private SipFactory sipFactory;          // Used to access the SIP API.
    private SipStack sipStack;              // The SIP stack.
    private SipProvider sipProvider;        // The SIP stack.
    private SdpFactory sdpFactory;
    private MessageFactory messageFactory;  // Used to create SIP message factory.
    private HeaderFactory headerFactory;    // Used to create SIP headers.
    private AddressFactory addressFactory;  // Used to create SIP URIs.
    private Address contactAddress;         // The contact address.
    private ContactHeader contactHeader;    // The contact header.
    private String ip;
    private ProcessCall call = null;
    private Object locker = new Object();
    private Config properties = new Config();
    private GUI gui = null;

    /**
     * Initialise SIP on your local application.
     */
    public SIP() {
        try {
            try {
                // Get the local IP address.
                //this.ip = "192.168.178.33";

                this.ip = InetAddress.getLocalHost().getHostAddress().toString();
            } catch (UnknownHostException ex) {
                Logger.getLogger(SIP.class.getName()).log(Level.SEVERE, null, ex);
            }
             
            // Create the SIP factory and set the path name.
            this.sipFactory = SipFactory.getInstance();
            this.sipFactory.setPathName("gov.nist");
            this.sdpFactory = SdpFactory.getInstance();
            
            // Create and set the SIP stack prop.
            Properties prop = new Properties();
            
            prop.setProperty("javax.sip.STACK_NAME", Constants.SIP_NAME);
            prop.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "32");
            prop.setProperty("gov.nist.javax.sip.DEBUG_LOG", "log_debug.txt");
            prop.setProperty("gov.nist.javax.sip.SERVER_LOG", "log_server.txt");

            // Create the SIP stack.
            this.sipStack = this.sipFactory.createSipStack(prop);
            // Create the SIP message factory.
            this.messageFactory = this.sipFactory.createMessageFactory();
            // Create the SIP header factory.
            this.headerFactory = this.sipFactory.createHeaderFactory();
            // Create the SIP address factory.
            this.addressFactory = this.sipFactory.createAddressFactory();
            // Create the SIP listening point and bind it to the local IP address, port and protocol.
            ListeningPoint listeningPoint = this.sipStack.createListeningPoint(this.ip, Constants.SIP_PORT, Constants.SIP_PROTOCOL);
            // Create the SIP provider.
            this.sipProvider = this.sipStack.createSipProvider(listeningPoint);
            // Add our application as a SIP listener.
            this.sipProvider.addSipListener(this);
            // Create the contact address used for all SIP messages.
            this.contactAddress = this.addressFactory.createAddress(Constants.SIP_NAME + " <sip:" + this.ip + ":" + Constants.SIP_PORT + ">");
            // Create the contact header used for all SIP messages.
            this.contactHeader = this.headerFactory.createContactHeader(contactAddress);
            System.out.print(contactHeader);
        } catch (ParseException | TooManyListenersException | InvalidArgumentException | ObjectInUseException | PeerUnavailableException | TransportNotSupportedException e) {
            // If an error occurs, display an error message box and exit.
            System.out.println(e.getMessage());
            System.out.println("ne");
            System.exit(-1);
        }
    }

    /**
     * Process a request event.
     * @param re is an event informing about details of a request processing.
     */
    @Override
    public void processRequest(RequestEvent re) {
        Request request = re.getRequest();
        ServerTransaction transaction = re.getServerTransaction();
        if (transaction == null) {
            //request.get
            //System.out.println("Request " + request.getMethod() + " - transaction is null");
        } else {
            System.out.println("Request " + request.getMethod() + " - transaction (branch id: " + transaction.getBranchId() + ", dialog id " + transaction.getDialog().getDialogId() + ")");
        }
        switch (request.getMethod()) {
            case Request.INVITE:
                incomingInvite(re, request, transaction);
                break;
            case Request.BYE:
                incomingBye(request, transaction);
                break;
            case Request.CANCEL:
                incommingCancel(re, request, transaction);
                break;
            default:
                incommingOther(re, transaction);
        }
    }

    /**
     * Process a response event.
     * @param re is an event informing about details of a response processing.
     */
    @Override
    public void processResponse(ResponseEvent re) {
        System.out.println("Response");
    }

    /**
     * Process a timeout event.
     * @param te is an event informing about details of a timeout processing.
     */
    @Override
    public void processTimeout(TimeoutEvent te) {
        if (te.isServerTransaction()) {
            System.out.println("Timeout event: " + te.getServerTransaction().toString());
        } else {
            System.out.println("Timeout event: " + te.getClientTransaction().toString());
        }
    }

    /**
     * Process a IOException event.
     * @param ioee is an event informing about details of a IOException processing.
     */
    @Override
    public void processIOException(IOExceptionEvent ioee) {
        System.out.println("Some IOException");
    }

    /**
     * Process a TransactionTerminated event.
     * @param tte is an event informing about details of a TransactionTerminated processing.
     */
    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent tte) {
        if (tte.isServerTransaction()) {
            System.out.println("Transaction terminated: " + tte.getServerTransaction().getBranchId());
        } else {
            System.out.println("Transaction terminated: " + tte.getClientTransaction().getBranchId());
        }
    }

    /**
     * Process a DialogTerminated event.
     * @param dte is an event informing about details of a DialogTerminated processing.
     */
    @Override
    public void processDialogTerminated(DialogTerminatedEvent dte) {
        System.out.println("Dialog terminated: " + dte.getDialog().getLocalTag());
    }

    /**
     * Handle an incoming INVITE. 
     * @param re is an event informing about details of a request processing.
     * @param request is the request.
     * @param transaction is the transaction.
     */
    private void incomingInvite(RequestEvent re, Request request, ServerTransaction transaction) {
        //SipProvider sipProvider = (SipProvider) re.getSource();
        synchronized (locker) {
            try {

                if (transaction == null) {
                    transaction = sipProvider.getNewServerTransaction(request);
                }

                if (call != null) {
                    Response response = messageFactory.createResponse(Response.BUSY_HERE, request);
                    response.addHeader(contactHeader);
                    transaction.sendResponse(response);
                    return;
                }
                try {
                    if (transaction.getState() != TransactionState.COMPLETED) {
                        System.out.println(new String(request.getRawContent()));
                        SessionDescription client = parseDescription(new String(request.getRawContent()));
                        //System.out.println(client.getSessionName() + "wewewe");
                        String clientIP = parseIPAdress(client);
                        System.out.println("Client: " + clientIP);
                        int clientAudioPort = parseAudioPort(client);

                        if (clientAudioPort == -1) {
                            System.out.println("Client doesn't distribute audio port!");
                        } else {
                            System.out.println("Client listening for audio on: " + clientIP + ":" + clientAudioPort);
                            String caller = ((FromHeader) request.getHeader("from")).getAddress().getDisplayName();
                            //System.out.println(Constants.SIP_NAME +"www");
                            String internal = "internal";
                            String path = "dummy";
                            call = new ProcessCall(caller, clientIP, clientAudioPort, internal, path);
                            //System.out.println("sdad");
                            SessionDescription sdp = parseDescription(
                                    "v=0\n" //protocol 
                                    + "o=" + Constants.SIP_NAME + " " + new Date().getTime() + " " + new Date().getTime() + " IN IP4 " + ip + "\n" //session owner and identifier
                                    + "c=IN IP4 " + ip + "\n" //connection info
                                    + "s=AnsweringMachine_session\n" //session name
                                    + "t=0 0\n" //time the session is active
                                    + "m=audio " + (call.getPort()) + " RTP/AVP 0\n" //port where server listening audio
                                    + "a=rtpmap:0 PCMU/8000" //payload code 
                            );

                            Response response = messageFactory.createResponse(Response.OK, request);
                            response.addHeader(contactHeader);
                            ToHeader toHeader = (ToHeader) response.getHeader(ToHeader.NAME);
                            toHeader.setTag(Constants.SIP_NAME + System.nanoTime()); // Application is supposed to set.
                            response.setContent(sdp.toString().getBytes(), headerFactory.createContentTypeHeader("application", "sdp"));
                            transaction.sendResponse(response);

                            System.out.println("Response for INVITE sent");
                        }
                    }
                } catch (SipException | InvalidArgumentException ex) {
                    System.out.println(ex.getMessage());
                }
            } catch (ParseException | InvalidArgumentException | SipException ex) {
                System.out.println(ex.getMessage());
                System.exit(0);
            }
        }
    }

    /**
     * Handle an incoming BYE.
     * @param request is the request.
     * @param transaction is the transaction.
     */
    private void incomingBye(Request request, ServerTransaction transaction) {
        synchronized (locker) {
            try {
                System.out.println(transaction.getBranchId());
                Response response = messageFactory.createResponse(200, request);
                transaction.sendResponse(response);
                String caller = ((FromHeader) request.getHeader("from")).getAddress().toString();
                
                //Read prop
                String address = properties.getProperties("address");
                call.stopRecording(address, caller);
                call = null;    
                
                //Refresh the GUI.
                if (gui != null) {
                    gui.refresh();
                }

            } catch (ParseException | InvalidArgumentException | SipException ex) {
                System.out.println(ex.getMessage());
                System.exit(0);
            }
        }

    }

    /**
     * Handle an incoming CANCEL.
     * @param re is an event informing about details of a request processing.
     * @param request is the request.
     * @param transaction is the transaction.
     */
    private void incommingCancel(RequestEvent re, Request request, ServerTransaction transaction) {
        try {
            if (transaction == null) {
                System.out.println("Transaction doesn't exist.");
                return;
            }
            Response response = messageFactory.createResponse(200, request);
            transaction.sendResponse(response);
            if (re.getDialog().getState() != DialogState.CONFIRMED) {
                response = messageFactory.createResponse(Response.REQUEST_TERMINATED, request);
                re.getServerTransaction().sendResponse(response);
            }
        } catch (ParseException | InvalidArgumentException | SipException ex) {
            System.out.println(ex.getMessage());
            System.exit(0);
        }
    }

    /**
     * Handle an incoming OTHER.
     * @param re is an event informing about details of a request processing.
     * @param transaction is the transaction.
     */
    private void incommingOther(RequestEvent re, ServerTransaction transaction) {
        try {
            //System.out.println("Some other request arrived");
            transaction.sendResponse(messageFactory.createResponse(202, re.getRequest()));
            SipProvider provider = (SipProvider) re.getSource();
            Request refer = re.getDialog().createRequest("REFER");
            re.getDialog().sendRequest(provider.getNewClientTransaction(refer));
        } catch (ParseException | InvalidArgumentException | SipException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Parse the session description.
     * @param ssdp is a session description.
     * @return the session description.
     */
    public SessionDescription parseDescription(String ssdp) {
        SessionDescription description = null;
        try {
            description = sdpFactory.createSessionDescription(ssdp);
        } catch (SdpParseException e) {
            System.out.println("parseDescription: " + e.toString());
        }
        return description;
    }

    /**
     * Get the IP address from a session description.
     * @param sdp is a session description.
     * @return an IP address.
     */
    public String parseIPAdress(SessionDescription sdp) {
        String result = null;
        try {
            result = sdp.getOrigin().getAddress();
        } catch (SdpParseException e) {
            System.out.println("parseIPAdress: " + e.toString());
        }
        return result;
    }

    /**
     * Get the port number from a session description.
     * @param sdp is a session description.
     * @return a port number.
     */
    public int parseAudioPort(SessionDescription sdp) {
        try {
            for (Object obj : sdp.getMediaDescriptions(false)) {
                Media m = ((MediaDescription) obj).getMedia();
                if (m.getMediaType().equals("audio")) {
                    return m.getMediaPort();
                }
            }
        } catch (SdpException e) {
            System.out.println("parseAudioPort: " + e.toString());
        }
        return -1;
    }

    /**
     * Link the GUI to the SIP.
     * @param gui is the GUI passed.
     */
    public void setGui(GUI gui) {
        this.gui = gui;
    }
}
