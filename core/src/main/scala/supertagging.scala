import improved_refinements.{Name, TwitterHandle}
import eu.timepit.refined.types.numeric.PosLong
import play.api.libs.json.{JsValue, Reads}
import supertagged._
import supertagging.{Developer, DeveloperId, FirstName, LastName}

object supertagging {

  trait DeveloperIdTag
  type DeveloperId = PosLong @@ DeveloperIdTag
  object DeveloperId extends TaggedTypeOps[DeveloperId, PosLong, DeveloperIdTag]

  trait FirstNameTag
  type FirstName = Name @@ FirstNameTag
  object FirstName extends TaggedTypeOps[FirstName, Name, FirstNameTag]

  trait LastNameTag
  type LastName = Name @@ LastNameTag
  object LastName extends TaggedTypeOps[LastName, Name, LastNameTag]

  case class Developer(id: Option[DeveloperId],
                       twitterHandle: TwitterHandle,
                       firstName: FirstName,
                       lastName: LastName
                      )

  implicit def taggedReads[T, U](implicit reads: Reads[T]): Reads[T @@ U] =
    reads.map(tag[U].apply(_))

  // supertagged.tag needs an asInstanceOf to be able to assign a tagged instance to the type alias
  class TaggedTypeOps[TT, T, U](implicit ev: @@[T, U] =:= TT) {
    def apply(t: T): TT = tag[U](t).asInstanceOf[TT]
  }

}

object SupertaggedSchema {

  import MyRefinedSlickProfile.api._
  import MyRefinedSlickProfile.mapping._

  // some necessary boilerplate. I haven't found a way yet to get rid of this
  implicit val developerIdIso = new Isomorphism[DeveloperId, PosLong](identity, DeveloperId.apply(_))
  implicit val firstNameIso = new Isomorphism[FirstName, Name](identity, FirstName.apply(_))
  implicit val lastNameIso = new Isomorphism[LastName, Name](identity, LastName.apply(_))

  class Developers(tag: Tag) extends Table[Developer](tag, "DEVELOPERS") {
    def id = column[DeveloperId]("ID", O.PrimaryKey, O.AutoInc)
    def twitter = column[TwitterHandle]("TWITTER")
    def firstName    = column[FirstName]("FIRSTNAME")
    def lastName    = column[LastName]("LASTNAME")

    def * = (id.?, twitter, firstName, lastName).mapTo[Developer]
  }

  val developers = TableQuery[Developers]

}


