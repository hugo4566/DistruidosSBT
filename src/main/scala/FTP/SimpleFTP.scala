package FTP

import java.io.{BufferedInputStream, BufferedOutputStream, BufferedReader, BufferedWriter, File, FileInputStream, IOException, InputStream, InputStreamReader, OutputStreamWriter}
import java.net.Socket
import java.util.StringTokenizer

/**
 * Created by Hugo on 13/11/2014.
 */

class SimpleFTP {


  /**
   * Connects to the default port of an FTP server and logs in as
   * anonymous/anonymous.
   */
  def connect(host: String) {
    connect(host, 21)
  }

  /**
   * Connects to an FTP server and logs in as anonymous/anonymous.
   */
  def connect(host: String, port: Int) {
    connect(host, port, "hugo4566", "teste123")
  }

  /**
   * Connects to an FTP server and logs in with the supplied username and
   * password.
   */
  def connect(host: String, port: Int, usuario: String, password: String) {
    if (socket != null) {
      throw new IOException("Is already connected. Disconnect first.")
    }
    socket = new Socket(host, port)
    reader = new BufferedReader(new InputStreamReader(socket.getInputStream))
    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream))
    response
    user(usuario)
    pass(password)
    System.out.println("Now logged in")
  }

  /*************************
   **** Common commands ****
   *************************/

  /**
   * Send this command to begin the login process. username should be a valid username on the system, or "anonymous" to initiate an anonymous login.
   * @param user
   * @return response
   */
  def user(user : String) : String = {
    sendLine("USER " + user)
    response
  }

  /**
   * After sending the USER command, send this command to complete the login process. (Note, however, that an ACCT command may have to be used on some systems.)
   * @param pass
   * @return response
   */
  def pass(pass : String) = {
    sendLine("PASS " + pass)
    response
  }

  def abor() = {}

  /**
   * Makes the given directory be the current directory on the remote host.
   * @param dir
   * @return response
   */
  def cwd(dir: String): String = {
    sendLine("CWD " + dir)
    response
  }

  def dele() = {}

  def list() = {}

  def mdtm() = {}

  def mkd() = {}

  def nlst() = {}


  /**
   * Tells the server to enter "passive mode". In passive mode, the server will wait for the client to establish a connection with it rather than attempting to connect
   * to a client-specified port. The server will respond with the address of the port it is listening on, with a message like:
   * 227 Entering Passive Mode (a1,a2,a3,a4,p1,p2)
   * where a1.a2.a3.a4 is the IP address and p1*256+p2 is the port number.
   * @return response
   */
  private def pasv() : String = {
    sendLine("PASV")
    response
  }

  def port() = {}

  /**
   * Returns the name of the current directory on the remote host.
   * @return response
   */
  def pwd(): String = {
    sendLine("PWD")
    response
  }


  /**
   * Terminates the command connection.
   */
  def quit() = {
    try {
      sendLine("QUIT")
    }
    finally {
      socket = null
    }
  }

  def retr() = {}
  def rmd() = {}
  def rnfr() = {}
  def rnto() = {}
  def site() = {}
  def size() = {}

  /**
   * Begins transmission of a file to the remote site. Must be preceded by either a PORT command or a PASV command so the server knows where to accept data from.
   * @param filename
   * @return response
   */
  private def stor(filename: String): String = {
    sendLine("STOR " + filename)
    response
  }

  /**
   * Sends a file to be stored on the FTP server. Returns true if the file
   * transfer was successful. The file is sent in passive mode to avoid NAT or
   * firewall problems at the client end.
   */
  def stor(file: File): String = {
    if (file.isDirectory) {
      throw new IOException("FTP.SimpleFTP cannot upload a directory.")
    }
    val filename: String = file.getName

    stor(new FileInputStream(file), filename)
  }

  /**
   * Sends a file to be stored on the FTP server. Returns true if the file
   * transfer was successful. The file is sent in passive mode to avoid NAT or
   * firewall problems at the client end.
   */
  def stor(inputStream: InputStream, filename: String): String = {

    val input: BufferedInputStream = new BufferedInputStream(inputStream)

    val (ip,port) = getIpPortFromPASV(pasv)
    val dataSocket: Socket = new Socket(ip, port)

    stor(filename)

    val output: BufferedOutputStream = new BufferedOutputStream(dataSocket.getOutputStream)
    val buffer: Array[Byte] = new Array[Byte](4096)
    var bytesRead: Int = 0

    while ({bytesRead = input.read(buffer); bytesRead} != -1) {
      output.write(buffer, 0, bytesRead)
    }

    output.flush
    output.close
    input.close

    response
  }

  /**
   * Enter binary mode for sending binary files.
   */
  def bin: String = {
    sendLine("TYPE I")
    return response
  }

  /**
   * Enter ASCII mode for sending text files. This is usually the default mode.
   * Make sure you use binary mode if you are sending images or other binary
   * data, as ASCII mode is likely to corrupt them.
   */
  def ascii: String = {
    sendLine("TYPE A")
    return response
  }

  /**
   * Sends a raw command to the FTP server.
   */
  private def sendLine(line: String) {
    if (socket == null) {
      throw new IOException("SimpleFTP is not connected.")
    }
    try {
      writer.write(line + "\r\n")
      writer.flush
      if (DEBUG) {
        System.out.println("> " + line)
      }
    }
    catch {
      case e: IOException => {
        socket = null
        throw e
      }
    }
  }

  private def response: String = {
    val line: String = reader.readLine
    //if (DEBUG) {
      System.out.println("< " + line)
    //}
    return line
  }

  def getIpPortFromPASV(response : String) : (String,Int) ={
    var ip: String = null
    var port: Int = -1
    val opening: Int = response.indexOf('(')
    val closing: Int = response.indexOf(')', opening + 1)
    if (closing > 0) {
      val dataLink: String = response.substring(opening + 1, closing)
      val tokenizer: StringTokenizer = new StringTokenizer(dataLink, ",")
      try {
        ip = tokenizer.nextToken + "." + tokenizer.nextToken + "." + tokenizer.nextToken + "." + tokenizer.nextToken
        port = Integer.parseInt(tokenizer.nextToken) * 256 + Integer.parseInt(tokenizer.nextToken)
      }
      catch {
        case e: Exception => {
          throw new IOException("FTP.SimpleFTP received bad data link information: " + response)
        }
      }
    }
    (ip,port)
  }

  private var socket: Socket = null
  private var reader: BufferedReader = null
  private var writer: BufferedWriter = null
  private val DEBUG: Boolean = true
}