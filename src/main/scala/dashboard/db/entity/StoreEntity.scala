package dashboard.db.entity

import com.novus.salat.annotations._

case class StoreEntity (@Key("_id") id: Option[String] , title: String) {
  @transient lazy val name = title.replaceAll("(http|https)://", "").substring(0, title.indexOf("/", 8))
}
