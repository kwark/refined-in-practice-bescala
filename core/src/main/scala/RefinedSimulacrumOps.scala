import eu.timepit.refined.types.numeric._
import eu.timepit.refined.types.string._
import simulacrum._

object RefinedSimulacrumOps {

  @typeclass trait RefinedStringOps[S] {
    @op("|+|") def concat(s1: S, s2: S): S
  }

  implicit val refinedNonEmptyStringOps: RefinedStringOps[NonEmptyString] =
    (t1: NonEmptyString, t2: NonEmptyString) =>
      NonEmptyString.unsafeFrom(t1.value + t2.value)

  @typeclass trait RefinedIntOps[T] {
    @op("!+!") def unsafeAdd(t1: T, t2: T): T
    @op("|+|") def add(t1: T, t2: T): Option[T]
  }

  implicit val refinedPosIntOps: RefinedIntOps[PosInt] = new RefinedIntOps[PosInt] {

    override def unsafeAdd(x: PosInt, y: PosInt): PosInt =
      PosInt.unsafeFrom(x.value + y.value)


    override def add(x: PosInt, y: PosInt): Option[PosInt] =
      PosInt.unapply(x.value + y.value)

  }


}
