package jobs

import com.novoda.zoidberg.Device
import collection.JavaConversions._
import collection.immutable.Queue
import JobQueue._

class JobQueue[T <: Job] //extends Queue[(DeviceSelector, T)]

object JobQueue {

  implicit def toPoorDevice(d: Device) = d.javaDevice

  type DeviceSelector = (Device => Boolean)

  def allDevices = (d: Device) => true

  def emulators = (d: Device) => d.isEmulator

  def devicesOnly = (d: Device) => !d.isEmulator
//
//  def propertyBased(f: (String, String) => Boolean) = (d: Device) => {
//    d.javaDevice.getProperties.map(f)
//  }
}
