package org.prince;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.prince.SerialCommunication.ESP32;
import org.prince.camera.CameraCapture;
import org.prince.configuration.ConfigManager;
import org.prince.configuration.Fields;
import org.prince.properties.SampleDialog;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.CardLayout;
import javax.swing.SpringLayout;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Image;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JTextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;



public class ApplicationWindow {

	private JFrame frame;
	private JLabel VideoLabel_RP;
	
	private boolean isLive = false;
	private CameraCapture cameraCapture;
	private Future<?> FeedingFuture;
	private Future<?> recordingFuture;
	private ExecutorService service = Executors.newCachedThreadPool(); 
	private JButton startBtn;
	private JTextField RCode_TF;
	private boolean isRecording = false;
//	private String saveToFilePath;
	private ConfigManager configManager;
	private JMenu Port_Menu;
	private int portRequest = 0;
	private ESP32 esp32;
	private boolean isESP32 = false;
	private JMenuItem portList[];
	private int selectedPortIndex;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		nu.pattern.OpenCV.loadLocally();
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ApplicationWindow window = new ApplicationWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ApplicationWindow() {
		configManager = new ConfigManager();
		initialize();
		getapplicationDataSession();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				getResourcesReleased();
				setApplicationDataSession();
			}
		});
		frame.setBounds(100, 100, 1500, 1000);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel MasterPanel = new JPanel();
		frame.getContentPane().add(MasterPanel, BorderLayout.CENTER);
		MasterPanel.setLayout(new CardLayout(0, 0));
		
		JPanel RecordingPanel = new JPanel();
		MasterPanel.add(RecordingPanel, "name_2331494577939800");
		SpringLayout sl_RecordingPanel = new SpringLayout();
		RecordingPanel.setLayout(sl_RecordingPanel);
		
		JPanel ControlPanel_RP = new JPanel();
		sl_RecordingPanel.putConstraint(SpringLayout.NORTH, ControlPanel_RP, 10, SpringLayout.NORTH, RecordingPanel);
		sl_RecordingPanel.putConstraint(SpringLayout.SOUTH, ControlPanel_RP, -183, SpringLayout.SOUTH, RecordingPanel);
		sl_RecordingPanel.putConstraint(SpringLayout.EAST, ControlPanel_RP, -10, SpringLayout.EAST, RecordingPanel);
		ControlPanel_RP.setBackground(Color.WHITE);
		RecordingPanel.add(ControlPanel_RP);
		
		JPanel VideoPanel_RP = new JPanel();
		sl_RecordingPanel.putConstraint(SpringLayout.WEST, ControlPanel_RP, 7, SpringLayout.EAST, VideoPanel_RP);
		sl_RecordingPanel.putConstraint(SpringLayout.NORTH, VideoPanel_RP, 10, SpringLayout.NORTH, RecordingPanel);
		sl_RecordingPanel.putConstraint(SpringLayout.SOUTH, VideoPanel_RP, -183, SpringLayout.SOUTH, RecordingPanel);
		sl_RecordingPanel.putConstraint(SpringLayout.WEST, VideoPanel_RP, 10, SpringLayout.WEST, RecordingPanel);
		sl_RecordingPanel.putConstraint(SpringLayout.EAST, VideoPanel_RP, -571, SpringLayout.EAST, RecordingPanel);
		VideoPanel_RP.setBackground(Color.WHITE);
		RecordingPanel.add(VideoPanel_RP);
		VideoPanel_RP.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		VideoPanel_RP.add(panel, BorderLayout.NORTH);
		
		JLabel lblNewLabel = new JLabel("Recording View");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
		panel.add(lblNewLabel);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBackground(Color.WHITE);
		VideoPanel_RP.add(panel_4, BorderLayout.CENTER);
		panel_4.setLayout(new BorderLayout(0, 0));
		
		VideoLabel_RP = new JLabel("Camera Not Connected");
		VideoLabel_RP.setFont(new Font("Tahoma", Font.PLAIN, 14));
		VideoLabel_RP.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(VideoLabel_RP, BorderLayout.CENTER);
		
		JPanel ExtraPanel_RP = new JPanel();
		sl_RecordingPanel.putConstraint(SpringLayout.NORTH, ExtraPanel_RP, 6, SpringLayout.SOUTH, ControlPanel_RP);
		ControlPanel_RP.setLayout(null);
		
		startBtn = new JButton("Start Live Feed");
		startBtn.setMultiClickThreshhold(1L);
		startBtn.setFocusPainted(false);
		startBtn.setFont(new Font("Tahoma", Font.PLAIN, 14));
		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionButton();
			}
		});
		startBtn.setBounds(227, 52, 146, 31);
		ControlPanel_RP.add(startBtn);
		
		JLabel lblNewLabel_1 = new JLabel("R-Code :");
		lblNewLabel_1.setFont(new Font("Monospaced", Font.BOLD, 18));
		lblNewLabel_1.setBounds(55, 142, 88, 17);
		ControlPanel_RP.add(lblNewLabel_1);
		
		RCode_TF = new JTextField();
		RCode_TF.setFont(new Font("Tahoma", Font.PLAIN, 16));
		RCode_TF.setBounds(153, 142, 220, 21);
		ControlPanel_RP.add(RCode_TF);
		RCode_TF.setColumns(10);
		
		JButton recordBtn = new JButton("Start Recording");
		recordBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				recordBtnAction();
			}
		});
		recordBtn.setFont(new Font("Tahoma", Font.PLAIN, 14));
		recordBtn.setBounds(227, 252, 146, 31);
		ControlPanel_RP.add(recordBtn);
		sl_RecordingPanel.putConstraint(SpringLayout.WEST, ExtraPanel_RP, 10, SpringLayout.WEST, RecordingPanel);
		sl_RecordingPanel.putConstraint(SpringLayout.SOUTH, ExtraPanel_RP, -10, SpringLayout.SOUTH, RecordingPanel);
		sl_RecordingPanel.putConstraint(SpringLayout.EAST, ExtraPanel_RP, -10, SpringLayout.EAST, RecordingPanel);
		ExtraPanel_RP.setBackground(Color.WHITE);
		RecordingPanel.add(ExtraPanel_RP);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("View");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Restore Defaults");
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Resotre called");
				configManager.restoreDefaults();
				RCode_TF.setText(configManager.getProperty("savePath"));
			}
		});
		mnNewMenu.add(mntmNewMenuItem_1);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Recording View");
		mnNewMenu.add(mntmNewMenuItem);
		
		JMenuItem mntmNewMenuItem_2 = new JMenuItem("Save Settings");
		mntmNewMenuItem_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Save called");
				configManager.setProperty("savePath", RCode_TF.getText());
				configManager.saveUserProperties();
			}
		});
		mnNewMenu.add(mntmNewMenuItem_2);
		
		JMenu mnNewMenu_1 = new JMenu("Properties");
		menuBar.add(mnNewMenu_1);
		
		JMenuItem mntmNewMenuItem_3 = new JMenuItem("Save Location");
		mntmNewMenuItem_3.addActionListener( e -> new SampleDialog(frame, configManager).setVisible(true));
		mnNewMenu_1.add(mntmNewMenuItem_3);
		
		Port_Menu = new JMenu("COM PORTS");
		Port_Menu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!isESP32) {
					esp32 = new ESP32();
					isESP32 = true;
				}
				if(portRequest == 0) {
					getPortList();
				}
			}
		});
		mnNewMenu_1.add(Port_Menu);
	}
	
	private void actionButton() {
		if(!isLive) {
			cameraCapture = new CameraCapture(1, 60);
			boolean check = cameraCapture.initializeCamera();
			if(!check) {
				return;
			}
			System.out.println("Camera capture Up and active....");
			isLive = true;
			startBtn.setText("Stop Live Feed");
			FeedingTask feedingTask = new FeedingTask();
			FeedingFuture = service.submit(feedingTask);
		}else {
			FeedingFuture.cancel(true);
			if(cameraCapture.releaseResources()) {
				System.out.println("resourses released");
			}
			cameraCapture = null;
			VideoLabel_RP.setIcon(null);
			VideoLabel_RP.setText("Camera Not Connected");
			isLive = false;
			startBtn.setText("Start Live Feed");
		}
	}
	
	private void startFeed() {
		while(true) {
			if(!cameraCapture.isCaptureOpen()) {
				return;
			}
			Image image = cameraCapture.getLiveFeed();
			VideoLabel_RP.setIcon(new ImageIcon(image));
		}
	}
	
	private void recordBtnAction() {
		if(!isRecording && isLive) {
			System.out.println("Starting Recording....");
			RecordingTask recordingTask = new RecordingTask();
			isRecording = true;
			recordingFuture = service.submit(recordingTask);
			ESP32Con();
		}
	}
	
	private void startRecording() {
		if(cameraCapture.isCaptureOpen()) {
			if(cameraCapture.recordFrames(RCode_TF.getText())) {
				recordingFuture.cancel(true);
				System.out.println("========= Recording Performed Successfully ============");
				isRecording = false;
			}
		}
	}
	
	int az;
	private void getPortList() {
		LinkedList<String> portNameList = esp32.getPortNameList();
		if(!portNameList.isEmpty()) {
			portList = null;
			portList = new JMenuItem[portNameList.size()];
			Port_Menu.removeAll();
			for(az=0; az<portNameList.size(); az++) {
				portList[az] = new JMenuItem(portNameList.get(az));
				portList[az].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.out.println(portNameList.get(az-1)+ " is selected");
						selectedPortIndex = az-1;
					}
				});
				Port_Menu.add(portList[az]);
			}
		}
	}
	
	private void ESP32Con() {
		System.out.println("ESPcon called");
		esp32.sendMessage(selectedPortIndex);
	}
	
	private void getResourcesReleased() {
		if(cameraCapture != null) {
			cameraCapture.releaseResources();
		}
		service.shutdownNow();
	}
	
	private  void getapplicationDataSession() {
//		RCode_TF.setText(configManager.getProperty("savePath"));
//		Fields path = Fields.savePath;
		RCode_TF.setText(configManager.getProperty(Fields.savePath.toString()));
	}
	
	private void setApplicationDataSession() {
//		String path = RCode_TF.getText();
//		configManager.setProperty("savePath", path);
		configManager.saveUserProperties();
		System.out.println("Data saved successfully!");
	}
	
	private class FeedingTask implements Callable<Void>{

		@Override
		public Void call() throws InterruptedException {
			while(true) {
				startFeed();
				if(Thread.currentThread().isInterrupted()) {
					throw new InterruptedException("Thread interrupted");
				}
			}
		}
		
	}
	
	private class RecordingTask implements Callable<Void>{

		@Override
		public Void call() throws Exception {
			while(true) {
				startRecording();
				if(Thread.currentThread().isInterrupted()) {
					throw new InterruptedException("Thread interrupted");
				}
			}
		}
		
	}
}
