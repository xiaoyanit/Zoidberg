package novoda.zoidberg.akka

import java.util.concurrent.atomic.AtomicLong
import akka.actor._
import com.android.ddmlib.AndroidDebugBridge.{IDeviceChangeListener, IDebugBridgeChangeListener}
import com.android.ddmlib.{IDevice, AndroidDebugBridge}
import akka.event.EventStream

case class DeviceConnected(device: IDevice)
case class DeviceDisconnected(device: IDevice)
case class DeviceChanged(device: IDevice, changeMask: Int)

trait ADBChangeListener extends IDebugBridgeChangeListener {
  def bridgeChanged(adb: AndroidDebugBridge) {
    println("Bridge changed")
  }
}

trait DeviceChangeListener extends IDeviceChangeListener {
  def eventStream: EventStream
  def deviceConnected(device: IDevice) = eventStream.publish(DeviceConnected(device))
  def deviceDisconnected(device: IDevice) = eventStream.publish(DeviceDisconnected(device))
  def deviceChanged(device: IDevice, changeMask: Int) = eventStream.publish(DeviceChanged(device, changeMask))
}

case class Listener(listener: ActorRef)

class DeviceStoreExtensionImpl(system: ActorSystem, adbLocation: Option[String]) extends Extension with ADBChangeListener with DeviceChangeListener {

  private val counter = new AtomicLong(0)

  def eventStream = system.eventStream

  def increment() = counter.incrementAndGet()

  def start() {
    try {
      AndroidDebugBridge.init(false)
    } catch {
      case ioe: IllegalStateException => {
        println("already started")
      }
    }
    AndroidDebugBridge.createBridge()
    AndroidDebugBridge.addDeviceChangeListener(this)
    AndroidDebugBridge.addDebugBridgeChangeListener(this)
  }

  def stop() {
    AndroidDebugBridge.removeDebugBridgeChangeListener(this)
    AndroidDebugBridge.removeDeviceChangeListener(this)
    AndroidDebugBridge.terminate()
  }
}

object DeviceStoreExtensionImpl {
  implicit def enrichSystem(system: ExtendedActorSystem) = new DeviceStoreExtensionImpl(system, None)
}

object DeviceStoreExtension extends ExtensionId[DeviceStoreExtensionImpl] with ExtensionIdProvider {
  val adb = System.getenv("ANDROID_HOME") + "/platform-tools/adb"
  override def lookup = DeviceStoreExtension
  override def createExtension(system: ExtendedActorSystem) = new DeviceStoreExtensionImpl(system, Some(adb))
}