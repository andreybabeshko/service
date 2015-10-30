package dashboard.rest

import dashboard.utils.JacksonSerializer
import net.liftweb.http.rest.{RestContinuation, RestHelper}
import net.liftweb.http.{InternalServerErrorResponse, LiftResponse, PlainTextResponse}
import net.liftweb.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.reflect.ClassTag
import scala.util.{Failure, Success}

trait BaseRestApi extends RestHelper {

  def serialize(value: Any) =  PlainTextResponse(JacksonSerializer.serialize(value))
  def deserialize[T: ClassTag](jValue: JValue) = JacksonSerializer.deserialize(compact(render(jValue)))

  case class Response[T](result: String, response: Option[T] = None)

  object Response{
    def success[T](response: T) = Response("success", Some(response))
    def failure[T](errors: Seq[String]): Response[T] = Response("failure")

    def apply[T](e: Either[List[String], T]): Response[T] = e.fold(failure[T], success)
  }

  /** NOTE: Don't use session specific variables inside f callback **/
  def continuationResponse[T](future: Future[T])(f: T => LiftResponse) =
    RestContinuation.async { reply =>
      future.onComplete {
        case Success(x) => reply(f(x))
        case Failure(t) => reply(InternalServerErrorResponse())
      }
    }


}
