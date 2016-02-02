package com.helios.flow.actors

import akka.actor.Actor
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.model.GetObjectRequest
import akka.stream.io.InputStreamSource
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider
import com.amazonaws.services.s3.model.S3Object
import akka.stream.scaladsl.Source
import scala.concurrent.Future
import java.io.InputStream
import akka.util.ByteString
import akka.actor.ActorLogging


object DownloadResourceActor {
  case class InitDownload(message: S3Message)
  case class NotifyStream(stream: Source[ByteString, Future[Long]])
  
  class S3Message(val bucketName: String, val key: String)
    
}

class DownloadResourceActor extends Actor with ActorLogging {
  
  import DownloadResourceActor._
  
  val chunk = 128
  
  def receive = {
    
    case InitDownload(message) => {
       
       val objectContent = getS3ObjectAsInputStream(getS3Object(message.bucketName, message.key))
       val stream =  getS3AsStream(objectContent)
       
       sender ! NotifyStream(stream)

    }
  }
  
  def getS3AsStream(in: InputStream) = InputStreamSource(() => in, chunk)
  
  def getS3ObjectAsInputStream(s3Object: S3Object) = s3Object.getObjectContent
  
  def getS3Object(bucketName: String, key: String) = s3Client.getObject(new GetObjectRequest(bucketName, key))
  
  lazy val s3Client = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider)
  
}