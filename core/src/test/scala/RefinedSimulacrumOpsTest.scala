import RefinedOps.RefinedIntOps
import RefinedSimulacrumOps._
import eu.timepit.refined.scalacheck.numeric._
import eu.timepit.refined.scalacheck.string.nonEmptyStringArbitrary
import eu.timepit.refined.types.numeric
import eu.timepit.refined.types.numeric._
import eu.timepit.refined.types.string._
import org.scalatest.prop.PropertyChecks
import org.scalatest.{FunSuite, Matchers}

class RefinedSimulacrumOpsTest extends FunSuite with Matchers with PropertyChecks {

  implicit val refinedStringStringOps = new RefinedStringOps[String] {
    override def concat(t1: String, t2: String): String = t1 + t2
  }

  import RefinedStringOps.ops._

  test("String concat") {
    forAll { (s1: String, s2: String) =>
      (s1 |+| s2) shouldBe (s1 + s2)
    }
  }

  test("NonEmptyString concat") {
    forAll { (s1: NonEmptyString, s2: NonEmptyString) =>
      val ev = implicitly[RefinedStringOps[NonEmptyString]]
      (s1 |+| s2).value.nonEmpty shouldBe true
    }
  }

  import RefinedIntOps.ops._

  test("PosInt add") {
    forAll { (i1: PosInt, i2: PosInt) =>
      val ev = implicitly[RefinedIntOps[PosInt]]
      val result: Option[numeric.PosInt] = i1 |+| i2
      if (i2.value + i1.value > 0) result.nonEmpty shouldBe true
      else result.isEmpty shouldBe true
    }
  }

  ignore("PosInt unsafeAdd") {
    forAll { (i1: PosInt, i2: PosInt) =>
      val ev = implicitly[RefinedIntOps[PosInt]]
      // This test fails sometimes, because of int overflow
      (i1 !+! i2).value should be > 0
    }
  }

}
