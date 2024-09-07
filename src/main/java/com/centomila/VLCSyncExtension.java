package com.centomila;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Transport;
import com.bitwig.extension.controller.ControllerExtension;

public class VLCSyncExtension extends ControllerExtension
{
   private Transport transport;
   private ControllerHost host;

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
      host.showPopupNotification("ChordGenerator Initialized");
      transport.playPosition().markInterested();
      transport.playPositionInSeconds().markInterested();
      transport.getPosition().markInterested();
      transport.isPlaying().markInterested();

      transport.playPosition().subscribe();
      transport.playPositionInSeconds().subscribe();
      transport.getPosition().subscribe();
      transport.isPlaying().subscribe();
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
      final double testCurPos = transport.playPositionInSeconds().getAsDouble();
      host.println(String.valueOf(testCurPos));
   }


}
