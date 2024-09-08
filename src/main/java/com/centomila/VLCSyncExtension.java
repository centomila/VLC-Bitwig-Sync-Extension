package com.centomila;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Transport;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.Preferences;
import com.bitwig.extension.controller.api.SettableBooleanValue;
import com.bitwig.extension.controller.api.SettableStringValue;
import com.centomila.VLCController;

public class VLCSyncExtension extends ControllerExtension {
   private Transport transport;
   private ControllerHost host;
   private boolean wasPlaying = false;
   private int currentPlayPosition = 0;
   private String seekVLC = "";

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

      transport.playPosition().subscribe();
      transport.playPositionInSeconds().subscribe();
      transport.getPosition().subscribe();
      transport.isPlaying().subscribe();

      // Add observer for play state changes
      transport.isPlaying().addValueObserver(this::onPlayStateChangedVLCCommand);

   }

   @Override
   public void exit() {
      // TODO: Perform any cleanup once the driver exits
      // For now just show a popup notification for verification that it is no longer
      // running.
      getHost().showPopupNotification("VLCSync Exited");
   }

   @Override
   public void flush() {
      // TODO Send any updates you need here.
      currentPlayPosition = (int) transport.playPositionInSeconds().getAsDouble();
   }

   private void onPlayStateChangedVLCCommand(boolean isPlaying)
   {
      final VLCController vlcController = new VLCController();
      if (isPlaying && !wasPlaying) {
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

      } else if (!isPlaying && wasPlaying) {
         host.println("VLC Stopped");
         // send command to VLC using the VLCController class
         try {
            // pause
            vlcController.sendCommand("pl_pause");
            // seek
            seekVLC = "seek&val=" + String.valueOf(currentPlayPosition);
            vlcController.sendCommand(seekVLC);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      wasPlaying = isPlaying;
   }

   private void createSettingsObjects(final ControllerHost host) {
      final Preferences preferences = host.getPreferences();
      VLCIpString = preferences.getStringSetting("VLC IP or Host", "Controls", 100, "localhost");
      VLCIpString.markInterested();

   }
      private SettableStringValue VLCIpString;

}