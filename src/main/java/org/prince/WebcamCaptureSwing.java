package org.prince;

import org.opencv.core.Mat;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WebcamCaptureSwing extends JFrame {
    private VideoCapture capture;
    private JLabel videoLabel;
    private boolean recording = false;
    private VideoWriter videoWriter;
    private volatile boolean isRecording = false;
    double FPS = 15;

    public WebcamCaptureSwing() {
        // Initialize VideoCapture (OpenCV)
        capture = new VideoCapture(1);
        
//        FPS = capture.get(org.opencv.videoio.Videoio.CAP_PROP_FPS);
//        if(FPS == 0) {
//        	FPS = 30;
//        }
        
        capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 1920);
        capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 1080);
        capture.set(Videoio.CAP_PROP_FPS, FPS);

        // Create the JFrame and components
        videoLabel = new JLabel();
        videoLabel.setBounds(0, 0, 712, 485);
        videoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JButton stopButton = new JButton("Stop Recording");
        stopButton.addActionListener(e -> stopRecording());

        JPanel panel = new JPanel();
        panel.setBounds(0, 587, 984, 33);
        panel.add(stopButton);
        getContentPane().setLayout(null);
        getContentPane().add(videoLabel);
        getContentPane().add(panel);
        JButton startButton = new JButton("Start Recording");
        startButton.setBounds(784, 231, 107, 23);
        getContentPane().add(startButton);
        startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
        });
        
                startButton.addActionListener(e -> startRecording());

        // JFrame settings
        this.setTitle("Webcam Capture with Swing");
        this.setSize(1000, 659);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        // Start video feed
        new Thread(this::startVideoFeed).start();
    }

    private void startVideoFeed() {
        if(!recording) {
        	Mat frame = new Mat();
            while (capture.isOpened()) {
                capture.read(frame);
                if (!frame.empty()) {
                    Image image = matToBufferedImage(frame);
                    videoLabel.setIcon(new ImageIcon(image));
                }
            }
        }else {
        	
        }
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
    
    long frameInterval = Math.round(1000.0/ FPS);

    private void startRecording() {
    	if (!isRecording) {
            isRecording = true;
            System.out.println("Recording started...");

            // Set up the VideoWriter to save the recording
            videoWriter = new VideoWriter("jew.avi", VideoWriter.fourcc('M', 'J', 'P', 'G'),
                    FPS, new org.opencv.core.Size(1920, 1080), true);
            
            new Thread(() -> {
                long lastFrameTime = System.currentTimeMillis();
                long startTime = System.currentTimeMillis();
                while (isRecording) {
                    Mat frame = new Mat();
                    
                    while(System.currentTimeMillis() - startTime < 10000) {
                    	if (capture.read(frame)) {
                    		System.out.println("frame captured");
                            videoWriter.write(frame);
                            long currentTime = System.currentTimeMillis();
                            long elapsedTime = currentTime - lastFrameTime;

                            // Sleep for the remainder of the frame interval
//                            if (elapsedTime < frameInterval) {
//                                try {
//                                	System.out.println("Try Block");
//                                    Thread.sleep(frameInterval - elapsedTime);
//                                } catch (InterruptedException e) {
//                                	System.out.println("Catch Block");
//                                    Thread.currentThread().interrupt();
//                                }
//                            }
                            lastFrameTime = System.currentTimeMillis();
                        }
                    }
                    isRecording = false;
                }
                videoWriter.release();  // Release resources when recording stops
                System.out.println("Recording stopped and resources released.");
            }).start();
            
//            new org.opencv.core.Size(640, 480)
            // Run the recording process in a separate thread
//            new Thread(() -> {
//            	long startTime = System.currentTimeMillis();
////            	if(!capture.isOpened()) {
////            		capture.open(1);
////            	}
//                while (isRecording) {
//                    Mat frame = new Mat();
//                    while(System.currentTimeMillis() - startTime < 20000) {
//                    	if (capture.read(frame)) {
//                    		System.out.println("frame captured");
//                            videoWriter.write(frame);
//                        }
//                    }
//                    isRecording = false;
//                }
//                videoWriter.release();  // Release resources when recording stops
//                System.out.println("Recording stopped and resources released.");
////                isRecording = false;
//            }).start();
        }
    }

    private void stopRecording() {
        recording = false;
        // Add your logic to stop recording and save the video file
    }

    public static void main(String[] args) {
        nu.pattern.OpenCV.loadLocally();
        SwingUtilities.invokeLater(WebcamCaptureSwing::new);
    }
}
