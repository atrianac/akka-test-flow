package com.helios.flow.actors

import akka.actor.Actor
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.model.GetObjectRequest
import akka.stream.io.InputStreamSource

case class InitDownload(pathFile: String)

class DownloadResourceActor extends Actor {
  
  val accessKey = "AKIAJJS5V3RKWGHMET6Q";
  
  val secretKey = "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";
  
  def receive = {
    case InitDownload(pathFile) => {
       val s3Object = s3Client.getObject(new GetObjectRequest("pablo-proto-akka-dictionaries", pathFile));
       val objectContent = s3Object.getObjectContent();
       
       val fileStream = InputStreamSource(() => objectContent, 128).map { x => ??? };

    }
  }
  
  lazy val s3Client = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey))
  
}