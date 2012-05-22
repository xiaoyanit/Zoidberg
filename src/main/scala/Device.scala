package com.novoda.zoidberg

import com.android.ddmlib.IDevice
import com.android.hierarchyviewerlib.device.ViewNode
import com.android.hierarchyviewerlib.device.ViewNode.Property

class RichDevice(device: IDevice) {

  def views() {
    //    DeviceBridge.startViewServer(device)
    //    DeviceBridge.loadWindows(device)
  }
}

object RichDevice {
  implicit def toRichDevice(device: IDevice) = new RichDevice(device)
}


class Device(javaDevice: IDevice) {
  def property(what: String) = {
  }


  import collection.JavaConversions._

  def views(f: ViewNode): Stream[ViewNode] = {
    if (f.children.isEmpty) {
      Stream(f)
    } else {
      f.children.toStream.map(views).flatten
    }
  }

  def find(f: ViewNode) = views(f).filter(_.properties.exists(isTextView))

  def isTextView = (prop: Property) => prop.name == "text:mText"

  class RichViewProperty(prop: Property) {
    def isTextView = prop.name == "text:mText"
  }

  //  def find(f: ViewNode)(s: String) = {
  //    views(f).filter(
  //
  ////      views(res105).foreach(a => a.namedProperties.filterKeys(_ equalsIgnoreCase "text:mText").foreach(n => println(n._2)))
  ////        ! _.namedProperties.filterKeys(_.eq("android:text")).isEmpty
  //
  //    )
  //  }

  //
  //  def find(s: String): ViewNode = (v: ViewNode) => {
  //    v.properties.
  //  }
}