package org.prince.SerialCommunication;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.prince.security.client.HardwareFingerprint;

import com.fazecast.jSerialComm.SerialPort;

public class ESP32 {
	
	private List<String> portNameList;
	
	private SerialPort[] ports;
	
	private SerialPort selectedPort = null;
	
	private boolean isConnected = false;

	
	public ESP32(){
		ports = SerialPort.getCommPorts();
		ini();
	}
	
	
	private void ini() {
		if(ports != null && ports.length>0) {
			portNameList = new LinkedList<String>();
			for(SerialPort port : ports) {
				if(port.getDescriptivePortName().equalsIgnoreCase("Silicon Labs CP210x USB to UART Bridge (COM3)")) {
					portNameList.add("SparkleDi Dome");
					continue;
				}
				portNameList.add(port.getSystemPortName()+ " - " + port.getDescriptivePortName());
			}
		}
	}
	public LinkedList<String> getPortNameList() {
		return (LinkedList<String>) portNameList;
	}
	
	public boolean sendData(String data) {
		if(selectedPort == null) {
			return false;
		}
		
		try {
			selectedPort.getOutputStream().write(("3%"+data).getBytes());
			selectedPort.getOutputStream().flush();
			System.out.println("Data Sent.");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void setSelectedPort(int portIndex) {
		selectedPort = ports[portIndex];
	}
	
	
	public boolean getConnection() {
		if(selectedPort == null) {
			return false;
		}
		if(selectedPort.isOpen()) {
			return false;
		}
		
		selectedPort.setBaudRate(115200);
		selectedPort.openPort();
		
		try {
			selectedPort.getOutputStream().write("1".getBytes());
			selectedPort.getOutputStream().flush();
			System.out.println("Requested for verification.");
			
			Thread.sleep(1000);
			
			InputStream inputStream = selectedPort.getInputStream();
			Scanner sc = new Scanner(inputStream);
			System.out.println("Waiting for ESP32 response...");
			while(sc.hasNextLine()) {
				String response = sc.nextLine();
				System.out.println("Received from ESP32: " + response);
				if (response.contains("0010")) {
					System.out.println("Breaking 1");
                    break;
                }
			}
			
			selectedPort.getOutputStream().write(HardwareFingerprint.getHardwareFingerprint().getBytes());
			selectedPort.getOutputStream().flush();
			System.out.println("Fingerprint Sent");
			System.out.println("Waiting for ESP32 response...");
			
			Thread.sleep(3000);
			
			while(sc.hasNextLine()) {
				String response = sc.nextLine();
				System.out.println("Received from ESP32: " + response);
				if(response.contains("1110011")) {
					isConnected = true;
					System.out.println("Verification Success");
					break;
				}
			}
			
			sc.close();
			inputStream.close();
			return true;
			
		}catch(InterruptedException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void closeConnection() {
		if(selectedPort == null) {
			System.out.println("Port Closed");
			return;
		}
		if(selectedPort.isOpen()) {
			try {
				if(isConnected) {
					selectedPort.getOutputStream().write("2".getBytes());
					selectedPort.getOutputStream().flush();
					isConnected = false;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				System.out.println("Command Sent");
				selectedPort.closePort();
				System.out.println("Port Closed");
			}
			
		}
	}
}
