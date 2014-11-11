import java.io.{BufferedInputStream, BufferedOutputStream, PrintStream}
import java.net.Socket

/**
 * Created by Hugo on 10/11/2014.
 */
object Client {
  def main(args: Array[String]) {
    val socket = new Socket("localhost",2020)
    val envia =  new PrintStream(new BufferedOutputStream(socket.getOutputStream))
    val recebe = new BufferedInputStream(socket.getInputStream)
    while(true){
      envia.println("teste")
      envia.flush()
      Thread.sleep(30000)
    }
  }
}
