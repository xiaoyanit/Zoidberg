import akka.actor._
import akka.event.Logging
import akka.pattern.ask
import akka.util.duration._
import akka.util.Timeout
import collection.immutable.HashMap

case object Tick

case object Get

class Counter extends Actor {
  var count = 0

  def receive = {
    case Tick => count += 1
    case Get => sender ! count
  }
}

object Zoidberg extends App {
  val system = ActorSystem("Zoidberg")

  val counter = system.actorOf(Props[Counter])

  counter ! Tick
  counter ! Tick
  counter ! Tick

  implicit val timeout = Timeout(5 seconds)

  (counter ? Get) onSuccess {
    case count => println("Count is " + count)
  }

  system.shutdown()
}

sealed trait Event

case class Login(user: String) extends Event

case class Logout(user: String) extends Event

case class GetChatLog(from: String) extends Event

case class ChatLog(log: List[String]) extends Event

case class ChatMessage(from: String, message: String) extends Event

trait DeviceServer extends Actor {

}

//trait DeviceManagement {
//  this: Actor =>
//  val log = Logging(context.system, this)
//  val storage: ActorRef
//  val sessions = new HashMap[String, ActorRef]
//
//  protected def sessionManagement: Receive = {
//    case Login(username) =>
//      log.info(this, "User [%s] has logged in".format(username))
//      val session = actorOf(new Session(username, storage))
//      session.start()
//      sessions += (username -> session)
//
//    case Logout(username) =>
//      log.info(this, "User [%s] has logged out".format(username))
//      val session = sessions(username)
//      session.stop()
//      sessions -= username
//  }
//
//  protected def shutdownSessions = sessions.foreach {
//    case (_, session) => session.stop()
//  }
//
//}
