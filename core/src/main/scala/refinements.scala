import eu.timepit.refined.W
import eu.timepit.refined.api.{Refined, RefinedTypeOps}
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.string.StartsWith


object refinements {
  type Name = String Refined NonEmpty
  object Name extends RefinedTypeOps[Name, String]

  type TwitterHandle = String Refined StartsWith[W.`"@"`.T]
  object TwitterHandle extends RefinedTypeOps[TwitterHandle, String]

  final case class Developer(name: Name, twitterHandle: TwitterHandle)
}

