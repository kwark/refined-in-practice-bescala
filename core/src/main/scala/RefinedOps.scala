import eu.timepit.refined.api.{RefType, Refined}
import eu.timepit.refined.types.numeric._
import eu.timepit.refined.types.string._

object RefinedOps {

  trait RefinedStringOps[T] {
    def concat(t1: T, t2: T): T
  }

  implicit val refinedNonEmptyStringOps: RefinedStringOps[NonEmptyString] =
    (t1: NonEmptyString, t2: NonEmptyString) =>
      NonEmptyString.unsafeFrom(t1.value + t2.value)

  trait RefinedIntOps[T] {
    def unsafeAdd(t1: T, t2: T): T
    def add(t1: T, t2: T): Option[T]
  }

  implicit val refinedPosIntOps: RefinedIntOps[PosInt] = new RefinedIntOps[PosInt] {

    override def unsafeAdd(x: PosInt, y: PosInt): PosInt =
      PosInt.unsafeFrom(x.value + y.value)


    override def add(x: PosInt, y: PosInt): Option[PosInt] =
      PosInt.unapply(x.value + y.value)

  }


}
