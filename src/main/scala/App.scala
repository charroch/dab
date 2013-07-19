package com.novoda

import com.android.ddmlib.AndroidDebugBridge.{IDeviceChangeListener, IDebugBridgeChangeListener, IClientChangeListener}
import com.android.ddmlib.{AndroidDebugBridge, IDevice, Client}
import java.util.concurrent.{TimeUnit, CountDownLatch}


class App extends xsbti.AppMain {
  def run(config: xsbti.AppConfiguration) = {
    Exit(App.run(config.arguments))
  }
}

object App {

  val latch: CountDownLatch = new CountDownLatch(1)

  val clientChangeListener = new IClientChangeListener {
    def clientChanged(client: Client, index: Int) {
    }
  }

  val debugBridgeListener = new IDebugBridgeChangeListener {
    def bridgeChanged(bridge: AndroidDebugBridge) {
    }
  }

  val deviceChangeListener = new IDeviceChangeListener {
    def deviceConnected(device: IDevice) {
    }

    def deviceDisconnected(device: IDevice) {
    }

    def deviceChanged(device: IDevice, changeMask: Int) {
      latch.countDown()
    }
  }


  def asString(device: IDevice): String = {
    val serial = device.getSerialNumber
    val ip = device.getProperty("dhcp.wlan0.ipaddress")
    val hostname = device.getProperty("net.hostname")
    val release = device.getProperty("ro.build.version.release")
    val sdk = device.getProperty("ro.build.version.sdk")
    val manufacturer = device.getProperty("ro.product.manufacturer")
    val model = device.getProperty("ro.product.model")
    s"$serial\t\t$sdk\t\t$release\t\t$hostname\t\t$manufacturer\t\t$model\t\t$ip"
  }


  def run(args: Array[String]): Int = {
    AndroidDebugBridge.addClientChangeListener(clientChangeListener)
    AndroidDebugBridge.addDebugBridgeChangeListener(debugBridgeListener)
    AndroidDebugBridge.addDeviceChangeListener(deviceChangeListener)

    AndroidDebugBridge.initIfNeeded(false)
    val adb = AndroidDebugBridge.createBridge

    latch.await(1, TimeUnit.SECONDS)
    val devices = adb.getDevices

    devices.foreach(d => println(asString(d)))
    AndroidDebugBridge.terminate()
    0
  }

  def print = (d: IDevice) => {

  }

  def main(args: Array[String]) {
    run(args)
  }
}

/**
 * Serial IP Hostname Release SDK manufacturer, model
 */
case class Exit(val code: Int) extends xsbti.Exit

case class Device(val serial: String, val IP: String, val hostname: String, val release: String, val SDK: String, val manufacturer: String, val model: String)





