package org.prince.camera;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

public class CameraCapture {
	
	private boolean isRecording = false;
	
	private double FPS;
	
	private int cameraID;
	
	private String savedPath="";
	
	private Mat frame = new Mat();
	
	private VideoCapture videoCapture;
	
	private VideoWriter videoWriter;	

	public CameraCapture(int cameraID, double FPS) {
		nu.pattern.OpenCV.loadLocally();
		this.cameraID = cameraID;
		this.FPS = FPS;
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
		videoCapture.read(frame);
//				Core.rotate(frame, rotatedFrame, Core.ROTATE_90_CLOCKWISE);
		if(!frame.empty()) {
			Image image = matToBufferedImage(frame);
			return image;
		}else {
			return null;
		}
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
	
	public boolean recordFrames(String path, String weight) {
		if(videoCapture.isOpened() && !isRecording) {
			String fName = path + File.separator + weight +" cts.avi";
			videoWriterAction(fName, weight);
			savedPath = fName;
			return true;
		}else {
			return false;
		}
	}
	
	public String getVideoPath() {
		return savedPath;
	}
	
	private void videoWriterAction(String fileName, String weight) {
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
		
		String logoFilePath = null;
		try {
			logoFilePath = extractResourceToTempFile("/images/SparkleDiWatermark.png");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ERROR PRODUCING THE FILE PATH");
			return;
		}
		
		Mat logo = Imgcodecs.imread(logoFilePath, Imgcodecs.IMREAD_UNCHANGED);
		if(logo.empty()) {
			System.out.println("LOGO is EMPTY");
			return;
		}
		
        int frameHeight = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
        
        Mat resizedLogo = new Mat();
        int logoWidth = 170;
        int logoHeight = 170;
        
        Imgproc.resize(logo, resizedLogo, new Size(logoWidth, logoHeight));	
		
		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
		while(System.currentTimeMillis() - startTime <= 10000) {
			System.out.println(java.time.LocalTime.now());
			if(videoCapture.read(videoFrame)) {
				
				Imgproc.putText(videoFrame, 
						weight + " cts", 
						new Point(videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH)-300, 100), 
						Imgproc.FONT_HERSHEY_PLAIN,
						3.0, 
						new Scalar(255, 255, 255),
						3);
				
				Imgproc.putText(videoFrame,
						timeStamp.toString(), 
						new Point(videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH) - 450, videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT) - 100),
						Imgproc.FONT_HERSHEY_PLAIN,
						1.5, 
						new Scalar(255, 255, 255),
						1);
				
				int xPos = 50;
				int yPos = frameHeight - resizedLogo.height() - 40;
				
				overlayImage(videoFrame, resizedLogo, new Point(xPos, yPos));

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
	
	public void overlayImage(Mat background, Mat logo, Point position) {
        for (int y = 0; y < logo.rows(); y++) {
            for (int x = 0; x < logo.cols(); x++) {
                double[] logoPixel = logo.get(y, x);
                if (logoPixel[3] != 0) {
                    background.put((int) (position.y + y), (int) (position.x + x), 
                            new double[]{logoPixel[0], logoPixel[1], logoPixel[2]});
                }
            }
        }
    }
	
	private String extractResourceToTempFile(String resourcePath) throws IOException {
        File tempFile = File.createTempFile("resource-", ".png");
        try (var inputStream = CameraCapture.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return tempFile.getAbsolutePath();
    }
}
