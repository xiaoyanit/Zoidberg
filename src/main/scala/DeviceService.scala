package novoda.android.zoidberg

import akka.actor.{ActorRef, OneForOneStrategy, Actor}
import novoda.zoidberg.akka.DeviceConnected
import collection.immutable.HashMap


/**
 * Class encapsulating the full device Service.
 * Start service by invoking:
 * <pre>
 * val deviceService = Actor.actorOf[DeviceService].start()
 * </pre>
 */
class DeviceService extends DeviceServer with DeviceManagement with JobManagement with JobStorageFactory {
  def preStart() = {
    remote.start("localhost", 2552);
    remote.register("device:service", self)
  }
}

/**
 * Device server. Manages devices.
 */
trait DeviceServer extends Actor {
  self.faultHandler = OneForOneStrategy(List(classOf[Exception]), 5, 5000)
  val storage: ActorRef

  def receive: Receive = deviceManagement orElse jobManagement

  protected def deviceManagement: Receive

  protected def jobManagement: Receive

  protected def shutdownSessions(): Unit

  protected def start(): Unit

  override def preStart() {
    super.preStart()
  }

  def postStop() = {
    shutdownSessions
    self.unlink(storage)
    storage.stop()
  }
}

/**
 * Implements device management via ADB
 */
trait DeviceManagement {
  this: Actor =>

  val storage: ActorRef

  val sessions = new HashMap[String, ActorRef]

  protected def sessionManagement: Receive = {
    case _ => ""
  }

  protected def start = {
    system.eventStream.subscribe(listener, classOf[DeviceConnected])
  }

  protected def shutdownSessions = sessions.foreach {
    case (_, session) => session.stop()
  }
}

/**
 * Implements device management via ADB
 */
trait JobManagement {
  this: Actor =>
}

trait JobStorageFactory {
  this: Actor =>
  val storage = this.self.spawnLink[JobManagement] // starts and links ChatStorage
}