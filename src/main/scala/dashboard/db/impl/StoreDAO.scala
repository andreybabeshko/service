package dashboard.db.impl

import dashboard.db.entity.StoreEntity
import com.mongodb.casbah.Imports._
import com.novus.salat.global._



object StoreDAO extends BasicDAO[StoreEntity, ObjectId](collectionName = "webpage")
