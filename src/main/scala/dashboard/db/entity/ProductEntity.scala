package dashboard.db.entity

import com.novus.salat.annotations.raw._

case class ProductEntity (@Key("_id") id: Option[String] , text: String, prevFetchTime: Long, title:String) {
} 