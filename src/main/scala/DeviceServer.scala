package typedactordemo

import akka.actor._
import akka.dispatch.Future
import akka.pattern.ask
import akka.util.Timeout
import akka.util.duration._
import com.novoda.zoidberg.Device
import com.android.chimpchat.ChimpChat

case class Request(payload: String)

case class Response(payload: Device)

trait Service {
  def request(r: Request): Future[Response]
}

class ServiceImpl extends Service {
  val actor = {
    val ctx = TypedActor.context
    ctx.actorOf(Props[ServiceActor])
  }

  implicit val timeout = Timeout(10 seconds)

  def request(req: Request): Future[Response] = (actor ? req).mapTo[Response]
}

class ServiceActor extends Actor {
  def receive = {
    case Request(payload) => {
      val d = new Device(ChimpChat.getInstance.waitForConnection)
      sender ! Response(d)
    }
  }
}


object Main extends App {
  val system = ActorSystem("TypedActorDemo")

  val service: Service =
    TypedActor(system).typedActorOf(
      TypedProps[ServiceImpl]()
    )

  val req = Request("hello world!")

  service.request(req) onSuccess {
    case Response(response) =>
      println(response)
      println(response.property("ro.build.version.incremental"))
      system.shutdown()
  } onFailure ({
    case _ => println("TEST")
  })
}
