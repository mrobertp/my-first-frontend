package controllers

import javax.inject.Inject
import models.DataModel
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import reactivemongo.core.errors.DatabaseException
import repositories.DataRepository
import scala.concurrent.{ExecutionContext, Future}

class ApplicationController @Inject()(val controllerComponents: ControllerComponents,
                                      dataRepository: DataRepository,
                                      implicit val ex:ExecutionContext) extends BaseController {



  def index() = Action.async { implicit request =>
    dataRepository.find().map(items => Ok(Json.toJson(items)))
  }


  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        dataRepository.create(dataModel).map(_ => Created) recover {
          case _: DatabaseException => InternalServerError(Json.obj(
            "message" -> "Error adding item to Mongo"
          ))
        }
      case JsError(_) => Future(BadRequest)
    }
  }

  def read(id:String):Action[AnyContent] = Action.async  {implicit request =>
    dataRepository.read(id).map(items => Ok(Json.toJson(items)))
  }

  def update(id:String) = Action.async(parse.json){ implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel: DataModel, _) =>
        dataRepository.update(dataModel).map(items => Accepted(Json.toJson(items))) recover {
          case _: DatabaseException => InternalServerError(Json.obj(
            "message" -> "Error updating item to Mongo"
          ))
        }
      case JsError(_) => Future(BadRequest)
    }
  }


  def delete(id:String) = Action.async{implicit request =>
    dataRepository.delete(id).map(_ => Ok)
  }

}
