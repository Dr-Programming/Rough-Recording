package org.prince;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.prince.camera.CameraCapture;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.JLayeredPane;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.Image;

import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.event.ActionEvent;

public class Master {

	private JFrame frame;
	private JTextField rCodeTF;
	private JTextField textField_1;
	private JLabel videoLabel;

	/**
	 * Launch the application.
	 */
	

	/**
	 * Create the application.
	 */
	public Master() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1061, 778);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("Application");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Close");
		mntmNewMenuItem.setHorizontalAlignment(SwingConstants.CENTER);
		mnNewMenu.add(mntmNewMenuItem);
		
		JMenu mnNewMenu_1 = new JMenu("Action Panels");
		menuBar.add(mnNewMenu_1);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Recording");
		mnNewMenu_1.add(mntmNewMenuItem_1);
		
		JLayeredPane layeredPane = new JLayeredPane();
		frame.getContentPane().add(layeredPane, BorderLayout.CENTER);
		layeredPane.setLayout(new CardLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.PINK);
		layeredPane.add(panel_1, "name_1913400080547600");
		panel_1.setLayout(null);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(54, 54, 584, 393);
		panel_1.add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		videoLabel = new JLabel("");
		videoLabel.setIcon(null);
		videoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel_2.add(videoLabel, BorderLayout.CENTER);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(679, 54, 311, 393);
		panel_1.add(panel_3);
		panel_3.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("R-Code:");
		lblNewLabel_1.setFont(new Font("Monospaced", Font.BOLD, 18));
		lblNewLabel_1.setBounds(26, 59, 77, 25);
		panel_3.add(lblNewLabel_1);
		
		rCodeTF = new JTextField();
		rCodeTF.setFont(new Font("Tahoma", Font.PLAIN, 18));
		rCodeTF.setBounds(113, 58, 188, 25);
		panel_3.add(rCodeTF);
		rCodeTF.setColumns(10);
		
		JButton startRecBtn = new JButton("Start Recording");
		startRecBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startRecording();
			}
		});
		startRecBtn.setBounds(26, 117, 130, 23);
		panel_3.add(startRecBtn);
		
		JLabel lblNewLabel_1_1 = new JLabel("Opreator Name:");
		lblNewLabel_1_1.setFont(new Font("Monospaced", Font.BOLD, 18));
		lblNewLabel_1_1.setBounds(65, 503, 154, 25);
		panel_1.add(lblNewLabel_1_1);
		
		textField_1 = new JTextField();
		textField_1.setFont(new Font("Tahoma", Font.PLAIN, 18));
		textField_1.setColumns(10);
		textField_1.setBounds(229, 502, 224, 25);
		panel_1.add(textField_1);
	}
	
	private void startRecording() {
		System.out.println("button Clicked...");
//		CameraCapture cameraCapture = new CameraCapture(0,15);
//		System.out.println(cameraCapture.initializeCamera());
//		cameraCapture.startLiveFeed(this.videoLabel);
		VideoCapture capture = new VideoCapture(0);
		Mat frame = new Mat();
		System.out.println("1");
//		if(!capture.isOpened()) {
//			capture.open(0);
//		}
        while (capture.isOpened()) {
            capture.read(frame);
            if (!frame.empty()) {
                Image image = matToBufferedImage(frame);
                videoLabel.setIcon(new ImageIcon(image));
            }
        }
        System.out.println("exited");
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
	
//	public static void main(String[] args) {
//		nu.pattern.OpenCV.loadLocally();
//		try {
//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
//				| UnsupportedLookAndFeelException e) {
//			e.printStackTrace();
//		}
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					Master window = new Master();
//					window.frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}
}
