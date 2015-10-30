package dashboard.db.impl

import com.mongodb.casbah.MongoURI
import com.novus.salat.dao._
import com.novus.salat.global._
import com.novus.salat._
import com.mongodb.casbah.Imports._


class BasicDAO[ObjectType <: AnyRef, ID <: Any](collectionName: String)(implicit mot: Manifest[ObjectType], mid: Manifest[ID], ctx: Context) extends
  SalatDAO[ObjectType, ID](collection = MongoConnection(MongoURI("mongodb://userAdminSite:password@52.30.167.36:27017/nutch"))("nutch")(collectionName))(mot, mid, ctx)
