package novoda.zoidberg.akka

import java.util.concurrent.atomic.AtomicLong
import akka.actor._
import com.android.ddmlib.AndroidDebugBridge.{IDeviceChangeListener, IDebugBridgeChangeListener}
import com.android.ddmlib.{IDevice, AndroidDebugBridge}


trait ADBChangeListener extends IDebugBridgeChangeListener {
  def bridgeChanged(adb: AndroidDebugBridge) {
    println("Bridge changed")
  }
}

trait DeviceChangeListener extends IDeviceChangeListener {
  def deviceConnected(p1: IDevice) {
    println("DEVICE CONNECTED")
  }

  def deviceDisconnected(p1: IDevice) {
    println("DEVICE DiscCONNECTED")
  }

  def deviceChanged(p1: IDevice, p2: Int) {
    println("DEVICE Changed")
  }
}

case class Listener(listener: ActorRef)

class DeviceStoreExtensionImpl(system: ActorSystem, adbLocation: Option[String]) extends Extension with ADBChangeListener with DeviceChangeListener {

  start

  private val counter = new AtomicLong(0)

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
  }

  //
  //  def newStore(listener: Listener): ActorRef = {
  //  }
  //
  //  def newDevice(): ActorRef = {
  //
  //  }
}


object DeviceStoreExtensionImpl {
  implicit def enrichSystem(system: ExtendedActorSystem) = new DeviceStoreExtensionImpl(system, None)
}


object DeviceStoreExtension extends ExtensionId[DeviceStoreExtensionImpl] with ExtensionIdProvider {

  val adb = System.getenv("ANDROID_HOME") + "/platform-tools/adb"

  override def lookup = DeviceStoreExtension

  override def createExtension(system: ExtendedActorSystem) = new DeviceStoreExtensionImpl(system, Some(adb))
}