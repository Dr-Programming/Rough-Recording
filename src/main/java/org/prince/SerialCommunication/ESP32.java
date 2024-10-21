package org.prince.SerialCommunication;

import java.util.LinkedList;
import java.util.List;

import com.fazecast.jSerialComm.SerialPort;

public class ESP32 {
	
	private SerialPort[] ports;
	private static final String COMMAND_TO_ESP = "1";
	
	private List<String> portNameList;
	
	public ESP32(){
		ports = SerialPort.getCommPorts();
		ini();
	}
	
	private void ini() {
		if(ports != null && ports.length>0) {
			portNameList = new LinkedList<String>();
			for(SerialPort port : ports) {
				portNameList.add(port.getSystemPortName()+ " - " + port.getDescriptivePortName());
//				System.out.println(port.getSystemPortName()+ " - " + port.getDescriptivePortName());
			}
		}
	}

	public LinkedList<String> getPortNameList() {
		return (LinkedList<String>) portNameList;
	}

	public boolean sendMessage(int portIndex){
		if(sendSerialCommand(portIndex)) {
			return true;
		}else {
			return false;
		}
	}
	
	private boolean sendSerialCommand(int portIndex) {
		ports[portIndex].setBaudRate(115200);
		if(ports[portIndex].openPort()) {
			System.out.println("Port " + ports[portIndex].getSystemPortName() + " opened Successfully.");
			try {
				ports[portIndex].getOutputStream().write(COMMAND_TO_ESP.getBytes());
				ports[portIndex].getOutputStream().flush();
				System.out.println("Command Sent");
				ports[portIndex].closePort();
				System.out.println("Port Closed");
			}catch(Exception e) {
				e.printStackTrace();
				return false;
			}finally {
				if(ports[portIndex].isOpen()) {
					ports[portIndex].closePort();
				}
			}
			return true;
		}else {
			return false;
		}
	}
}
