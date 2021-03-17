package controllers

import uk.gov.hmrc.play.test.UnitSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest

import play.api.mvc.Result
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.UnitSpec


class ApplicationControllerSpec extends UnitSpec with GuiceOneAppPerSuite{
  val controllerComponents: ControllerComponents = app.injector.instanceOf[ControllerComponents]

  object TestApplicationController extends ApplicationController(
    controllerComponents
  )

  "ApplicationController .index()" should {
    val result = TestApplicationController.index()(FakeRequest())

    "return OK-200" in {
      status(result) shouldBe Status.OK
    }
  }

  "ApplicationController .create()" should {

  }

  "ApplicationController .read()" should {

  }

  "ApplicationController .update()" should {

  }
  "ApplicationController .delete()" should {

  }

}
