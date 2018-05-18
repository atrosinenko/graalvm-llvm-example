package io.github.atrosinenko

import java.io.File

import org.graalvm.polyglot.proxy.ProxyExecutable
import org.graalvm.polyglot.{Context, Source, Value}

object SQLiteTest {
  val polyglot: Context = Context.newBuilder().allowAllAccess(true).build()

  def loadBcFile(file: File): Value = {
    val source = Source.newBuilder("llvm", file).build()
    polyglot.eval(source)
  }
  val cpart: Value = loadBcFile(new File("./sqlite3.bc"))
  val lib:   Value = loadBcFile(new File("./lib.bc"))


  val sqliteOpen:   Value = cpart.getMember("sqlite3_open")
  val sqliteExec:   Value = cpart.getMember("sqlite3_exec")
  val sqliteErrmsg: Value = cpart.getMember("sqlite3_errmsg")
  val sqliteClose:  Value = cpart.getMember("sqlite3_close")
  val sqliteFree:   Value = cpart.getMember("sqlite3_free")

  val bytesToNative: Value = polyglot.getBindings("llvm").getMember("__sulong_byte_array_to_native")
  def toCString(str: String): Value = {
    bytesToNative.execute(str.getBytes())
  }

  val lib_fromCString: Value = lib.getMember("fromCString")
  def fromCString(ptr: Value): String = {
    if (ptr.isNull)
      "<null>"
    else
      lib_fromCString.execute(ptr).asString()
  }

  val lib_copyToArray: Value = lib.getMember("copy_to_array_from_pointers")
  val callback: ProxyExecutable = new ProxyExecutable {
    override def execute(arguments: Value*): AnyRef = {
      val argc = arguments(1).asInt()
      val xargv = new Array[Long](argc)
      val xazColName = new Array[Long](argc)
      lib_copyToArray.execute(xargv, arguments(2))
      lib_copyToArray.execute(xazColName, arguments(3))

      (0 until argc) foreach { i =>
        val name  = fromCString(polyglot.asValue(xazColName(i) ^ 1))
        val value = fromCString(polyglot.asValue(xargv(i) ^ 1))
        println(s"$name = $value")
      }
      println("========================")
      Int.box(0)
    }
  }

  def query(dbFile: String, queryString: String): Unit = {
    val filenameStr = toCString(dbFile)
    val ptrToDb = new Array[Object](1)
    val rc = sqliteOpen.execute(filenameStr, ptrToDb)
    val db = ptrToDb.head
    if (rc.asInt() != 0) {
      println(s"Cannot open $dbFile: ${fromCString(sqliteErrmsg.execute(db))}!")
      sqliteClose.execute(db)
    } else {
      val zErrMsg = new Array[Object](1)
      val execRc = sqliteExec.execute(db, toCString(queryString), callback, Int.box(0), zErrMsg)
      if (execRc.asInt != 0) {
        val errorMessage = zErrMsg.head.asInstanceOf[Value]
        println(s"Cannot execute query: ${fromCString(errorMessage)}")
        sqliteFree.execute(errorMessage)
      }
      sqliteClose.execute(db)
    }
  }

  def main(args: Array[String]): Unit = {
    query(args(0), args(1))
    polyglot.close()
  }
}
