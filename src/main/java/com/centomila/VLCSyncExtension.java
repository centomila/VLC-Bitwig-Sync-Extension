package com.centomila;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Transport;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.Preferences;
import com.bitwig.extension.controller.api.SettableStringValue;
import com.centomila.VLCController;

public class VLCSyncExtension extends ControllerExtension {
   private Transport transport;
   private ControllerHost host;
   private boolean wasPlaying = false;
   private int currentPlayPosition = 0;
   private String seekVLC = "";
   
   private SettableStringValue VLCHostString;
   private SettableStringValue VLCHostPortString;
   private SettableStringValue VLCPasswordString;

   protected VLCSyncExtension(final VLCSyncExtensionDefinition definition, final ControllerHost host) {
      super(definition, host);
   }

   @Override
   public void init() {
      host = getHost();

      transport = host.createTransport();
      createSettingsObjects(host);

      // TODO: Perform your driver initialization here.
      host.showPopupNotification("VLCSync Initialized");
      transport.playPosition().markInterested();
      transport.playPositionInSeconds().markInterested();
      transport.getPosition().markInterested();
      transport.isPlaying().markInterested();
      transport.playStartPositionInSeconds().markInterested();

      transport.playPosition().subscribe();
      transport.playPositionInSeconds().subscribe();
      transport.getPosition().subscribe();
      transport.isPlaying().subscribe();
      transport.playStartPositionInSeconds().subscribe();

      // Add observer for play state changes
      transport.isPlaying().addValueObserver(this::onPlayStateChangedVLCCommand);
      transport.playStartPositionInSeconds().addValueObserver(this::onChangePlayStartPositionVLCCommand);

      host.println(VLCHostString.get());

   }

   @Override
   public void exit() {
      // Perform any cleanup once the driver exits
      // For now just show a popup notification for verification that it is no longer
      // running.
      getHost().showPopupNotification("VLCSync Exited");
   }

   @Override
   public void flush() {
      // Send any updates you need here.
      currentPlayPosition = (int) transport.playPositionInSeconds().getAsDouble();
   }

   private void onPlayStateChangedVLCCommand(boolean isPlaying)
   {
      final VLCController vlcController = new VLCController(VLCHostString.get(), VLCHostPortString.get(), VLCPasswordString.get());
      if (isPlaying) {
         host.println("VLC Started");
         // send command to VLC using the VLCController class
         try {
            // seek
            seekVLC = "seek&val=" + String.valueOf(currentPlayPosition);
            host.println(String.valueOf(seekVLC));
            vlcController.sendCommand(seekVLC);
            // play
            vlcController.sendCommand("pl_play");
         } catch (Exception e) {
            e.printStackTrace();
         }

      } else if (!isPlaying) {
         host.println("VLC Stopped");
         // send command to VLC using the VLCController class
         try {
            
            // move in bitwig to the round second
            // pause
            vlcController.sendCommand("pl_forcepause");
            // seek
            seekVLC = "seek&val=" + String.valueOf(currentPlayPosition);
            vlcController.sendCommand(seekVLC);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      wasPlaying = isPlaying;
   }
   
   private void onChangePlayStartPositionVLCCommand(double startPosition) {
      final VLCController vlcController = new VLCController(VLCHostString.get(), VLCHostPortString.get(),
            VLCPasswordString.get());
      
      
            // round start position
      int startPositionInt = (int) startPosition;

      host.println("START POSITION: " + startPositionInt);
      // send command to VLC using the VLCController class
      try {
         
         // seek
         seekVLC = "seek&val=" + String.valueOf(startPositionInt);
         vlcController.sendCommand(seekVLC);
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   private void createSettingsObjects(final ControllerHost host) {
      final Preferences preferences = host.getPreferences();

      VLCHostString = preferences.getStringSetting("VLC IP or Host (Default: localhost)", "VLC Host", 100, "localhost");
      VLCHostString.markInterested();

      VLCHostPortString = preferences.getStringSetting("VLC Port (Default: 8080)", "VLC Host", 100, "8080");
      VLCHostPortString.markInterested();

      VLCPasswordString = preferences.getStringSetting("VLC Password (Default: 1234)", "VLC Host", 100, "1234");
      VLCPasswordString.markInterested();

   }
      

}