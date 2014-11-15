package FTP

/**
 * Created by Hugo on 14/11/2014.
 */
object testFTP {
  def main(args: Array[String]) {
    val simpleFTP = new SimpleFTP()
    simpleFTP.connect("ftp.xpg.com.br")
    println(simpleFTP.pwd)
  }
}
