package models
import play.api.libs.json.{Json, OFormat}
//Case class - A data structure
case class DataModel(
               _id:String,
               name:String,
               description:String,
               numSales:Int
               )

//Format data model to Json
object DataModel{
  implicit val formats:OFormat[DataModel] = Json.format[DataModel]
}