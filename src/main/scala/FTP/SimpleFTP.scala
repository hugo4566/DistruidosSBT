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
  def connect(host: String, port: Int, user: String, pass: String) {
    if (socket != null) {
      throw new IOException("FTP.SimpleFTP is already connected. Disconnect first.")
    }
    socket = new Socket(host, port)
    reader = new BufferedReader(new InputStreamReader(socket.getInputStream))
    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream))
    var response: String = readLine
    validateResponse(response,"220 ")
    sendLine("USER " + user)
    response = readLine
    validateResponse(response,"331 ")
    sendLine("PASS " + pass)
    response = readLine
    validateResponse(response,"230 ")
    System.out.println("Now logged in")
  }

  /**
   * Disconnects from the FTP server.
   */
  def disconnect {
    try {
      sendLine("QUIT")
    }
    finally {
      socket = null
    }
  }

  /**
   * Returns the working directory of the FTP server it is connected to.
   */
  def pwd: String = {
    sendLine("PWD")
    val response: String = readLine
    return validateResponse(response,"257 ").toString
  }

  /**
   * Changes the working directory (like cd). Returns true if successful.
   */
  def cwd(dir: String): Boolean = {
    sendLine("CWD " + dir)
    val response: String = readLine
    return (response.startsWith("250 "))
  }

  /**
   * Sends a file to be stored on the FTP server. Returns true if the file
   * transfer was successful. The file is sent in passive mode to avoid NAT or
   * firewall problems at the client end.
   */
  def stor(file: File): Boolean = {
    if (file.isDirectory) {
      throw new IOException("FTP.SimpleFTP cannot upload a directory.")
    }
    val filename: String = file.getName
    return stor(new FileInputStream(file), filename)
  }

  /**
   * Sends a file to be stored on the FTP server. Returns true if the file
   * transfer was successful. The file is sent in passive mode to avoid NAT or
   * firewall problems at the client end.
   */
  def stor(inputStream: InputStream, filename: String): Boolean = {

    val input: BufferedInputStream = new BufferedInputStream(inputStream)
    sendLine("PASV")
    var response: String = readLine
    validateResponse(response,"227 ")

    val (ip,port) = getIpPortfromPASV(response)

    sendLine("STOR " + filename)
    val dataSocket: Socket = new Socket(ip, port)
    response = readLine
    validateResponse(response,"125 ")

    val output: BufferedOutputStream = new BufferedOutputStream(dataSocket.getOutputStream)
    val buffer: Array[Byte] = new Array[Byte](4096)
    var bytesRead: Int = 0
    while ((({
      bytesRead = input.read(buffer); bytesRead
    })) != -1) {
      output.write(buffer, 0, bytesRead)
    }
    output.flush
    output.close
    input.close
    response = readLine
    return response.startsWith("226 ")
  }

  /**
   * Enter binary mode for sending binary files.
   */
  def bin: Boolean = {
    sendLine("TYPE I")
    val response: String = readLine
    return (response.startsWith("200 "))
  }

  /**
   * Enter ASCII mode for sending text files. This is usually the default mode.
   * Make sure you use binary mode if you are sending images or other binary
   * data, as ASCII mode is likely to corrupt them.
   */
  def ascii: Boolean = {
    sendLine("TYPE A")
    val response: String = readLine
    return (response.startsWith("200 "))
  }

  /**
   * Sends a raw command to the FTP server.
   */
  private def sendLine(line: String) {
    if (socket == null) {
      throw new IOException("FTP.SimpleFTP is not connected.")
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

  private def readLine: String = {
    val line: String = reader.readLine
    if (DEBUG) {
      System.out.println("< " + line)
    }
    return line
  }

  def getIpPortfromPASV(response : String) : (String,Int) ={
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

  def validateResponse(response : String,codigo : String) : Any = codigo match {
    case "220 " => {
      if (!response.startsWith(codigo)) {
        throw new IOException("SimpleFTP received an unknown response when connecting to the FTP server: " + response)
      }
    }
    case "331 " => {
      if (!response.startsWith(codigo)) {
        throw new IOException("SimpleFTP received an unknown response after sending the user: " + response)
      }
    }
    case "230 " => {
      if (!response.startsWith(codigo)) {
        throw new IOException("SimpleFTP was unable to log in with the supplied password: " + response)
      }
    }
    case "257 " => {
      var dir : String = null;
      if (response.startsWith(codigo)) {
        val firstQuote: Int = response.indexOf('\"')
        val secondQuote: Int = response.indexOf('\"', firstQuote + 1)
        if (secondQuote > 0) {
          dir = response.substring(firstQuote + 1, secondQuote)
        }
      }
      dir
    }
    case "257 " => {
      if (!response.startsWith("227 ")) {
        throw new IOException("FTP.SimpleFTP could not request passive mode: " + response)
      }
    }
    case "125 " => {
      if (!response.startsWith("125 ")) {
        throw new IOException("FTP.SimpleFTP was not allowed to send the file: " + response)
      }
    }
  }

  private var socket: Socket = null
  private var reader: BufferedReader = null
  private var writer: BufferedWriter = null
  private val DEBUG: Boolean = false
}