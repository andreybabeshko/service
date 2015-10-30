package dashboard.db.entity

import com.novus.salat.annotations.raw.Key

case class ProductEntity (@Key("_id") id: Option[String] , text: String, fetchTime: Long, title:String) {
} 