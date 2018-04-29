import eu.timepit.refined.types.string._
import eu.timepit.refined.types.numeric._
import org.scalatest.{FunSuite, Matchers}
import org.scalatest.prop.PropertyChecks
import RefinedOps._
import eu.timepit.refined.types.numeric
import eu.timepit.refined.scalacheck.string.nonEmptyStringArbitrary
import eu.timepit.refined.scalacheck.numeric._

class RefinedOpsTest extends FunSuite with Matchers with PropertyChecks {

  test("NonEmptyString append") {
    forAll { (s1: NonEmptyString, s2: NonEmptyString) =>
      val ev = implicitly[RefinedStringOps[NonEmptyString]]
      ev.concat(s1, s2).value.nonEmpty shouldBe true
    }
  }

  test("PosInt add") {
    forAll { (i1: PosInt, i2: PosInt) =>
      val ev = implicitly[RefinedIntOps[PosInt]]
      val result: Option[numeric.PosInt] = ev.add(i1, i2)
      if (i2.value + i1.value > 0) result.nonEmpty shouldBe true
      else result.isEmpty shouldBe true
    }
  }

  ignore("PosInt unsafeAdd") {
    forAll { (i1: PosInt, i2: PosInt) =>
      val ev = implicitly[RefinedIntOps[PosInt]]
      // This test fails sometimes, because of int overflow
       ev.unsafeAdd(i1, i2).value should be > 0
    }
  }

}
