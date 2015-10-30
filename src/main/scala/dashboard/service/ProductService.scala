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
        products :+= new Product(productName.mkString.trim, "")
      }
    }
    products.distinct
  }

  def searchProductsWithPrices (subString:String) :Map[String,Seq[Seq[Any]]]  = {
    var products:Map[String, Seq[Seq[Any]]] = Map[String,Seq[Seq[Any]]]()
    val productEntityList:Seq[ProductEntity] = ProductDAO.find(ref = MongoDBObject("$text" -> MongoDBObject("$search" -> subString))).toList
    for{
      store <- productEntityList
      raw <- store.text.split("\\n")
    } {
      var first = false
      var price = 0.00f
      if (raw.contains("Aligned:") && raw.replaceAll("\\t","").contains(subString)) {
        val productName = for {
          productPartName <- raw.replaceAll("Aligned:", "").split("\\t")
        } yield {
            if (productPartName.contains("£") || productPartName.contains("$") || first) {
              first = true
              price = productPartName.replaceAll("($|£)","").toFloat
              ""
            }
            else productPartName
          }
        val current:Seq[Seq[Any]] = products.get(productName.mkString).getOrElse(Seq[Seq[Seq[Any]]]()).++(Seq(Seq(getStoreName(store.title), price)))
        products = products.+(productName.mkString.trim -> current)
      }
    }
    products
  }

  def searchProductsWithPrices (subString: String, filteStore: String) :Map[String,Seq[Seq[Any]]]  = {
    var products:Map[String, Seq[Seq[Any]]] = Map[String, Seq[Seq[Any]]]()
    val productEntityList:Seq[ProductEntity] = ProductDAO.find(ref = MongoDBObject("$text" -> MongoDBObject("$search" -> subString))).toList
    for{
      store <- productEntityList.filter(p => getStoreName(p.title).contains(filteStore))
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
        val current:Seq[Seq[Any]] = products.get(productName.mkString).getOrElse(Seq[Seq[Any]]()).++(Seq(Seq(store.fetchTime, price)))
        products = products.+(productName.mkString.trim -> current)
      }
    }
    products
  }

  def getStoreName(url: String): String = {
    url.substring(0, url.indexOf("/", 8)).replaceAll("(http|https)://", "")
  }
}
