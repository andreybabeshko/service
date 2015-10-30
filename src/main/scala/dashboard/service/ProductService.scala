package dashboard.service

import com.mongodb.casbah.Imports._
import com.novus.salat.global._
import dashboard.db.entity.ProductEntity
import dashboard.db.impl.ProductDAO
import dashboard.model.Product

object ProductService {

  def getAllProducts:Seq[Product] = {
    var products:Seq[Product] = Seq[Product]()
    val productStringList:Seq[String] = ProductDAO.primitiveProjections[String](MongoDBObject(), "text"):Seq[String]
    for{
      store <- productStringList.distinct
      raw <- store.split("\\n")
    } {
      var first = false
      if (raw.contains("Aligned:")) {
        val productName = for {productPartName <- raw.replaceAll("Aligned:", "").split("\\t")
          } yield {
            if (productPartName.contains("£") || productPartName.contains("$") || first) {
              first = true
              ""
            }
            else productPartName
          }
        products :+= new Product(productName.mkString, raw.replaceAll("\\t",""))
      }
    }
    products
  }

  def searchProducts (subString:String) :Seq[Product]  = {
    var products:Seq[Product] = Seq[Product]()
    val productEntityList:Seq[ProductEntity] = ProductDAO.find(ref = MongoDBObject("text" -> MongoDBObject("$regex" -> subString, "$options" -> "si"))).toList
    for{
      store <- productEntityList
      raw <- store.text.split("\\n")
    } {
      var first = false
      if (raw.contains("Aligned:") && raw.contains(subString)) {
        val productName = for {productPartName <- raw.replaceAll("Aligned:", "").split("\\t")
        } yield {
            if (productPartName.contains("£") || productPartName.contains("$") || first) {
              first = true
              ""
            }
            else productPartName
          }
        products :+= new Product(productName.mkString, raw.replaceAll("\\t",""))
      }
    }
    products.distinct
  }

  def searchProductsWithPrices (subString:String) :Map[String,Map[Long, Float]]  = {
    var products:Map[String,Map[Long, Float]] = Map[String, Map[Long, Float]]()
    val productEntityList:Seq[ProductEntity] = ProductDAO.find(ref = MongoDBObject("$text" -> MongoDBObject("$search" -> subString))).toList
    for{
      store <- productEntityList
      raw <- store.text.split("\\n")
    } {
      var first = false
      var price = 0.00f
      if (raw.contains("Aligned:") && raw.contains(subString)) {
        val productName = for {productPartName <- raw.replaceAll("Aligned:", "").split("\\t")
        } yield {
            if (productPartName.contains("£") || productPartName.contains("$") || first) {
              first = true
              price = productPartName.replaceAll("($|£)","").toFloat
              ""
            }
            else productPartName
          }
        val current = products.get(productName.mkString).getOrElse(Map[Long, Float]()).+(store.fetchTime -> price)
        products = products.+(productName.mkString -> current)
      }
    }
    products
  }
}
