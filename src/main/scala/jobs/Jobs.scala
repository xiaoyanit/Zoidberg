package jobs

sealed trait Job
case class Shell(s: String) extends Job
case class Monkey(s:String, s2:String) extends  Job
