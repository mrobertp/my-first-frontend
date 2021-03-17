package controllers

import javax.inject.Inject
import play.api.mvc._

class ApplicationController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok
  }
  def create() = TODO //HTTP POST - 201
  def read() = TODO //HTTP GET - 200
  def update() = TODO //HTTP PUT  -201?
  def delete() = TODO //HTTP DELETE -200?

}
