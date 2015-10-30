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
      serialize(ProductService.searchProductsWithPrices(product.replaceAll("%20", " ")))
  })

}
