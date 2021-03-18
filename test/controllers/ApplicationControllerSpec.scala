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
import reactivemongo.core.errors.GenericDriverException
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
        status(result) shouldBe Status.CREATED
      }
    }
      "the mongo data creation failed" should {

        when(mockDataRepository.create(any()))
          .thenReturn(Future.failed(GenericDriverException("Error")))

        "return an error" in {

          val result = TestApplicationController.create()(FakeRequest().withBody(jsonBody))

          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          await(bodyOf(result)) shouldBe Json.obj("message" -> "Error adding item to Mongo").toString()
        }
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
    //TestApplicationController.create()(FakeRequest().withBody(jsonBody))
    val resultValid = TestApplicationController.update("abcd")(FakeRequest().withBody(jsonBody2))

    "return the correct json" in {
      await(jsonBodyOf(resultValid)) shouldBe (jsonBody)
    }
  }

  "ApplicationController .delete()" should {

    //Not really sure what this is doing
    //add dataModel to MongoDB using create
    //Delete from mongoDb
    //check deletion is successful
    when(mockDataRepository.delete(any()))
      .thenReturn(Future(null))
    val testValid  = TestApplicationController.delete("abcd")(FakeRequest())

    "return id and find" in {
      status(testValid) shouldBe 202
    }


  }

}
