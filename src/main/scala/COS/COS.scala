package COS

import com.ibm.cloud.objectstorage.auth.{AWSCredentials, AWSStaticCredentialsProvider}
import com.ibm.cloud.objectstorage.client.builder.AwsClientBuilder.EndpointConfiguration
import com.ibm.cloud.objectstorage.oauth.BasicIBMOAuthCredentials
import com.ibm.cloud.objectstorage.services.s3.model.{Bucket, ListObjectsRequest, ObjectListing}
import com.ibm.cloud.objectstorage.services.s3.transfer.{TransferManager, TransferManagerBuilder, Upload}
import com.ibm.cloud.objectstorage.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.ibm.cloud.objectstorage.{ClientConfiguration, SdkClientException}

import java.io._
import java.net.URL
import java.util
import javax.crypto.{Cipher, CipherInputStream, KeyGenerator, SecretKey}

class COS {
  val credentials: Map[String, String] =
    JSIMPLE.parse("/home/ozne-otsugua/IdeaProjects/Automobiles Web Scrapping/cos_credentials.json")

  val bucketName: String = "automobiles-data"
  val newBucketName: String = "automobiles-data-v2"
  val apiKey: String = credentials("apikey")
  val serviceInstanceId: String = credentials("resource_instance_id")
  val endpointUrl: String = credentials("api_endpoint")
  val storageClass: String = credentials("storage_class")
  val location: String = credentials("location")

  val cosClient: AmazonS3 = createClient(apiKey, serviceInstanceId, endpointUrl, location)

  def createClient(apiKey: String, serviceInstanceId: String, endpointUrl: String, location: String): AmazonS3 = {
    val credentials: AWSCredentials = new BasicIBMOAuthCredentials(apiKey, serviceInstanceId)
    val clientConfig: ClientConfiguration = new ClientConfiguration()
      .withRequestTimeout(5000)
      .withTcpKeepAlive(true)

    AmazonS3ClientBuilder
      .standard
      .withCredentials(new
      AWSStaticCredentialsProvider(credentials))
      .withEndpointConfiguration(new EndpointConfiguration(endpointUrl, location))
      .withPathStyleAccessEnabled(true)
      .withClientConfiguration(clientConfig)
      .build
  }

  def listObjects(cosClient: AmazonS3,  bucketName: String): Unit = {
    println("Listing objects in bucket " + bucketName)

    val objectListing: ObjectListing = cosClient.listObjects(new ListObjectsRequest().withBucketName(bucketName))
    objectListing.getObjectSummaries.forEach(objectSummary =>
      println(" = " + objectSummary.getKey + "  " + "" + "(size = " + objectSummary.getSize + ")"))
    println
  }

  def createBucket(cosClient: AmazonS3, bucketName: String, storageClass: String): Unit = {
    cosClient.createBucket(bucketName, storageClass)
  }

  def listBuckets(cosClient: AmazonS3): Unit = {
    println("Listing buckets")
    val bucketList: util.List[Bucket] = cosClient.listBuckets
    bucketList.forEach(bucket => println(bucket.getName))
    println
  }

  def encryptStream(InputStream: InputStream): CipherInputStream = {
    // Generate Key
    val kgen: KeyGenerator = KeyGenerator.getInstance("AES")
    kgen.init(128)

    val aesKey: SecretKey = kgen.generateKey
    // Encrypt cipher

    val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, aesKey)

    val cis: CipherInputStream = new CipherInputStream(InputStream, cipher)
    cis
  }

  def uploadImage(id: String, url: String): Unit = {
    if (url.nonEmpty & url != "https://static.olx.com.br/cd/listing/notFound.png") {
      val imageUrl = new URL(url)

      val inputStream: InputStream = imageUrl.openStream

      val outputStream: OutputStream = new BufferedOutputStream(new FileOutputStream("CarImage.jpg"))

      do {
        val byte: Int = inputStream.read
        outputStream.write(byte)
      } while (inputStream.read != -1)

      outputStream.close()
      inputStream.close()

    } else {
      println("The url is empty or result in a not found url.")
    }

    val uploadFile: File = new File("CarImage.jpg")
    if (!uploadFile.isFile) {
      printf("The file '%s' does not exists or is not accessible.\n", "CarImage.jpg")
      return
    }

    val partSize: Int = 1024 * 1024 * 5
    val thresholdSize: Int = 1024 * 1024 * 5

    val transferManager: TransferManager = TransferManagerBuilder.standard()
      .withS3Client(cosClient)
      .withMinimumUploadPartSize(partSize)
      .withMultipartCopyThreshold(thresholdSize)
      .build

    try {
      val lrgUpload: Upload = transferManager.upload(bucketName, id, uploadFile)
      lrgUpload.waitForCompletion()

      println("Large file upload completed!")
    } catch {
      case e: SdkClientException => printf("Upload error: %s\n", e.getMessage)
    } finally {
      transferManager.shutdownNow()
    }
  }

}
