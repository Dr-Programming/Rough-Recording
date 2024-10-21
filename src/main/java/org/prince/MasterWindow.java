package org.prince;

import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.prince.camera.CameraCapture;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MasterWindow extends JFrame {
	
	private VideoCapture videoCapture;

	private JPanel contentPane;
	private JLabel videoLabel;
	private volatile boolean isLive = false;
	private boolean threadStrated = false;
	private Future<?> future;
	private CameraCapture cameraCapture;
	
	private ExecutorService service = Executors.newCachedThreadPool();

	/**
	 * Create the frame.
	 */
	public MasterWindow() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				getResourceClose();
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 934, 668);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		Thread myThread = new Thread(this::startFeed);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		videoLabel = new JLabel("");
		videoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		videoLabel.setBounds(40, 45, 551, 369);
		panel.add(videoLabel);
		
		
		
		JButton startBtn = new JButton("Start feed");
		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!isLive) {
					cameraCapture = new CameraCapture(0, 15);
					boolean check = cameraCapture.initializeCamera();
					if(!check) {
						return;
					}
					System.out.println("Camera capture Up and active....");
//					videoCapture = new VideoCapture(0);
					isLive = true;
					Task task = new Task();
					future = service.submit(task);
					
//					myThread.start();
//					if(!threadStrated) {
//						threadStrated = true;
//						myThread.start();
//					}else {
//						myThread.notify();
//					}
				}else {
					future.cancel(true);
					if(cameraCapture.releaseResources()) {
						System.out.println("resourses released");
					}
					cameraCapture = null;
//					if(videoCapture.isOpened()) {
//						videoCapture.release();
//					}
					videoLabel.setIcon(null);
					isLive = false;
				}
			}
		});
		startBtn.setBounds(708, 145, 89, 23);
		panel.add(startBtn);
	}
	
	private void startFeed() {
		while(true) {
			if(!cameraCapture.isCaptureOpen()) {
				return;
			}
			Image image = cameraCapture.getLiveFeed();
			videoLabel.setIcon(new ImageIcon(image));
		}
		
//		if(!isLive) {
//			Mat frame = new Mat();
//			isLive = true;
//			while(videoCapture.isOpened()) {
//				videoCapture.read(frame);
//				if(!frame.empty()) {
//					Image image = matToBufferedImage(frame);
//					videoLabel.setIcon(new ImageIcon(image));
//				}
//			}
//		}else {
//			if(videoCapture.isOpened()) {
//				videoCapture.release();
//			}
//			videoLabel.setIcon(null);
//			isLive = false;
//		}
		
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
	
	private class Task implements Callable<Void>{

		@Override
		public Void call() throws InterruptedException {
			while(true) {
					startFeed();
				if (Thread.currentThread().isInterrupted()) {
	                throw new InterruptedException("Thread interrupted");
	            }
			}
		}
		
	}
	
	private void getResourceClose() {
		if(cameraCapture != null) {
			cameraCapture.releaseResources();
		}
		service.shutdownNow();
	}
	
	
	/**
	 * Launch the application.
	 */
	
//	public static void main(String[] args) {
//		nu.pattern.OpenCV.loadLocally();
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					MasterWindow frame = new MasterWindow();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}
}
