package autodetect.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/*
 * This class will check whether android device is connected to the system or not
 * by executing the adb commands. If device is connected it will get the connected device information.  
 */
public class AndroidDeviceDetails {

	private static final Logger LOG = Logger.getLogger(AndroidDeviceDetails.class);
	private final String ADB_PATH;
	private boolean isDeviceConnected = false;
	private Map<String, String> allDeviceDetails = new LinkedHashMap<>();

	public AndroidDeviceDetails() throws ADBPathNotFound, DeviceNotConnected {
		PropertyConfigurator.configure("src/main/resources/log4j.properties");
		this.ADB_PATH = this.getADBLocation() + "\\platform-tools\\adb.exe";
		this.isDeviceConnected = this.checkDevice();
		if (this.isDeviceConnected == false) {
			throw new DeviceNotConnected("Device is not connected . Please connect device and enable developer mode.");
		}
		this.getDeviceAllProperties();
	}

	/**
	 * This method gets ADB system Path from system environment variables
	 * 
	 * @return String ADB system Path
	 * @throws ADBPathNotFound
	 */
	private String getADBLocation() throws ADBPathNotFound {
		LOG.info("Get ADB system Path start");
		String adbPath = null;
		try {
			adbPath = System.getenv("ANDROID_HOME");
			if (adbPath == null) {
				throw new ADBPathNotFound(
						"ANDROID_HOME Environment Variable is not found . Please set the path to root folder of Android SDK");
			}
		} catch (ADBPathNotFound e) {
			throw e;
		}
		return adbPath;
	}

	/**
	 * This method checks whether the device is connected or not
	 * 
	 * @return boolean
	 */
	private boolean checkDevice() {

		LOG.info("Finding active device");
		boolean isDeviceConnected = false;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(ADB_PATH + " devices");
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			Stream<String> stream = in.lines();
			if (stream.count() > 2) {
				LOG.info("Active device found");
				isDeviceConnected = true;
			}
			process.destroy();
		} catch (IOException exception) {
			LOG.error(exception.getMessage(), exception);
		}
		return isDeviceConnected;
	}

	/**
	 * This method gets the Device properties
	 */

	private void getDeviceAllProperties() {
		LOG.info("getDeviceAllProperties start");
		Process processList = null;

		try {
			processList = Runtime.getRuntime().exec(this.ADB_PATH + "  shell  getprop");

			BufferedReader inputs = new BufferedReader(new InputStreamReader(processList.getInputStream()));
			Stream<String> stream = inputs.lines();
			stream.forEach((String s) -> {
				this.setDetails(s);
			});

			processList.destroy();

		} catch (IOException exception) {
			LOG.error(exception.getMessage(), exception);
		}

	}

	private void setDetails(String s) {
		if (s.contains("[net.bt.name]")) {
			this.allDeviceDetails.put("osName", s.substring(s.lastIndexOf('[') + 1, s.lastIndexOf(']')));
		} else if (s.contains("[ro.build.version.release]")) {
			this.allDeviceDetails.put("version", s.substring(s.lastIndexOf('[') + 1, s.lastIndexOf(']')));
		} else if (s.contains("[ro.product.manufacturer]")) {
			this.allDeviceDetails.put("manufacturer", s.substring(s.lastIndexOf('[') + 1, s.lastIndexOf(']')));
		} else if (s.contains("[ro.boot.serialno]")) {

			this.allDeviceDetails.put("deviceId", s.substring(s.lastIndexOf('[') + 1, s.lastIndexOf(']')));
		} else if (s.contains("[ro.product.model]")) {

			this.allDeviceDetails.put("model", s.substring(s.lastIndexOf('[') + 1, s.lastIndexOf(']')));
		} else if (s.contains("[ro.build.version.sdk]")) {

			this.allDeviceDetails.put("api", s.substring(s.lastIndexOf('[') + 1, s.lastIndexOf(']')));
		} else if (s.contains("[ro.yulong.product.devicename]")) {

			this.allDeviceDetails.put("deviceName", s.substring(s.lastIndexOf('[') + 1, s.lastIndexOf(']')));
		} else if (s.contains("[gsm.sim.operator.alpha]")) {

			this.allDeviceDetails.put("simNames", s.substring(s.lastIndexOf('[') + 1, s.lastIndexOf(']')));
		}

	}

	/**
	 * This method returns Android device OS Name
	 * 
	 * @return String
	 */
	public String getOSName() {
		return this.allDeviceDetails.get("osName");
	}

	/**
	 * This method returns Android device version number
	 * 
	 * @return String
	 */
	public String getVersion() {
		return this.allDeviceDetails.get("version");
	}

	/**
	 * This method returns Android device manufacturer
	 * 
	 * @return String
	 */
	public String getManufacturer() {
		return this.allDeviceDetails.get("manufacturer");
	}

	/**
	 * This method returns Android device deviceId
	 * 
	 * @return String
	 */
	public String getDeviceId() {
		return this.allDeviceDetails.get("deviceId");
	}

	/**
	 * This method returns Android device model
	 * 
	 * @return String
	 */
	public String getModel() {
		return this.allDeviceDetails.get("model");
	}

	/**
	 * This method returns Android device api level number
	 * 
	 * @return String
	 */
	public String getApi() {
		return this.allDeviceDetails.get("api");
	}

	/**
	 * This method returns Android device deviceName
	 * 
	 * @return String
	 */
	public String getDeviceName() {
		return this.allDeviceDetails.get("deviceName");
	}

	/**
	 * This method returns Android device simNames
	 * 
	 * @return String
	 */
	public String getSimNames() {
		return this.allDeviceDetails.get("simNames");
	}

}
