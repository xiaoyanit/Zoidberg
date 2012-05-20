package viewserver

import com.android.hierarchyviewerlib.device.ViewNode
import com.android.hierarchyviewerlib.device.ViewNode.Property

import collection.JavaConversions._
import collection.TraversableLike

class RichViewNode(node: ViewNode) extends Traversable[ViewNode] {

  def foreach[U](f: ViewNode => U) = views(node).foreach(f)

  def text: Option[String] = node.properties.find(isPropTextView) match {
    case Some(prop) => Some(prop.value)
    case _ => None
  }

  def views(f: ViewNode): Stream[ViewNode] = {
    if (f.children.isEmpty) {
      Stream(f)
    } else {
      f.children.toStream.map(views).flatten
    }
  }

  def isTextView = node.properties.exists(isPropTextView)

  def isPropTextView = (prop: Property) => prop.name == "text:mText"
}

object RichViewNode {
  implicit def toRichViewNode(node: ViewNode) = new RichViewNode(node)
}

