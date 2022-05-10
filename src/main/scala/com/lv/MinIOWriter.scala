package com.lv

import io.minio.{BucketExistsArgs, MakeBucketArgs, MinioClient, PutObjectArgs}

import scala.util.Random
import ru.tinkoff.phobos.encoding._
import java.io.ByteArrayInputStream

import models._

/**
 * mvn scala:run -DmainClass=com.lv.MinIOWriter -DaddArgs=5
 */
object MinIOWriter extends App {

  if (confR.isLeft) {
    confR.left.foreach(failure => println(failure.prettyPrint()))
    System.exit(0)
  }

  val conf = confR.right.get
  val bucketName = conf.bucketName
  val accessKey = conf.accessKey
  val secretKey = conf.secretKey
  val endpoint = conf.endpoint

  val n = if (args.length == 0) 10 else { args(0).toInt }

  val minioClient = MinioClient.builder
    .endpoint(endpoint)
    .credentials(accessKey, secretKey)
    .build

  // Make bucket if not exist.
  val found = minioClient.bucketExists(BucketExistsArgs.builder.bucket(bucketName).build)
  if (!found) minioClient.makeBucket(MakeBucketArgs.builder.bucket(bucketName).build)
  else println(s"Bucket $bucketName already exists.")

  (1 to n).foreach(i => {
    // Autogenerate XML file
    val pIdSeq = f"$i%04d"
    val player = Player(s"21380$pIdSeq", totalAmnt(), (1 to Random.nextInt(10)).map(_ => createTransaction()).toList)
    val audit = AuditFile(player)
    val xml: String = XmlEncoder[AuditFile].encode(audit)
    val fNameSeq = f"$i%02d"
    val fileName = f"xmls/year=2022/month=05/Aleovegas_3_1_00000000${fNameSeq}_202205091022$fNameSeq.xml"
    println(s"$fileName - $xml")
    val bais = new ByteArrayInputStream(xml.getBytes("UTF-8"))

    // Put XML in MinIO
    val putObj = PutObjectArgs.builder
      .bucket(bucketName)
      .`object`(fileName)
      .stream(bais, bais.available, -1)

    minioClient.putObject(putObj.build)
    bais.close()
  })
  println("Finished!")
}
