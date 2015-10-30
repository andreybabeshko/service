package dashboard.db.impl

import dashboard.db.entity.ProductEntity
import com.novus.salat.global._
import com.mongodb.casbah.Imports._

object ProductDAO extends BasicDAO[ProductEntity, ObjectId](collectionName = "webpage")
