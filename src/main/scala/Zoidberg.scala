package com.novoda.zoidberg

import akka.kernel.Bootable
import akka.actor.{Props, Actor, ActorSystem}
import com.android.ddmlib.AndroidDebugBridge.{IDeviceChangeListener, IDebugBridgeChangeListener}
import com.android.ddmlib.{IDevice, AndroidDebugBridge}

case object Start

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

class DeviceManagerActor(adb: AndroidDebugBridge) extends Actor with ADBChangeListener with DeviceChangeListener {

  val worldActor = context.actorOf(Props[WorldActor])

  override def preStart() {
    AndroidDebugBridge.addDebugBridgeChangeListener(_)
    AndroidDebugBridge.addDeviceChangeListener(_)
    adb.getDevices.foreach(println)
    println("PRE")
  }


  override def postStop() {
    super.postStop()
//    AndroidDebugBridge.removeDebugBridgeChangeListener _
//    AndroidDebugBridge.removeDeviceChangeListener _
  }

  def receive = {
    case Start ⇒ {
      println("HELLO WORLD FROM PRINTLN")
      worldActor ! "Hello"
    }
    case message: String ⇒
      println("Received message '%s'" format message)
  }


  type DeviceStore = Map[String, IDevice]

}

class CommandListener extends Actor {
  val worldActor = context.actorOf(Props[WorldActor])

  def receive = {
    case Start ⇒ worldActor ! "Hello"
    case message: String ⇒
      println("Received message '%s'" format message)
  }
}

class WorldActor extends Actor {
  def receive = {
    case message: String ⇒ sender ! (message.toUpperCase + " world!")
  }
}

class ZoidbergKernel extends Bootable with DeviceChangeListener {

  val system = ActorSystem("zoidberg")

  lazy val androidHome = System.getenv("ANDROID_HOME")

  lazy val adb: AndroidDebugBridge = {
    AndroidDebugBridge.init(false)
    //AndroidDebugBridge.addDeviceChangeListener(this)
    AndroidDebugBridge.createBridge(androidHome + "/platform-tools/adb", true)
  }

  def startup = {
    system.actorOf(Props(new DeviceManagerActor(adb))) ! Start
    system.actorOf(Props[CommandListener]) ! Start
  }

  def shutdown = {
    AndroidDebugBridge.terminate()
    system.shutdown()
  }
}
