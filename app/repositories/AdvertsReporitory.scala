package repositories

import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.api.{Cursor, ReadPreference}
import models.Adverts
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.BSONDocument

class  AdvertsRepository @Inject()
(implicit ec: ExecutionContext,
 reactiveMongoApi: ReactiveMongoApi)
{
  private def collection: Future[JSONCollection] =
    reactiveMongoApi.database.map((_.collection(("adverts"))))

  def list(limit: Int = 100): Future[Seq[Adverts]] =
    collection.flatMap(
      _.find(BSONDocument())
        .cursor[Adverts](ReadPreference.primary)
        .collect[Seq](limit, Cursor.FailOnError[Seq[Adverts]]())
  )

    def create(adverts: Adverts): Future[WriteResult] =
      collection.flatMap(_.insert(adverts))

    def read(id: Int): Future[Option[Adverts]] =
      collection.flatMap(_.find(BSONDocument("id" -> id)).one[Adverts])

    def update(id: Int, adverts: Adverts): Future[Option[Adverts]] =
      collection.flatMap(
        _.findAndUpdate(
          BSONDocument("id" -> id)
          , BSONDocument(
            f"$$set" -> BSONDocument(
              "title" -> adverts.title,
              "fuelType" -> adverts.fuelType,
              "price" -> adverts.price,
              "isNew" -> adverts.isNew,
              "mileage" -> adverts.mileage,
              "firstRegistration" -> adverts.firstRegistration
            )
          ),
          true
        ).map(_.result[Adverts])
      )

    def delete(id: Int): Future[Option[Adverts]] =
      collection.flatMap(
        _.findAndRemove(BSONDocument("id" -> id)).map(_.result
        [Adverts])
      )
}
