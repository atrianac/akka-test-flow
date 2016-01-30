package com.helios.flow.actors

import javax.jms.Message
import javax.jms.MessageListener

object MessageConversion {
  implicit def convertMessage(fn: Message => Unit) = {
    new MessageListener() {
      def onMessage(msg: Message) = fn(msg)
    }
  }
}