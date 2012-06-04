package jobs

import novoda.zoidberg.device.{RichDevice => Device}

class JobQueue[T <: Job] //extends Queue[(DeviceSelector, T)]

object JobQueue {

  type DeviceSelector = (Device => Boolean)

  def allDevices = (d: Device) => true

  def emulators = (d: Device) => d.isEmulator

  def devicesOnly = (d: Device) => !d.isEmulator
//
//  def propertyBased(f: (String, String) => Boolean) = (d: Device) => {
//    d.javaDevice.getProperties.map(f)
//  }
}
