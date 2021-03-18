package models
import play.api.libs.json.{Json, OFormat}
//Case class - A data structure
case class DataModel(
               _id:String,
               name:String,
               description:String,
               numSales:Int
               )

//Format data model to Json - Object class similar to java statics
object DataModel{
  implicit val formats:OFormat[DataModel] = Json.format[DataModel]
}