package com.novoda.zoidberg

import com.android.chimpchat.core.IChimpDevice
import com.android.hierarchyviewerlib.device.ViewNode

class Device(chimp: IChimpDevice) {

  def property(what: String) = {
    chimp.getSystemProperty(what)
  }


  def views(f: ViewNode): Stream[ViewNode] = {
    if (f.children.isEmpty) {
      Stream(f)
    } else {
      f.children.toStream.map(views).flatten
    }
  }

  import collection.JavaConversions._
  def find(f: ViewNode)(s: String) = {
    views(f).filter(

      views(res105).foreach(a=> a.namedProperties.filterKeys(_ equalsIgnoreCase  "text:mText").foreach(n => println(n._2)))
        !_.namedProperties.filterKeys(_.eq("android:text")).isEmpty

    )
  }

  //
  //  def find(s: String): ViewNode = (v: ViewNode) => {
  //    v.properties.
  //  }
}