package novoda.android.zoidberg

import akka.actor.{Props, ActorSystem, ActorRef, Actor}
import novoda.zoidberg.akka.{DeviceChanged, DeviceDisconnected, DeviceStoreExtension, DeviceConnected}
import collection.immutable.{Queue, HashMap}
import akka.dispatch.Future
import akka.util.Timeout
import collection.parallel.mutable
import com.android.ddmlib.IDevice


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

  val devices = new HashMap[String, ActorRef]

  protected def deviceManagement: Receive = {
    case DeviceConnected(d) => {
      println("Device connected: " + d.getSerialNumber)
    }
    case DeviceDisconnected(d) => println("Device disconnected: " + d.getSerialNumber)
    case DeviceChanged(d, _) => println("Device changed: " + d.getSerialNumber)
  }

  override def preStart() {
    // Register callback on ADB
    subscribe(classOf[DeviceConnected])
    subscribe(classOf[DeviceDisconnected])
    subscribe(classOf[DeviceChanged])
  }

  def subscribe = (c: Class[_]) => context.system.eventStream.subscribe(this.self, c)
}


sealed trait Job

case class Shell(s: String) extends Job

case class Monkey(s: String, s2: String) extends Job

/**
 * Implements device management via ADB
 */
trait JobManagement {
  this: Actor =>

  val jobs = collection.mutable.Queue[Job]()

  protected def jobManagement: Receive = {
    case Shell(command) => jobs.enqueue(Shell(command));
    case GetJob => sender ! jobs.dequeue()
    case _ => println("hello from " + sender)
  }

  override def preStart() {
    jobs.enqueue(Shell("ls"))
    println("enqueuein shell " + jobs.size)
  }

  override def postStop() {
    println("stop" + jobs.size)
  }

}

trait JobStorageFactory {
  this: Actor =>
  val storage = this.self
}

case object GetJob

import akka.pattern.ask
import akka.util.duration._

object ActorApp extends App {
  val system = ActorSystem("DeviceService")
  DeviceStoreExtension(system).start()
  val ds = system.actorOf(Props[DeviceService], name = "deviceActor")
  //ds ! Shell("ls -l")
  ds ! "hello"
  implicit val timeout = Timeout(5 seconds)
  val future: Future[Job] = ask(ds, GetJob).mapTo[Job]
  future.onComplete(println)
  // system.shutdown()
}

import novoda.zoidberg.device.RichDevice._

class Device(device: IDevice) extends Actor {

  def receive = {
    case Shell(command) => sender ! device.shell(command)
  }
}
