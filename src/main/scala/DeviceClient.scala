package com.novoda.zoidberg

import akka.actor.Actor

class DeviceClient(val name: String) {
//  val chat = Actor.remote.actorFor("device:service", "localhost", 2552)
//
//  def login = chat ! Login(name)
//
//  def logout = chat ! Logout(name)
//
//  def post(message: String) = chat ! ChatMessage(name, name + ": " + message)

  //def chatLog = (chat ? GetChatLog(name)).as[ChatLog].getOrElse(throw new Exception("Couldn't get the chat log from ChatServer"))
}