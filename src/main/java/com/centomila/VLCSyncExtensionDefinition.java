package com.centomila;
import java.util.UUID;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

public class VLCSyncExtensionDefinition extends ControllerExtensionDefinition
{
   private static final UUID DRIVER_ID = UUID.fromString("7760f2dd-7443-401f-8f54-118912e4e784");
   
   public VLCSyncExtensionDefinition()
   {
   }

   @Override
   public String getName()
   {
      return "VLCSync";
   }
   
   @Override
   public String getAuthor()
   {
      return "centomila";
   }

   @Override
   public String getVersion()
   {
      return "0.1";
   }

   @Override
   public UUID getId()
   {
      return DRIVER_ID;
   }
   
   @Override
   public String getHardwareVendor()
   {
      return "centomila";
   }
   
   @Override
   public String getHardwareModel()
   {
      return "VLCSync";
   }

   @Override
   public int getRequiredAPIVersion()
   {
      return 19;
   }

   @Override
   public int getNumMidiInPorts()
   {
      return 0;
   }

   @Override
   public int getNumMidiOutPorts()
   {
      return 0;
   }

   @Override
   public void listAutoDetectionMidiPortNames(final AutoDetectionMidiPortNamesList list, final PlatformType platformType)
   {
   }

   @Override
   public VLCSyncExtension createInstance(final ControllerHost host)
   {
      return new VLCSyncExtension(this, host);
   }
}
