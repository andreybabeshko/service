package dashboard.utils

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.typesafe.scalalogging.slf4j.Logging

import scala.reflect.ClassTag

trait BaseJacksonSerializer extends Logging {
  protected val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  mapper.registerModule(new JodaModule)
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  val module = new SimpleModule()
  initCustomModule(module)
  mapper.registerModule(module)

  def serialize(value: Any): String = mapper.writeValueAsString(value)

  def deserialize[T: ClassTag](value: String): T =
    mapper.readValue(value, implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]])

  protected def initCustomModule(module: SimpleModule) = {}
  
  def deserialize[T: ClassTag](typeRef: TypeReference[T], value: String): T =
    mapper.readValue(value, typeRef)
}

object JacksonSerializer extends BaseJacksonSerializer

object JacksonLeanSerializer extends BaseJacksonSerializer {
  mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false)
  mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
}
