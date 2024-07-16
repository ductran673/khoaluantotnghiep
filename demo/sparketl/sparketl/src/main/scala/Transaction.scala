import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions._

object Ingestion {
  def main(args: Array[String]) = {
    // get argument 
    var executionDate = ""
    args.sliding(2,2).toList.collect {
         case Array("--executionDate : String) => executionDate = argExecutionDate
    }

    // Extract year, month, day from executionDate
    val runTime = executionDate.split("-")
    val year = runTime(0)
    val month = runTime(1)
    val day = runTime(2)

    // Create Spark session
    val spark = SparkSession
      .builder()
      .appName("BAO CAO HOAT DONG SAN XUAT HANG NGAY")
      .config("hive.metastore.uris", "thrift://localhost:9083")
      .config("hive.exec.dynamic.partition", "true")
      .config("hive.exec.dynamic.partition.mode", "nonstrict")
      .enableHiveSupport()
      .getOrCreate()
   //load data to spark df
    val orderDF = spark.read.parquet("hdfs://localhost:9000/datalake/ProductOrders").drop("year","month","day")
    val orderDetailDF = spark.read.parquet("hdfs://localhost:9000/datalake/OrderActivities").drop("year","month","day")
    val orderDetailDF = spark.read.parquet("hdfs://localhost:9000/datalake/SKUs").drop("year","month","day","created_at")
    val orderDetailDF = spark.read.parquet("hdfs://localhost:9000/datalake/DimLine").drop("year","month","day")

   //join data frame
    val preDF = orderDF
        .filter(orderDF("created_at") === executionDate)
        .join(orderDetailDF, ordersDF("id") === orderDetailDF("order_id"), "inner")
        .join(productsDF, ordersDF("id") === productsDF("order_id"), "inner")
        .join(inventoryDF.select(col("quantity") as "inv_quantity", col("id")), productsDF(inventory_id) ===inventoryDF("id"), "inner")
   //aggregate data
    val mapDF = preDF.groupBy("Make", "Model", "Category", "product_id", "Inv_quantity")
        .agg(
            sum("quantity").as("Sales"),
            sum("total").as("Revenue")
          )
   // prepare result
    val resultDF = mapDF
       .withColumn("LeftOver", col("inv_quantity") - col("Sales"))
       .withColumn("year", lit(year))
       .withColumn("month", lit(month))
       .withColumn("day", lit(day))
       .select("Make", "Model", "Category", "ProductionQuantity", "year", "month", "day")
  // write to data warehouse
    resultDF.write
       .format("hive")
       .partitionBy("year","month","day")
       .mode(SaveMode.Append)
       .saveAsTable("reports.daily_product_producing")
      }
}
