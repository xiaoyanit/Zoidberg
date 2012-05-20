package novoda.zoidberg.akka

import java.util.concurrent.atomic.AtomicLong
import akka.actor.{ExtendedActorSystem, ExtensionIdProvider, ExtensionId, Extension}

class DeviceStoreExtensionImpl(adbLocation: String) extends Extension {
  private val counter = new AtomicLong(0)

  def increment() = counter.incrementAndGet()

  def start() {
  }

  def stop() {
  }
}

object DeviceStoreExtension extends ExtensionId[DeviceStoreExtensionImpl] with ExtensionIdProvider {
  val adb = System.getenv("ANDROID_HOME") + "/platform-tools/adb"

  override def lookup = DeviceStoreExtension

  override def createExtension(system: ExtendedActorSystem) = new DeviceStoreExtensionImpl(adb)
}