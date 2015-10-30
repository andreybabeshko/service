package dashboard.rest

import com.mongodb.casbah.Imports._
import dashboard.service.ProductService


object ProductRest extends BaseRestApi {

  serve( "api" / "product" prefix {
    case  JsonGet(Nil, _) =>
      serialize(ProductService.getAllProducts)

    case JsonGet("search" :: subString :: Nil, _) =>
      serialize(ProductService.searchProducts(subString))

    case JsonGet("prices" :: product :: Nil, _) =>
      serialize(ProductService.searchProductsWithPrices(product.replaceAll("%20", " "))
        .map(p => new ResponseProductPrice(p._1, true, p._2)))

    case JsonGet("prices" :: product :: store ::Nil, _) =>
      serialize(ProductService.searchProductsWithPrices(product.replaceAll("%20", " "), store.replaceAll("%20", " "))
        .map(p => new ResponseProductPrice(p._1, false, p._2)))
  })

  case class ResponseProductPrice (
   name:  String,
   store: Boolean,
   price: Seq[Seq[Any]]
  )

}
