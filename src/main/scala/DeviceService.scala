package novoda.android.zoidberg

import novoda.zoidberg.akka.DeviceConnected
import collection.immutable.HashMap
import akka.actor.{ActorRef, Actor}


/**
 * Class encapsulating the full device Service.
 * Start service by invoking:
 * <pre>
 * val deviceService = Actor.actorOf[DeviceService].start()
 * </pre>
 */
class DeviceService extends DeviceServer with DeviceManagement with JobManagement with JobStorageFactory

/**
 * Device server. Manages devices.
 */
trait DeviceServer extends Actor {

  def receive: Receive = deviceManagement orElse jobManagement

  val storage: ActorRef

  protected def deviceManagement: Receive

  protected def jobManagement: Receive

  override def preStart() {
    super.preStart()
  }
}

/**
 * Implements device management via ADB
 */
trait DeviceManagement {
  this: Actor =>

  val storage: ActorRef

  val sessions = new HashMap[String, ActorRef]

  protected def deviceManagement: Receive = {
    case _ => ""
  }

  override def preStart() {
    context.system.eventStream.subscribe(this.self, classOf[DeviceConnected])
  }
}

/**
 * Implements device management via ADB
 */
trait JobManagement {
  this: Actor =>

  protected def jobManagement: Receive = {
    case _ => ""
  }
}

trait JobStorageFactory {
  this: Actor =>
  val storage = this.self
}