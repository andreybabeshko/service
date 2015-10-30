package dashboard.rest

import com.mongodb.casbah.Imports._
import com.novus.salat.global._
import dashboard.db.impl.StoreDAO
import dashboard.db.entity.StoreEntity

object StoreRest extends BaseRestApi{

  serve( "api" / "store" prefix {
    case  JsonGet(Nil, _) =>
      val storeList:Seq[String] = StoreDAO.primitiveProjections[String](MongoDBObject(), "title"):Seq[String]
      serialize(storeList.seq.distinct.map(s =>
        new ResponseStore(s.substring(0, s.indexOf("/", 8)).replaceAll("(http|https)://", ""),s)))

    case JsonGet("search" :: subString :: Nil, _) =>
     val storeList:Seq[StoreEntity] = StoreDAO.find(ref = MongoDBObject("title" -> MongoDBObject("$regex" -> subString, "$options" -> "si"))).limit(15).toList
    serialize(storeList.seq.distinct.map(s =>
      new ResponseStore(s.name,s.title)))
  })


  case class ResponseStore (
    name: String,
    url: String
  )
}
