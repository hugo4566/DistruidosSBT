package TCP

import java.io.{BufferedInputStream, BufferedOutputStream, PrintStream}
import java.net.{ServerSocket, Socket}
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.{ExecutorService, Executors}

import scala.collection.mutable.ListBuffer

class NetworkService(port: Int, poolSize: Int) extends Runnable {
  val serverSocket = new ServerSocket(port)
  val pool: ExecutorService = Executors.newFixedThreadPool(poolSize)
  var users: scala.collection.mutable.ListBuffer[String] = ListBuffer()

  def run() {
    try {
      while (true) {
        // This will block until a connection comes in.
        val socket = serverSocket.accept()
        val ip = socket.getInetAddress.getHostAddress
        users.+=(ip)
        pool.execute(new Handler(socket))
      }
    } finally {
      pool.shutdown()
    }
  }
}

class Handler(socket: Socket) extends Runnable {
  //def message = (Thread.currentThread.getName() + "\n")
  val envia =  new PrintStream(new BufferedOutputStream(socket.getOutputStream))
  val recebe = new BufferedInputStream(socket.getInputStream)
  val formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss")

  def run() {
    while(true){
      while(recebe.available() < 1){ Thread.sleep(100)}
      val buf = new Array[Byte](recebe.available())
      recebe.read(buf)
      val recebido = new String(buf)
      val message = "["+formater.format(Calendar.getInstance().getTime)+"] "+socket.getInetAddress.getCanonicalHostName+": "+recebido
      println(message)
    }
  }
}