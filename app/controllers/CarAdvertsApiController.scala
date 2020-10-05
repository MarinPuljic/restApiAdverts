package controllers

import io.circe.syntax.EncoderOps
import javax.inject.{Inject, Singleton}
import models.Adverts
import play.api.libs.circe.Circe
import play.api.mvc._
import repositories.AdvertsRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CarAdvertsApiController @Inject()
(implicit ec: ExecutionContext,
 cc: ControllerComponents,
 advertsRepo: AdvertsRepository ,
)
  extends AbstractController(cc)
  with Circe
{
  def getAll = Action.async {
    advertsRepo.list().map { post =>
      Ok(post.sortBy( _.id).asJson)
    }
  }

  def create = Action.async(parse.json) {
    _.body
      .validate[Adverts]
      .map { post =>
        advertsRepo.create(post).map {
           _ => Created
        }
      }
      .getOrElse {
        Future.successful(BadRequest("This is returned if json is invalid or cannot be parsed."))
        Future.successful(UnprocessableEntity("Validation failed."))
      }
  }

  def getById(id: Int) = Action.async {
    advertsRepo.read(id).map { maybePost =>
      maybePost.map { post =>
        Ok(post.asJson)
      }.getOrElse(NotFound)//("No car advert with given id was found.")
    }
  }

  def update(id: Int) = Action.async(parse.json)
  {
    _.body
      .validate[Adverts]
      .map { post =>
        advertsRepo.update(id, post).map {
          case Some(post) => Ok(post.asJson)
          case _          => NotFound
        }
      }.getOrElse {
        Future.successful(NotFound("This is returned if a car advert with given id is not found."))
        Future.successful(BadRequest("This is returned if json is invalid or cannot be parsed."))
        Future.successful(UnprocessableEntity("Validation failed."))
    }
  }

  def delete(id: Int) = Action.async {
    advertsRepo.delete(id).map {
      case Some(post) => Ok(post.asJson)
      case _          => NotFound//("This is returned if a car advert with given id is not found.")
    }
  }

}
