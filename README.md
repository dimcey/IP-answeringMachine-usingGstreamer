# IPansweringMachine-usingGstreamer
Callers are able to leave a message(voice mail) on the answering machine after a brief greeting. It is possible to playback, repeat or delete a message.

# Introduction
The aim of this project is to understand the basics of SIP. SIP is a protocol for establishing/setting up a communication session and disconnecting the parties in the session at the end of the session. It is widely applicable for voice and video calls and instant messaging applications over IP networks. SIP is an application layer protocol that can be implemented independent of the underlying transport layer protocol which makes SIP flexible and applicable for a variety of applications.  It is a text based protocol with request and response message types. Some of the request type messages are REGISTER, INVITE, ACK, BYE ... and the response messages can be PROVISIONAL, SUCCESS, REDIRECTING etc. In this lab among the options in the lab3 tracks, we have decided to implement the IP answering machine. 

# Problem Specification
The IP answering machine that we have implemented in the lab allows callers to leave voice messages after a short greeting message. The receiver side (the user of the application) should be able to playback, repeat and delete the voice messages. 

In addition to the above mentioned mandatory to implement functionalities, we have also implemented the following extra features.
- Email notifications: once the voicemail is recorded, the user is notified via an email about the message with an attachment of the recorded message
-	Users are able to select a greeting messages to be played for the callers before they are leaving a voicemail.
-	Users are able to redirect already saved voicemail to another SIP user.

The developed GUI is show in the figure below:

![alt tag](https://github.com/dimcey/IP-answeringMachine-usingGstreamer/blob/master/gui.jpg)

# Code Structure
The project consists 7 packages and 17 classes, as shown in the figure below. This code structure clearly defines and differentiates the Gstreamer functionalities from the SIP part, as well as from the graphical user interface.

Short description of each Package with its classes inside:

-	Answeringmachine

○	Constants (Defines the constants that are used in the whole package)

○	ProcessCall (Implements the functionality what to do when there is a invite and buy requestes)

○	SIP (Handles the whole logic behind SIP regarding the creating of the SIP server and handling the SIP listener)

○	SendEmail (A class used to send email notification when a user receives a voice mail)

○	TrimmAudio

-	Configuration

○	Config (Sets the global properties for the project such as the user’s email, the greeting message and the duration of the message

- GUI

○	GUI (Implements the whole logic behind the GUI)

-	Main

○	main (Includes the main class which has to be executed)

-	pipelines

○	Receiver (Gstreamer functionality to store incoming audio packets on UPD port)

○	Transmitter (Gstreamer functionality to send audio packets on ip address)

-	PlayRecGst

○	Constants (Defines the constants that are used in the whole package)

○	Controller (Initializes the SoundPlayer and SoundRecorder classes, and calling their functionalities)

○	EventListener (Class that dynamically react to a specific event from the GUI to perform certain Gstreamer action)

○	SoundPlayer (Gstreamer functionality to play a audio file)

○	SoundRecorder (Gstreamer functionality to record a audio file)

# Architecture
The image below illustrates the main architecture of the developed Answering Machine tool.
![alt tag](https://github.com/dimcey/IP-answeringMachine-usingGstreamer/blob/master/Architecture.jpg)
The classes and packages dependencies have been thought to enable a clear development, to facilitate the reusability of our solution and to bring high scalability. Guided from the development of the first lab, the produced GUI is also loosely coupled with the SIP functionalities such as the SIP and ProcessCall classes, and the Gstreamer operations, so that if an end user wants to develop his own GUIs, only an object is needed to be created from the mentioned classes and all of the Client’s functionalities would become available for the end user.

Once the Main class is started it will initialize the GUI and the SIP class, which are two separate parts of the whole project as seen in the figure above. Initializing the SIP class will start the creation of SIP server on a static IP address which in our case is the developer’s own private NAT address.  Using java sip and sdp libraries the SIP class is handling the requests that are coming to the already created SIP server such as INVITE, BYE and CANCEL, which are explained in the next section. 
Another part of the project is the GUI which has the possibility to trigger independent actions such as recording a greeting message, saving the greeting message and setting up a default greeting message that will be played once a SIP call is received. Also, there is a possibility to play, repeat and delete already saved voicemail message or a greeting message. The functionality behind this will be explained in the next section.

Important use case for this project is receiving a SIP call. Once the Main class is executed and the SIP server is initialized, the project is ready to receive a SIP call. The tests for this action were successfully done using Linphone application for android mobile phones. The SIP server is running on the developer’s IP address (192.168.178.33), on the default SIP port 5060 and with a registered used name “DummyName”. One test case was performed using “Linphone” android application installed on a mobile device with IP address (192.168.178.10) which is behind same NAT as the SIP server. From the Linphone application we were able to call “DummyName@192.168.178.33:5060” and receive the INVITE request on the SIP server which will аccordingly handle that request.

# Algorithm description
The following section will explain the technical part behind the GUI and the SIP functionality.
Once the Main class is executed the SIP server will be initialized on the developer’s IP address 192.168.178.33:5060 and will be able to process SIP requests such as INVITE, BYE and CANCEL. When the client receives a call i.e an INVITE request, scenario explained in the test case above, the server SIP will handle it and perform certain actions. The server is identifying the caller and calling the ProcessCall class where is implemented the logic behind the SIP requests. First a file is prepared to be ready to store the voicemail and then a greeting message is located and fetched to be transmitted to the caller by the Transmitter class. After the duration time of the greeting message expires, the Receiver class is called which will receive the audio voice coming from the caller and then store it in a file which will be displayed in the GUI “InBox” Jlist. When the caller hang up the call, BYE request is send to the SIP server which will trigger the stopRecording() function from the PrecessCall class. This will just stop the Gstreamer functionality to store the incoming audio packets and in the same time call the SendEmail class which will form an email with the recorded file as an attachment and send it to a given email address as a notification.

The logic behind the GUI is straightforward because every button-click triggers certain functionality. On the GUI startup, the Jlists will load the audio files that are in the “InBox” and “Greeting” directories. On a selected file from the Jlists the user can play, replay and delete a audio file by appropriate buttons. However, there are more advanced features in the GUI such as recording and saving audio. By clicking the microphone button the user can record his own voice and after finishing quickly play it on the speakers. Once the microphone is clicked a Gstreamer functionality is implemented in the SoundRecorder class that is creating the needed pipeline for recording an audio file and saving it in a temporary file. If the user wants to quiclky listen to the recorded file, on a button-click the temporary file passed to the SoundPlayer class which creates the needed pipeline to play the file on the user’s speakers. Also, there is a listener waiting for the end of the stream to fire an event which will be registered by the GUI class to perform updates and that the whole process of playing the audio can be stopped.

# Challenges
SIP has evolved over the years, RFC 3261 being the core protocol specification but it has been updated by a number of other RFCs which is more RFCs to implement. Therefore it was necessary to choose a library/stack which supported SIP functionalities and integrate it into our application, which was the initial challenge for us. This led us to the JSIP stack with the JAIN SIP/SDP libraries, which was an apt fit for our implementation. 

The extra features in the form of e-mail notification and recording custom made greetings also posed challenges in the form of improper storage of messages and playback of the greetings, which were eventually sorted out. 

# LogFile
The LogFile will be uploaded separately. The LogFile is automatically managed by the SIP server and is located in a log_server.txt in the project directory. 
