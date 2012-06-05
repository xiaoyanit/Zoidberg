package novoda.zoidberg.device

import com.android.hierarchyviewerlib.device.ViewNode
import com.android.hierarchyviewerlib.device.ViewNode.Property
import com.android.ddmlib.{IShellOutputReceiver, IDevice}
import akka.actor.Actor
import com.android.chimpchat.hierarchyviewer.HierarchyViewer
import com.android.ddmlib.log.LogReceiver

class RichDevice(val device: IDevice) extends Actor with Shellable {
  def receive = {
    case _ => ""
  }
}

object RichDevice {
  implicit def toRichDevice(device: IDevice) = new RichDevice(device)

  implicit def toLowerDevice(device: RichDevice) = device.device
}

trait Shellable {
  this: RichDevice with Actor =>

  type ShellOutput[T] = IShellOutputReceiver with T

  def shell[T](command: String)(receiver: ShellOutput[T]): T = {
    device.executeShellCommand(command, receiver)
    receiver.asInstanceOf[T]
  }
}

trait Viewable {
  this: RichDevice =>

  import collection.JavaConversions._

  lazy val hierarchyViewer = new HierarchyViewer(this)

  def views(f: ViewNode): Stream[ViewNode] =
    if (f.children.isEmpty) Stream(f) else f.children.toStream.map(views).flatten

  def find(f: ViewNode) = views(f).filter(_.properties.exists(isTextView))

  def isTextView = (prop: Property) => prop.name == "text:mText"

  class RichViewProperty(prop: Property) {
    def isTextView = prop.name == "text:mText"
  }
}

trait Loggable {
  this: RichDevice =>

  def log (to: A) {
    this.device.runEventLogService(LogReceiver)
  }
}