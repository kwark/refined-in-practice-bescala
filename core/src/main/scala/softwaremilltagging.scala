import improved_refinements.{Name, TwitterHandle}
import eu.timepit.refined.types.numeric.PosLong
import play.api.libs.json.{Format, JsResult, JsValue}
import com.softwaremill.tagging._
import softwaremilltagging.{Developer, DeveloperId, DeveloperIdTag, FirstName, FirstNameTag, LastName, LastNameTag}
import be.venneborg.refined.play.RefinedJsonFormats._

object softwaremilltagging {

  trait DeveloperIdTag
  type DeveloperId = PosLong @@ DeveloperIdTag
  implicit val developerIdFormat: Format[DeveloperId] = taggedFormat[PosLong, DeveloperIdTag]

  trait FirstNameTag
  type FirstName = Name @@ FirstNameTag
  implicit val firstNameFormat: Format[FirstName] = taggedFormat[Name, FirstNameTag]


  trait LastNameTag
  type LastName = Name @@ LastNameTag
  implicit val lastNameFormat: Format[LastName] = taggedFormat[Name, LastNameTag]

  case class Developer(id: Option[DeveloperId],
                       twitterHandle: TwitterHandle,
                       firstName: FirstName,
                       lastName: LastName
                      )

  def taggedFormat[T, U](implicit format: Format[T]): Format[T @@ U] = new Format[@@[T, U]] {

    override def writes(o: @@[T, U]): JsValue = format.writes(o)

    override def reads(json: JsValue): JsResult[@@[T, U]] = format.reads(json).map(_.taggedWith[U])
  }

}

object SoftwareMillTaggedSchema {

  import MyRefinedSlickProfile.api._
  import MyRefinedSlickProfile.mapping._

  // some necessary boilerplate. I haven't found a way yet to get rid of this
  implicit val developerIdIso = new Isomorphism[DeveloperId, PosLong](identity, _.taggedWith[DeveloperIdTag])
  implicit val firstNameIso = new Isomorphism[FirstName, Name](identity, _.taggedWith[FirstNameTag])
  implicit val lastNameIso = new Isomorphism[LastName, Name](identity, _.taggedWith[LastNameTag])

  class Developers(tag: Tag) extends Table[Developer](tag, "DEVELOPERS") {
    def id = column[DeveloperId]("ID", O.PrimaryKey, O.AutoInc)
    def twitter = column[TwitterHandle]("TWITTER")
    def firstName    = column[FirstName]("FIRSTNAME")
    def lastName    = column[LastName]("LASTNAME")

    def * = (id.?, twitter, firstName, lastName).mapTo[Developer]
  }

  val developers = TableQuery[Developers]

}


