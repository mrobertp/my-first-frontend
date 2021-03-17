package controllers

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import models.DataModel
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.play.test.UnitSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import play.api.libs.json.{JsObject, Json}
import reactivemongo.api.commands.{LastError, WriteResult}
import repositories.DataRepository
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.{ExecutionContext, Future}


class ApplicationControllerSpec extends UnitSpec with MockitoSugar with GuiceOneAppPerSuite{

  implicit val system: ActorSystem = ActorSystem("Sys")
  implicit val materializer: ActorMaterializer = ActorMaterializer()


  val controllerComponents: ControllerComponents = app.injector.instanceOf[ControllerComponents]
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val mockDataRepository: DataRepository = mock[DataRepository]

  object TestApplicationController extends ApplicationController(
    controllerComponents,
    mockDataRepository,
    executionContext
  )
  val dataModel:DataModel = DataModel ("abcd",
    "test name",
    "test description",
    100)
  val jsonBody: JsObject = Json.obj(
    "_id" -> "abcd",
    "name" -> "test name",
    "description" -> "test description",
    "numSales" -> 100
  )

  val jsonBody2: JsObject = Json.obj(
    "_id" -> "abcd",
    "name" -> "test name",
    "description" -> "test description",
    "numSales" -> 200
  )
  "ApplicationController .index()" should {

    when(mockDataRepository.find(any())(any()))
      .thenReturn(Future(List(dataModel)))
    val result = TestApplicationController.index()(FakeRequest())

    "return OK-200" in {
      status(result) shouldBe Status.OK
    }

//    "return jsonBody" in {
//      await(jsonBodyOf(result)) shouldBe dataModel
//    }

  }

  "ApplicationController .create" when {

    "the json body is valid" should {


      val writeResult: WriteResult = LastError(ok = true, None, None, None, 0, None, updatedExisting = false, None, None, wtimeout = false, None, None)

      when(mockDataRepository.create(any()))
        .thenReturn(Future(writeResult))

      val result = TestApplicationController.create()(FakeRequest().withBody(jsonBody))

      "return Created" in {
        status(result) shouldBe Status.CREATED    }
    }


    "the json bosy is not valid" should {
      val jsonBody: JsObject = Json.obj(
        "_id" -> "dfklk",
      "rubbish" -> "rubbish")

      val result = TestApplicationController.create()(FakeRequest().withBody(jsonBody))

      "return BAD_REQUEST" in {
        status(result)  shouldBe Status.BAD_REQUEST
      }
    }
  }




  "ApplicationController .read()" should {
    when(mockDataRepository.read(any()))
      .thenReturn(Future(dataModel))
    val resultValid = TestApplicationController.read("abcd")(FakeRequest())
    "return the correct json" in {
      await(jsonBodyOf(resultValid)) shouldBe (jsonBody)
    }
    "return the OK status" in{
      status(resultValid) shouldBe Status.OK
    }
  }

  "ApplicationController .update()" should {
    when(mockDataRepository.update(any()))
      .thenReturn(Future(dataModel))

    val resultValid = TestApplicationController.update("abcd")(FakeRequest().withBody(jsonBody2))

    "return the correct json" in {
      await(jsonBodyOf(resultValid)) shouldBe (jsonBody2)
    }
  }

  "ApplicationController .delete()" should {

  }

}
