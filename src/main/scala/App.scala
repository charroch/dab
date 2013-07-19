package com.novoda

import com.android.ddmlib.{AndroidDebugBridge => j_ADB, IDevice}


class App extends xsbti.AppMain {
  def run(config: xsbti.AppConfiguration) = {
    Exit(App.run(config.arguments))
  }
}

object App {

  def asString(device: IDevice): String = {
    val serial = device.getSerialNumber
    val ip = device.getProperty("net.gprs.local-ip")
    val hostname = device.getProperty("net.hostname")
    val sdk = device.getProperty("ro.build.version.release")
    val manufacturer = device.getProperty("ro.product.manufacturer")
    val model = device.getProperty("ro.product.model")
    s"$serial\t\t$ip\t\t$hostname\t\t$sdk\t\t$manufacturer\t\t$model"
  }

  def run(args: Array[String]): Int = {
    j_ADB.initIfNeeded(false)
    val adb = j_ADB.createBridge
    val devices = adb.getDevices
    j_ADB.getBridge.getDevices.foreach(d => println("-> " + asString(d)))
    j_ADB.terminate()
    0
  }

  def print = (d: IDevice) => {

  }

  def main(args: Array[String]) {
    run(args)
  }

  implicit class AndroidDebugBridge(original: j_ADB) {

    lazy val adb = {
      j_ADB.init(false)
      j_ADB.createBridge()
    }

    def devices = adb.getDevices


  }

}

/**
 * Serial IP Hostname Release SDK manufacturer, model
 */
case class Exit(val code: Int) extends xsbti.Exit

case class Device(val serial: String, val IP: String, val hostname: String, val release: String, val SDK: String, val manufacturer: String, val model: String)





