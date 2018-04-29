import MyRefinedSlickProfile.api._
import SoftwareMillTaggedSchema._
import eu.timepit.refined.auto._
import improved_refinements.Name
import com.softwaremill.tagging._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, FunSuite, Matchers}
import play.api.libs.json.{JsValue, Json}
import softwaremilltagging._

class SoftwaremillTaggingTest extends FunSuite with BeforeAndAfterAll with ScalaFutures with Matchers {

  var db: Database = _

  val developer = Developer(None, "@kwarkk", ("Peter": Name).taggedWith[FirstNameTag], ("Mortier": Name).taggedWith[LastNameTag])

  test("tagged type is instanceOf untagged type") {
    val firstName = ("Peter": Name).taggedWith[FirstNameTag]
    firstName.isInstanceOf[Name] shouldBe true
  }

  test("store and retrieve") {
    db.run(developers += developer).futureValue
    db.run(developers.length.result).futureValue shouldBe 1
    val result: Seq[Developer] = db.run(developers.result).futureValue
    result.size shouldBe 1
    val retrieved = result.head
    retrieved.id.map(_.value) shouldBe Some(1L)

    // slick queries
    db.run(developers.filter(_.firstName.asInstanceOf[Rep[Name]].ilike("%ete%")).result).futureValue should contain theSameElementsAs Seq(retrieved)

    // plain sql query
    import _root_.be.venneborg.refined.RefinedPlainSql._
    db.run(sql"""SELECT FIRSTNAME FROM DEVELOPERS WHERE TWITTER LIKE '%rkk'""".as[Name]).futureValue should contain theSameElementsAs Seq(developer.firstName.asInstanceOf[Name])

    // plain sql update
    db.run(sqlu"""UPDATE DEVELOPERS SET FIRSTNAME = 'Pieter' WHERE ID = 1""").futureValue shouldBe 1

  }

  test("automatic json format") {

    import _root_.be.venneborg.refined.play.RefinedJsonFormats._

    implicit val developerFormat = Json.format[Developer]

    val result: JsValue = Json.toJson(developer)
    Json.fromJson[Developer](result).get shouldBe developer
  }

  override def beforeAll(): Unit = {
    db = Database.forURL("jdbc:h2:mem:test-refined-softwaremill;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
    db.run(developers.schema.create).futureValue
  }

  override def afterAll(): Unit = {
    db.run(developers.schema.drop).futureValue
    db.close()
  }

  override implicit val patienceConfig: PatienceConfig = PatienceConfig(Span(2, Seconds))


}
