import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.{lit, max}

object Ingestion {
  def main(args: Array[String]): Unit = {
    // receive 
    if (args.length < 2) {
      println("Yo, I need two arguments !")
    }
    
    var tblName = ""
    var executionDate = ""
    
    // Parse command-line arguments
    args.sliding(2, 2).toList.collect {
      case Array("--tblName", argTblName: String) => tblName = argTblName
      case Array("--executionDate", argExecutionDate: String) => executionDate = argExecutionDate
    }
    
    // Split executionDate into year, month, day
    val runTime = executionDate.split("-")
    val year = runTime(0)
    val month = runTime(1)
    val day = runTime(2)
    
    // Create Spark session
    val spark = SparkSession
      .builder()
      .appName("Ingestion - from MYSQL to HIVE")
      .getOrCreate()
    
   // get the latest record_id in data lake
    val conf = spark.sparkContext.hadoopConfiguration
    val fs = org.apache.hadoop.fs.FileSystem.get(conf)
    val exists = fs.exists(new org.apache.hadoop.fs.Path(s"/datalake/$tblName"))
    val tblQuery = ""
    if (exists) {
       val df = spark.read.parquet(tblLocation)
       val record_id = df.agg(max("id")).head().getLong(0)
       tblQuery = s"(SELECT * FROM `$tblName` WHERE id > $record_id) tmp"
     } else {
       tblQuery = s"(SELECT * FROM `$tblName` AS tmp"
    }
  
  // get the latest records from MySQL 
    val jdbcDF = spark.read.formal("jdbc").options(
     Map("url" -> "jdbc:mysql://localhost:3306/luan_van_cua_duc?user=root&password=password",
         "fetchSize" -> tblQuery,
        )).load()
    
   // save to datalake
    val outputDF = jdbcDF.withColumn("year", lit(year)).withColumn("month", lit(month)).withColumn("day", lit(day))
    outputDF.write.partitionBy("year","month","day").mode(SaveMode.Append).parquet(tblLocation)
  }
}

