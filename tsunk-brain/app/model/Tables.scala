package model
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.driver.MySQLDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.driver.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Kashi.schema ++ Morpheme.schema ++ Word.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Kashi
   *  @param id Database column id SqlType(INT UNSIGNED), AutoInc, PrimaryKey
   *  @param kashi Database column kashi SqlType(TEXT)
   *  @param title Database column title SqlType(CHAR), Length(30,false), Default()
   *  @param artist Database column artist SqlType(CHAR), Length(30,false), Default() */
  case class KashiRow(id: Int, kashi: String, title: String = "", artist: String = "")
  /** GetResult implicit for fetching KashiRow objects using plain SQL queries */
  implicit def GetResultKashiRow(implicit e0: GR[Int], e1: GR[String]): GR[KashiRow] = GR{
    prs => import prs._
    KashiRow.tupled((<<[Int], <<[String], <<[String], <<[String]))
  }
  /** Table description of table kashi. Objects of this class serve as prototypes for rows in queries. */
  class Kashi(_tableTag: Tag) extends Table[KashiRow](_tableTag, "kashi") {
    def * = (id, kashi, title, artist) <> (KashiRow.tupled, KashiRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(kashi), Rep.Some(title), Rep.Some(artist)).shaped.<>({r=>import r._; _1.map(_=> KashiRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT UNSIGNED), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column kashi SqlType(TEXT) */
    val kashi: Rep[String] = column[String]("kashi")
    /** Database column title SqlType(CHAR), Length(30,false), Default() */
    val title: Rep[String] = column[String]("title", O.Length(30,varying=false), O.Default(""))
    /** Database column artist SqlType(CHAR), Length(30,false), Default() */
    val artist: Rep[String] = column[String]("artist", O.Length(30,varying=false), O.Default(""))
  }
  /** Collection-like TableQuery object for table Kashi */
  lazy val Kashi = new TableQuery(tag => new Kashi(tag))

  /** Entity class storing rows of table Morpheme
   *  @param id Database column id SqlType(INT UNSIGNED), AutoInc, PrimaryKey
   *  @param surface Database column surface SqlType(CHAR), Length(11,false), Default()
   *  @param pos Database column pos SqlType(CHAR), Length(11,false), Default()
   *  @param kashiId Database column kashi_id SqlType(INT) */
  case class MorphemeRow(id: Int, surface: String = "", pos: String = "", kashiId: Int)
  /** GetResult implicit for fetching MorphemeRow objects using plain SQL queries */
  implicit def GetResultMorphemeRow(implicit e0: GR[Int], e1: GR[String]): GR[MorphemeRow] = GR{
    prs => import prs._
    MorphemeRow.tupled((<<[Int], <<[String], <<[String], <<[Int]))
  }
  /** Table description of table morpheme. Objects of this class serve as prototypes for rows in queries. */
  class Morpheme(_tableTag: Tag) extends Table[MorphemeRow](_tableTag, "morpheme") {
    def * = (id, surface, pos, kashiId) <> (MorphemeRow.tupled, MorphemeRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(surface), Rep.Some(pos), Rep.Some(kashiId)).shaped.<>({r=>import r._; _1.map(_=> MorphemeRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT UNSIGNED), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column surface SqlType(CHAR), Length(11,false), Default() */
    val surface: Rep[String] = column[String]("surface", O.Length(11,varying=false), O.Default(""))
    /** Database column pos SqlType(CHAR), Length(11,false), Default() */
    val pos: Rep[String] = column[String]("pos", O.Length(11,varying=false), O.Default(""))
    /** Database column kashi_id SqlType(INT) */
    val kashiId: Rep[Int] = column[Int]("kashi_id")
  }
  /** Collection-like TableQuery object for table Morpheme */
  lazy val Morpheme = new TableQuery(tag => new Morpheme(tag))

  /** Entity class storing rows of table Word
   *  @param id Database column id SqlType(INT UNSIGNED), AutoInc, PrimaryKey
   *  @param word Database column word SqlType(CHAR), Length(255,false), Default()
   *  @param kashiId Database column kashi_id SqlType(INT UNSIGNED) */
  case class WordRow(id: Int, word: String = "", kashiId: Int)
  /** GetResult implicit for fetching WordRow objects using plain SQL queries */
  implicit def GetResultWordRow(implicit e0: GR[Int], e1: GR[String]): GR[WordRow] = GR{
    prs => import prs._
    WordRow.tupled((<<[Int], <<[String], <<[Int]))
  }
  /** Table description of table word. Objects of this class serve as prototypes for rows in queries. */
  class Word(_tableTag: Tag) extends Table[WordRow](_tableTag, "word") {
    def * = (id, word, kashiId) <> (WordRow.tupled, WordRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(word), Rep.Some(kashiId)).shaped.<>({r=>import r._; _1.map(_=> WordRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT UNSIGNED), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column word SqlType(CHAR), Length(255,false), Default() */
    val word: Rep[String] = column[String]("word", O.Length(255,varying=false), O.Default(""))
    /** Database column kashi_id SqlType(INT UNSIGNED) */
    val kashiId: Rep[Int] = column[Int]("kashi_id")

    /** Foreign key referencing Kashi (database name kashi_word_fk) */
    lazy val kashiFk = foreignKey("kashi_word_fk", kashiId, Kashi)(r => r.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table Word */
  lazy val Word = new TableQuery(tag => new Word(tag))
}
