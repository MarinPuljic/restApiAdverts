package models

import play.api.libs.json.{Json, OFormat}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class Adverts
      (
        id: Int,
        title: String,
        fuelType: String,
        price: Int,
        isNew: Boolean,
        mileage: Int,
        firstRegistration: Option[String]
      )
object Adverts{
  implicit val advertsDecoder: Decoder[Adverts] = deriveDecoder[Adverts]
  implicit val advertsEncoder: Encoder[Adverts] = deriveEncoder[Adverts]
  implicit val format: OFormat[Adverts] = Json.format[Adverts]
}