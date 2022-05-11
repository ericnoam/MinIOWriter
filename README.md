# MinIOWriter
This simple program auto-generates XML strings resembling the taxAccountReport and writes them to MinIO.

Its intended as a simple PoC to show how using the MinIO Java API we can write 8with the same code) to both MinIO,
Google Cloud Storage, AWS S3 and any S3-compatible object storage.

Although it's written in Scala, the code is simple enough to be easily translatable to Java or Kotlin.

## MinIO Docker

```shell
mkdir -p ~/minio/data

docker run
  -p 9000:9000
  -p 9001:9001
  --name minio1
  -v ~/minio/data:/data minio/minio
  server /data
  --console-address ":9001"
```
Note that the local directory `~/minio/data` is passed as a volume to the container. See [MinIO Docker Quickstart Guide](https://docs.min.io/docs/minio-docker-quickstart-guide) for details.

Also note that features such as versioning, object locking, and bucket replication
require **distributed deploying** MinIO with Erasure Coding. See [MinIO Quickstart Guide](https://docs.min.io/docs/minio-quickstart-guide.html) for details.

## Zeppelin Docker
```shell
docker run -u $(id -u) -p 8080:8080 -d
  -v $PWD/logs:/logs 
  -v $PWD/notebook:/notebook
  -v /usr/local/spark:/opt/spark
  -e SPARK_HOME=/opt/spark
  -e ZEPPELIN_LOG_DIR='/logs' 
  -e ZEPPELIN_NOTEBOOK_DIR='/notebook' 
  --name zeppelin apache/zeppelin:0.10.1
```
Note that `logs` and `notebooks` are persisted in a local volume. See [using the official docker image](https://zeppelin.apache.org/docs/latest/quickstart/install.html#using-the-official-docker-image) for details.

### Zeppelin Conf
Go to `Interpreter` and search for `spark`:

![Zeppelin Interpreters Configuration](/imgs/zepconf.png)

and `Add` the following properties:
```properties
spark.hadoop.fs.s3a.endpoint	172.17.0.3:9000
spark.hadoop.fs.s3a.access.key	roguedev1
spark.hadoop.fs.s3a.secret.key	shellaccess
spark.hadoop.fs.s3a.path.style.access	true
spark.hadoop.fs.s3a.impl	org.apache.hadoop.fs.s3a.S3AFileSystem
spark.hadoop.fs.s3a.aws.credentials.provider	org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider
spark.hadoop.fs.s3a.connection.ssl.enabled	false
```

Add to `spark.jars.packages ` the following coordinates:
```groovy
org.apache.hadoop:hadoop-aws:3.2.2,com.databricks:spark-xml_2.12:0.14.0
```

## MinIO XML Writer
To run and create 10 XML files (the default):
```shell
mvn scala:run -DmainClass=com.lv.MinIOWriter
```

You can pass the number of files to generate as an argument:
```shell
mvn scala:run -DmainClass=com.lv.MinIOWriter -DaddArgs=5
```

Endpoint, bucketname and auth properties can be configured on `src/main/resources/application.conf`

## Concept

![Big Query Architecture](/imgs/bqarch.png)

* __Dremel__ -> Compute engine for parallel SQL Queries (_Spark_)
* __Colossus__ -> Columnar Storage format (_Parquet_)
* __Jupiter__ -> Petabit network for fast shuffles (_Spark over TCP_)
* __Borg__ -> Hardware resources allocation (_Mesos/K8s_)
* __UI__ -> [Zeppelin](https://zeppelin.apache.org/), [Trino](https://trino.io/)

