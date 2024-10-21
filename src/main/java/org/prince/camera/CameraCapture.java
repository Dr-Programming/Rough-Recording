package org.prince.camera;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

//import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

public class CameraCapture {
	
	private VideoCapture videoCapture;
	private double FPS;
	private int cameraID;
//	private boolean isLiveFeeding = false;
	private Mat frame = new Mat();
//	private Mat rotatedFrame = new Mat();
//	private LiveFeed liveFeed;
	private boolean isRecording = false;
	private VideoWriter videoWriter;
	private String savedPath="";
	
	public CameraCapture(int cameraID, double FPS) {
		nu.pattern.OpenCV.loadLocally();
		this.cameraID = cameraID;
		this.FPS = FPS;
//		liveFeed = new LiveFeed();
	}
	
	public CameraCapture() {
		
	}
	
	public boolean initializeCamera() {
		
		videoCapture = new VideoCapture(cameraID);
		videoCapture.set(Videoio.CAP_PROP_FRAME_WIDTH, 1920);
		videoCapture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 1080);
		videoCapture.set(Videoio.CAP_PROP_FPS, FPS);
		
		if(videoCapture != null) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean releaseResources() {
		if(videoCapture.isOpened()) {
			videoCapture.release();
			return true;
		}else {
			return false;
		}
	}
	
	public Image getLiveFeed() {
//		if(!isLiveFeeding) {
//			videoCapture.open(cameraID);
//			liveFeed.startFeed(this.videoCapture, videoLabel);
//			if(!videoCapture.isOpened()) {
//				videoCapture.open(cameraID);
//				System.out.println("Camera is opened");
//			}
			
//			while(videoCapture.isOpened()) {
//				System.out.println("reading Frame");
				videoCapture.read(frame);
//				Core.rotate(frame, rotatedFrame, Core.ROTATE_90_CLOCKWISE);
				if(!frame.empty()) {
					Image image = matToBufferedImage(frame);
					return image;
				}else {
					return null;
				}
//			}
//		}else {
//			
//		}
	}
	public VideoCapture getVideoCapture() {
		System.out.println("video capture sent");
		return videoCapture;
	}
	
	public boolean isCaptureOpen() {
    	if(videoCapture.isOpened()) {
    		return true;
    	}else {
    		return false;
    	}
    }
	
	public boolean recordFrames(String fileName, String path) {
		if(videoCapture.isOpened() && !isRecording) {
//			String fName = "Videos/"+fileName+".avi";
//			String fName = "src/main/resources/Videos/"+fileName+".avi";
			String fName = path+fileName+".avi";
			videoWriterAction(fName);
			savedPath = fName;
			return true;
		}else {
			return false;
		}
	}
	
	public String getVideoPath() {
		return savedPath;
	}
	
	private void videoWriterAction(String fileName) {
		long stopTime = System.currentTimeMillis();
		while(System.currentTimeMillis() - stopTime <=1000) {
			
		}
		videoWriter = new VideoWriter(fileName,
				VideoWriter.fourcc('M', 'J', 'P', 'G'),
				25,
				new org.opencv.core.Size(1920,1080),
				true);
		long startTime = System.currentTimeMillis();
		Mat videoFrame = new Mat();
		while(System.currentTimeMillis() - startTime <= 10000) {
			System.out.println(java.time.LocalTime.now());
			if(videoCapture.read(videoFrame)) {
				videoWriter.write(videoFrame);
			}
		}
		videoWriter.release();
		videoWriter = null;
		System.out.println("Video Saved.");
	}
	
	private Image matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] b = new byte[bufferSize];
        mat.get(0, 0, b);  // Get all the pixels
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }
}
