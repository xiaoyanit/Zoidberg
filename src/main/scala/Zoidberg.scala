package com.novoda.zoidberg

import akka.kernel.Bootable
import akka.actor.{Props, Actor, ActorSystem}
import novoda.zoidberg.akka.{DeviceConnected, DeviceStoreExtension}

class ZoidbergKernel extends Bootable {

  val system = ActorSystem("zoidberg")

  lazy val androidHome = System.getenv("ANDROID_HOME")

  def startup = {
    DeviceStoreExtension(system).start()
    val listener = system.actorOf(Props(new Actor {
      def receive = {
        case DeviceConnected(a) => println("Device connected: " + a.getSerialNumber)
      }
    }))
    system.eventStream.subscribe(listener, classOf[DeviceConnected])
  }

  def shutdown = {
    DeviceStoreExtension(system).stop()
    system.shutdown()
  }
}


// val actor = context.actorFor("akka://zoidberg@10.0.0.1:2552/user/devices/E2-2112")
// actor ! Monkey(...)
// actor ! Install(file)
//