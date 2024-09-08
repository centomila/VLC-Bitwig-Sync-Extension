package com.centomila;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Transport;
import com.bitwig.extension.controller.ControllerExtension;
import com.centomila.VLCController;


public class VLCSyncExtension extends ControllerExtension
{
   private Transport transport;
   private ControllerHost host;
   private boolean wasPlaying = false;
   private int currentPosition = 0;
   
   protected VLCSyncExtension(final VLCSyncExtensionDefinition definition, final ControllerHost host)
   {
      super(definition, host);
   }
   
   @Override
   public void init()
   {
      host = getHost();
      transport = host.createTransport();
      
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
      transport.isPlaying().addValueObserver(this::onPlayStateChanged);
      
   }
   
   @Override
   public void exit()
   {
      // TODO: Perform any cleanup once the driver exits
      // For now just show a popup notification for verification that it is no longer running.
      getHost().showPopupNotification("VLCSync Exited");
   }
   
   @Override
   public void flush()
   {
      // TODO Send any updates you need here.
      currentPosition = (int) transport.playPositionInSeconds().getAsDouble();
   }
   
   private void onPlayStateChanged(boolean isPlaying)
   
   {
      if (isPlaying && !wasPlaying)
      {
         host.println("VLC Started");
         host.showPopupNotification("VLC Started");
         // send command to VLC using the VLCController class
         try {
            final VLCController vlcController = new VLCController();
            vlcController.sendCommand("pl_play");
            final String commandForVLC = "seek&val=" + String.valueOf(currentPosition);
            host.println(String.valueOf(commandForVLC));
            vlcController.sendCommand(commandForVLC);
         } catch (Exception e) {
            e.printStackTrace();
         }


      }
      else if (!isPlaying && wasPlaying)
      {
         host.println("VLC Stopped");
         host.showPopupNotification("VLC Stopped");
         // send command to VLC using the VLCController class
         try {
            final VLCController vlcController = new VLCController();
            vlcController.sendCommand("pl_pause");
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      wasPlaying = isPlaying;
   }
}