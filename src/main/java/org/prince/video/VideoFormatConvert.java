package org.prince.video;

import java.io.File;
import java.io.IOException;

import javax.swing.JTextArea;

public class VideoFormatConvert {
	
	private JTextArea console;
	
	public VideoFormatConvert(JTextArea console) {
		this.console = console;
	}

	public boolean convertingMethod(String path, String fileName) {
		
		String inputPath = "\"" + path + File.separator + fileName + " cts.avi\"";
		String outputPath = "\"" + path + File.separator + fileName + " cts.mp4\"";
		String consoleData = console.getText();
		String ffmpegCommand = String.format("ffmpeg -i %s -c:v libx264 -crf 15 -preset medium -tune film -pix_fmt yuv420p %s", inputPath, outputPath);
		System.out.println("Command sent : " + ffmpegCommand);
		console.setText("Starting the conversion....\n");
		console.append(ffmpegCommand + "\n");
		
		ProcessBuilder builder = new ProcessBuilder(ffmpegCommand.split(" "));
		builder.inheritIO();
		try {
			Process process = builder.start();
			
			int exitCode = process.waitFor();
			if(exitCode == 0) {
				console.setText(consoleData);
				System.out.println("Video Conversion Successfull.");
				return true;
			}else {
				console.setText(consoleData);
				System.out.println("Error converting the video.");
				return false;
			}
			
		}catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}
}
