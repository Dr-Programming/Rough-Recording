package org.prince;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.prince.SerialCommunication.ESP32;
import org.prince.camera.CameraCapture;
import org.prince.configuration.ConfigManager;
import org.prince.files.FilesManager;
import org.prince.inputs.BorderType;
import org.prince.inputs.InputManager;
import org.prince.properties.SampleDialog;
import org.prince.search.SamplePanel;
import org.prince.video.VideoFormatConvert;

import com.github.sarxos.webcam.Webcam;



public class ApplicationWindow {

	private JFrame frame;
	
	private JButton startFeedBtn_RP;
	private JButton recordingBtn_RP;
	
	private JLabel VideoLabel_RP;
	private JLabel caratsLabel_RP;

	private JMenu cameraMenu;
	private JMenu comPortMenu;
	
	private JMenuItem portList[];
	
	private JPanel masterPanel;
	private JPanel recordingPanel;
	
	private JRadioButton karpanYesRB_RP;
	private JRadioButton dCodeYesRB_RP;
	
	private JTextArea console_RP;
	
	private JTextField dCodeTF_RP;
	private JTextField cameraStatusTF_RP;
	private JTextField portStatusTF_RP;
	private JTextField connStatusTF_RP;
	private JTextField karpanTF_RP;
	private JTextField weightTF_RP;
	
	private ExecutorService service = Executors.newCachedThreadPool();
	
	private Future<?> FeedingFuture;
	private Future<?> recordingFuture;
	private Future<?> videoConvertingFuture;
	
	private boolean isRecording = false;
	private boolean isESP32 = false;
	private boolean isLive = false;
	private boolean isCamSet = false;
	private boolean isPortSet = false;
	
	private int selectedPortIndex = 20001;
	private int camIndex = 10001;
	
	private String saveToPathOfVideo;

	private CameraCapture cameraCapture;
	
	private ConfigManager configManager;
	
	private ESP32 esp32;
	
	private FilesManager filesManager;

	
	
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
		filesManager = new FilesManager(configManager);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("SparkleDi v1");
		ImageIcon icon = new ImageIcon(getClass().getResource("/images/diamond.png"));
		frame.setIconImage(icon.getImage());
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
		
		masterPanel = new JPanel();
		frame.getContentPane().add(masterPanel, BorderLayout.CENTER);
		masterPanel.setLayout(new CardLayout(0, 0));
		
		recordingPanel = new JPanel();
		masterPanel.add(recordingPanel, "name_2331494577939800");
		SpringLayout sl_recordingPanel = new SpringLayout();
		recordingPanel.setLayout(sl_recordingPanel);
		
		JPanel ControlPanel_RP = new JPanel();
		sl_recordingPanel.putConstraint(SpringLayout.NORTH, ControlPanel_RP, 10, SpringLayout.NORTH, recordingPanel);
		sl_recordingPanel.putConstraint(SpringLayout.SOUTH, ControlPanel_RP, -183, SpringLayout.SOUTH, recordingPanel);
		sl_recordingPanel.putConstraint(SpringLayout.EAST, ControlPanel_RP, -10, SpringLayout.EAST, recordingPanel);
		ControlPanel_RP.setBackground(Color.WHITE);
		recordingPanel.add(ControlPanel_RP);
		
		JPanel VideoPanel_RP = new JPanel();
		sl_recordingPanel.putConstraint(SpringLayout.WEST, ControlPanel_RP, 6, SpringLayout.EAST, VideoPanel_RP);
		sl_recordingPanel.putConstraint(SpringLayout.EAST, VideoPanel_RP, -619, SpringLayout.EAST, recordingPanel);
		sl_recordingPanel.putConstraint(SpringLayout.NORTH, VideoPanel_RP, 10, SpringLayout.NORTH, recordingPanel);
		sl_recordingPanel.putConstraint(SpringLayout.SOUTH, VideoPanel_RP, -183, SpringLayout.SOUTH, recordingPanel);
		sl_recordingPanel.putConstraint(SpringLayout.WEST, VideoPanel_RP, 10, SpringLayout.WEST, recordingPanel);
		VideoPanel_RP.setBackground(Color.WHITE);
		recordingPanel.add(VideoPanel_RP);
		VideoPanel_RP.setLayout(new BorderLayout(0, 0));
		
		JPanel headingPanel_1_RP = new JPanel();
		headingPanel_1_RP.setBackground(Color.WHITE);
		VideoPanel_RP.add(headingPanel_1_RP, BorderLayout.NORTH);
		
		JLabel headindLabel_1_RP = new JLabel("Recording View");
		headindLabel_1_RP.setFont(new Font("Tahoma", Font.BOLD, 30));
		headingPanel_1_RP.add(headindLabel_1_RP);
		
		JPanel videoPanel_RP = new JPanel();
		videoPanel_RP.setBackground(Color.WHITE);
		VideoPanel_RP.add(videoPanel_RP, BorderLayout.CENTER);
		videoPanel_RP.setLayout(new BorderLayout(0, 0));
		
		VideoLabel_RP = new JLabel("Camera Not Connected");
		VideoLabel_RP.setFont(new Font("Tahoma", Font.PLAIN, 14));
		VideoLabel_RP.setHorizontalAlignment(SwingConstants.CENTER);
		videoPanel_RP.add(VideoLabel_RP, BorderLayout.CENTER);
		
		JPanel ExtraPanel_RP = new JPanel();
		sl_recordingPanel.putConstraint(SpringLayout.NORTH, ExtraPanel_RP, 6, SpringLayout.SOUTH, ControlPanel_RP);
		sl_recordingPanel.putConstraint(SpringLayout.SOUTH, ExtraPanel_RP, -10, SpringLayout.SOUTH, recordingPanel);
		ControlPanel_RP.setLayout(null);
		
		startFeedBtn_RP = new JButton("Start Live Feed");
		startFeedBtn_RP.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		startFeedBtn_RP.setMultiClickThreshhold(1L);
		startFeedBtn_RP.setFocusPainted(false);
		startFeedBtn_RP.setFont(new Font("Tahoma", Font.PLAIN, 16));
		startFeedBtn_RP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionButton();
			}
		});
		startFeedBtn_RP.setBounds(347, 83, 146, 31);
		ControlPanel_RP.add(startFeedBtn_RP);
		
		JLabel inputLabel_2_RP = new JLabel("Diamond No. :");
		inputLabel_2_RP.setFont(new Font("Monospaced", Font.BOLD, 20));
		inputLabel_2_RP.setBounds(15, 304, 156, 26);
		ControlPanel_RP.add(inputLabel_2_RP);
		
		dCodeTF_RP = new JTextField();
		dCodeTF_RP.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				dCodeTF_RP.setBorder(new InputManager().getFocusOuterBorder());
			}
			@Override
			public void focusLost(FocusEvent e) {
				dCodeTF_RP.setBorder(new InputManager().getDefaultOuterBorder());
			}
		});
		dCodeTF_RP.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == ' ') {
					e.consume();
				}
			}
		});
		dCodeTF_RP.setToolTipText("Enter Diamond Number");
		dCodeTF_RP.setBorder(new InputManager().getDefaultOuterBorder());
		dCodeTF_RP.setFont(new Font("Monospaced", Font.BOLD, 20));
		dCodeTF_RP.setBounds(181, 306, 156, 26);
		ControlPanel_RP.add(dCodeTF_RP);
		dCodeTF_RP.setColumns(10);
		
		recordingBtn_RP = new JButton("Start Recording");
		recordingBtn_RP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				recordBtnAction();
			}
		});
		recordingBtn_RP.setFont(new Font("Tahoma", Font.PLAIN, 14));
		recordingBtn_RP.setBounds(225, 430, 146, 31);
		ControlPanel_RP.add(recordingBtn_RP);
		
		JLabel infoLabel_1_RP = new JLabel("Camera :");
		infoLabel_1_RP.setFont(new Font("Tahoma", Font.PLAIN, 16));
		infoLabel_1_RP.setBounds(10, 13, 65, 17);
		ControlPanel_RP.add(infoLabel_1_RP);
		
		cameraStatusTF_RP = new JTextField();
		cameraStatusTF_RP.setBorder(null);
		cameraStatusTF_RP.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		cameraStatusTF_RP.setRequestFocusEnabled(false);
		cameraStatusTF_RP.setFocusTraversalKeysEnabled(false);
		cameraStatusTF_RP.setFocusable(false);
		cameraStatusTF_RP.setBackground(Color.WHITE);
		cameraStatusTF_RP.setEditable(false);
		cameraStatusTF_RP.setFont(new Font("Tahoma", Font.PLAIN, 15));
		cameraStatusTF_RP.setBounds(85, 12, 508, 20);
		ControlPanel_RP.add(cameraStatusTF_RP);
		cameraStatusTF_RP.setColumns(10);
		
		JLabel infoLabel_2_RP = new JLabel("Port      :");
		infoLabel_2_RP.setFont(new Font("Tahoma", Font.PLAIN, 16));
		infoLabel_2_RP.setBounds(10, 46, 65, 17);
		ControlPanel_RP.add(infoLabel_2_RP);
		
		portStatusTF_RP = new JTextField();
		portStatusTF_RP.setBorder(null);
		portStatusTF_RP.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		portStatusTF_RP.setRequestFocusEnabled(false);
		portStatusTF_RP.setFocusable(false);
		portStatusTF_RP.setFocusTraversalKeysEnabled(false);
		portStatusTF_RP.setFont(new Font("Tahoma", Font.PLAIN, 15));
		portStatusTF_RP.setEditable(false);
		portStatusTF_RP.setColumns(10);
		portStatusTF_RP.setBackground(Color.WHITE);
		portStatusTF_RP.setBounds(85, 45, 508, 20);
		ControlPanel_RP.add(portStatusTF_RP);
		
		JLabel infoLabel_3_RP = new JLabel("Connection Status :");
		infoLabel_3_RP.setFont(new Font("Cascadia Mono", Font.BOLD, 16));
		infoLabel_3_RP.setBounds(10, 85, 180, 26);
		ControlPanel_RP.add(infoLabel_3_RP);
		
		connStatusTF_RP = new JTextField();
		connStatusTF_RP.setBorder(null);
		connStatusTF_RP.setRequestFocusEnabled(false);
		connStatusTF_RP.setFocusable(false);
		connStatusTF_RP.setFocusTraversalKeysEnabled(false);
		connStatusTF_RP.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		connStatusTF_RP.setBackground(Color.WHITE);
		connStatusTF_RP.setEditable(false);
		connStatusTF_RP.setAutoscrolls(false);
		connStatusTF_RP.setFont(new Font("Cascadia Mono", Font.BOLD, 20));
		connStatusTF_RP.setBounds(200, 89, 123, 22);
		ControlPanel_RP.add(connStatusTF_RP);
		connStatusTF_RP.setColumns(10);
		
		JSeparator separator_RP = new JSeparator();
		separator_RP.setBounds(0, 149, 603, 2);
		ControlPanel_RP.add(separator_RP);
		
		JLabel headingLabel_2_RP = new JLabel("Diamond Details :");
		headingLabel_2_RP.setFont(new Font("Dubai", Font.BOLD, 26));
		headingLabel_2_RP.setBounds(15, 162, 217, 29);
		ControlPanel_RP.add(headingLabel_2_RP);
		
		JLabel inputLabel_1_RP = new JLabel("Karpan No.  :");
		inputLabel_1_RP.setFocusTraversalKeysEnabled(false);
		inputLabel_1_RP.setFocusable(false);
		inputLabel_1_RP.setVerifyInputWhenFocusTarget(false);
		inputLabel_1_RP.setFont(new Font("Monospaced", Font.BOLD, 20));
		inputLabel_1_RP.setBounds(15, 238, 156, 31);
		ControlPanel_RP.add(inputLabel_1_RP);
		
		karpanTF_RP = new JTextField();
		karpanTF_RP.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				karpanTF_RP.setBorder(new InputManager().getFocusOuterBorder());
			}
			@Override
			public void focusLost(FocusEvent e) {
				karpanTF_RP.setBorder(new InputManager().getDefaultOuterBorder());
			}
		});
		karpanTF_RP.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == ' ') {
					e.consume();
				}
			}
		});
		karpanTF_RP.setToolTipText("Enter Karpan Number");
		karpanTF_RP.setBorder(new InputManager().getDefaultOuterBorder());
		karpanTF_RP.setFont(new Font("Monospaced", Font.BOLD, 20));
		karpanTF_RP.setBounds(181, 242, 156, 26);
		ControlPanel_RP.add(karpanTF_RP);
		karpanTF_RP.setColumns(10);
		
		JLabel inputLabel_3_RP = new JLabel("Weight      :");
		inputLabel_3_RP.setVerifyInputWhenFocusTarget(false);
		inputLabel_3_RP.setFont(new Font("Monospaced", Font.BOLD, 20));
		inputLabel_3_RP.setFocusable(false);
		inputLabel_3_RP.setFocusTraversalKeysEnabled(false);
		inputLabel_3_RP.setBounds(15, 364, 156, 31);
		ControlPanel_RP.add(inputLabel_3_RP);
		
		weightTF_RP = new JTextField();
		weightTF_RP.setBorder(
				new CompoundBorder(
						(MatteBorder) new InputManager().getCustomMatteBorder(1, 1, 1, 0, BorderType.DEFAULT),
						(EmptyBorder) new InputManager().getInnerBorder()
				));
		weightTF_RP.setToolTipText("Enter Diamonf Weight in Carats");
		weightTF_RP.setFont(new Font("Monospaced", Font.BOLD, 20));
		weightTF_RP.setColumns(10);
		weightTF_RP.setBounds(181, 369, 115, 26);
		ControlPanel_RP.add(weightTF_RP);
		
		caratsLabel_RP = new JLabel("cts");
		caratsLabel_RP.setHorizontalAlignment(SwingConstants.CENTER);
		caratsLabel_RP.setBorder(
				new CompoundBorder(
						(MatteBorder) new InputManager().getCustomMatteBorder(1, 0, 1, 1, BorderType.DEFAULT),
						(EmptyBorder) new InputManager().getInnerBorder()
				));
		caratsLabel_RP.setFont(new Font("Monospaced", Font.BOLD, 20));
		caratsLabel_RP.setBounds(296, 369, 41, 26);
		ControlPanel_RP.add(caratsLabel_RP);
		
		weightTF_RP.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				weightTF_RP.setBorder(
						new CompoundBorder(
								(MatteBorder) new InputManager().getCustomMatteBorder(1, 1, 1, 0, BorderType.FOCUS),
								(EmptyBorder) new InputManager().getInnerBorder()
						));
				caratsLabel_RP.setBorder(
						new CompoundBorder(
								(MatteBorder) new InputManager().getCustomMatteBorder(1, 0, 1, 1, BorderType.FOCUS),
								(EmptyBorder) new InputManager().getInnerBorder()
						));
			}
			@Override
			public void focusLost(FocusEvent e) {
				weightTF_RP.setBorder(
						new CompoundBorder(
								(MatteBorder) new InputManager().getCustomMatteBorder(1, 1, 1, 0, BorderType.DEFAULT),
								(EmptyBorder) new InputManager().getInnerBorder()
						));
				caratsLabel_RP.setBorder(
						new CompoundBorder(
								(MatteBorder) new InputManager().getCustomMatteBorder(1, 0, 1, 1, BorderType.DEFAULT),
								(EmptyBorder) new InputManager().getInnerBorder()
						));
			}
		});
		
		JLabel inputLabel_4_RP = new JLabel("Is New ?");
		inputLabel_4_RP.setFont(new Font("Tahoma", Font.PLAIN, 15));
		inputLabel_4_RP.setBounds(347, 247, 58, 17);
		ControlPanel_RP.add(inputLabel_4_RP);
		
		karpanYesRB_RP = new JRadioButton("Yes");
		karpanYesRB_RP.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		karpanYesRB_RP.setBackground(Color.WHITE);
		karpanYesRB_RP.setFont(new Font("Tahoma", Font.PLAIN, 15));
		karpanYesRB_RP.setBounds(411, 244, 58, 23);
		ControlPanel_RP.add(karpanYesRB_RP);
		
		JRadioButton karpanNoRB_RP = new JRadioButton("No");
		karpanNoRB_RP.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		karpanNoRB_RP.setFont(new Font("Tahoma", Font.PLAIN, 15));
		karpanNoRB_RP.setBackground(Color.WHITE);
		karpanNoRB_RP.setBounds(471, 244, 58, 23);
		ControlPanel_RP.add(karpanNoRB_RP);
		
		ButtonGroup karpanButtonGroup = new ButtonGroup();
		karpanButtonGroup.add(karpanYesRB_RP);
		karpanButtonGroup.add(karpanNoRB_RP);
		karpanNoRB_RP.setSelected(true);
		
		karpanYesRB_RP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(karpanTF_RP.getText().equals("")) {
					karpanTF_RP.setBorder(new InputManager().getErrorOuterBorder());
					JOptionPane.showMessageDialog(frame, "Kindly Enter Karpan Number First. ", "Information Manager", JOptionPane.INFORMATION_MESSAGE);
					karpanNoRB_RP.setSelected(true);
					return;
				}
				if(filesManager.isFolderAvailable(karpanTF_RP.getText())) {
					System.out.println("Function called");
					JOptionPane.showMessageDialog(frame, "Folder with Karpan Number " + karpanTF_RP.getText() + " already Exists!", "Files Manager", JOptionPane.INFORMATION_MESSAGE);
					karpanNoRB_RP.setSelected(true);
				}
			}
		});
		
		JLabel inputLabel_5_RP = new JLabel("Is New ?");
		inputLabel_5_RP.setFont(new Font("Tahoma", Font.PLAIN, 15));
		inputLabel_5_RP.setBounds(347, 310, 58, 17);
		ControlPanel_RP.add(inputLabel_5_RP);
		
		dCodeYesRB_RP = new JRadioButton("Yes");
		dCodeYesRB_RP.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		dCodeYesRB_RP.setFont(new Font("Tahoma", Font.PLAIN, 15));
		dCodeYesRB_RP.setBackground(Color.WHITE);
		dCodeYesRB_RP.setBounds(411, 307, 58, 23);
		ControlPanel_RP.add(dCodeYesRB_RP);
		
		JRadioButton dCodeNoRB_RP = new JRadioButton("No");
		dCodeNoRB_RP.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		dCodeNoRB_RP.setSelected(true);
		dCodeNoRB_RP.setFont(new Font("Tahoma", Font.PLAIN, 15));
		dCodeNoRB_RP.setBackground(Color.WHITE);
		dCodeNoRB_RP.setBounds(471, 307, 58, 23);
		ControlPanel_RP.add(dCodeNoRB_RP);
		
		ButtonGroup dCodeButtonGroup = new ButtonGroup();
		dCodeButtonGroup.add(dCodeYesRB_RP);
		dCodeButtonGroup.add(dCodeNoRB_RP);
		dCodeNoRB_RP.setSelected(true);
		
		dCodeYesRB_RP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(dCodeTF_RP.getText().equals("")) {
					dCodeTF_RP.setBorder(new InputManager().getErrorOuterBorder());
					JOptionPane.showMessageDialog(frame, "Kindly Enter Diamond Number First. ", "Information Manager", JOptionPane.INFORMATION_MESSAGE);
					dCodeNoRB_RP.setSelected(true);
					return;
				}
				if(filesManager.isFolderAvailable(karpanTF_RP.getText())) {
					System.out.println("Function called");
					JOptionPane.showMessageDialog(frame, "Folder with Diamond Number " + dCodeTF_RP.getText() + " already Exists!", "Files Manager", JOptionPane.INFORMATION_MESSAGE);
					dCodeNoRB_RP.setSelected(true);
				}
			}
		});
		
		sl_recordingPanel.putConstraint(SpringLayout.WEST, ExtraPanel_RP, 10, SpringLayout.WEST, recordingPanel);
		sl_recordingPanel.putConstraint(SpringLayout.EAST, ExtraPanel_RP, -10, SpringLayout.EAST, recordingPanel);
		ExtraPanel_RP.setBackground(Color.WHITE);
		recordingPanel.add(ExtraPanel_RP);
		ExtraPanel_RP.setLayout(new BorderLayout(0, 0));
		
		JPanel headingPanel_2_RP = new JPanel();
		headingPanel_2_RP.setBorder(new MatteBorder(0, 0, 1, 0, (Color) new Color(255, 255, 255)));
		headingPanel_2_RP.setForeground(new Color(255, 255, 255));
		headingPanel_2_RP.setBackground(new Color(0, 0, 0));
		FlowLayout fl_headingPanel_2_RP = (FlowLayout) headingPanel_2_RP.getLayout();
		fl_headingPanel_2_RP.setAlignment(FlowLayout.LEFT);
		ExtraPanel_RP.add(headingPanel_2_RP, BorderLayout.NORTH);
		
		JLabel infoLabel_RP = new JLabel("Console:");
		infoLabel_RP.setForeground(new Color(255, 255, 255));
		infoLabel_RP.setFont(new Font("Tahoma", Font.PLAIN, 12));
		headingPanel_2_RP.add(infoLabel_RP);
		
		console_RP = new JTextArea();
		console_RP.setSelectionColor(new Color(192, 192, 192));
		console_RP.setSelectedTextColor(new Color(0, 0, 0));
		console_RP.setFont(new Font("Monospaced", Font.PLAIN, 15));
		console_RP.setForeground(new Color(127, 255, 0));
		console_RP.setBackground(new Color(0, 0, 0));
		console_RP.setLineWrap(true);
		console_RP.setEditable(false);
		JScrollPane consoleSP_RP = new JScrollPane(console_RP);
		consoleSP_RP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		ExtraPanel_RP.add(consoleSP_RP, BorderLayout.CENTER);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu viewMenu = new JMenu("View");
		menuBar.add(viewMenu);
		
		JMenuItem recordingViewMenuItem = new JMenuItem("Recording View");
		recordingViewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				masterPanel.removeAll();
				masterPanel.add(recordingPanel);
				masterPanel.repaint();
				masterPanel.revalidate();
			}
		});
		viewMenu.add(recordingViewMenuItem);
		
		SamplePanel sPanel = new SamplePanel();
		
		JMenuItem searchViewMenuItem = new JMenuItem("Search View");
		searchViewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				masterPanel.removeAll();
				masterPanel.add(sPanel);
				masterPanel.repaint();
				masterPanel.revalidate();
			}
		});
		viewMenu.add(searchViewMenuItem);
		
		JMenu propertiesMenu = new JMenu("Properties");
		menuBar.add(propertiesMenu);
		
		JMenuItem saveLocationMenuItem = new JMenuItem("Save Location");
		saveLocationMenuItem.addActionListener( e -> new SampleDialog(frame, configManager).setVisible(true));
		propertiesMenu.add(saveLocationMenuItem);
		
		comPortMenu = new JMenu("COM PORTS");
		comPortMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!isESP32) {
					esp32 = new ESP32();
					isESP32 = true;
				}
				getPortList();
			}
		});
		propertiesMenu.add(comPortMenu);
		
		cameraMenu = new JMenu("Webcams");
		cameraMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				getCams();
			}
		});
		propertiesMenu.add(cameraMenu);
	}
	
	private void actionButton() {
		if(camIndex == 10001) {
			connStatusTF_RP.setText("Failed");
			consoleLog("Kindly select the Webcam.");
			return;
		}
		
		if(isCamSet && isPortSet) {
			connStatusTF_RP.setText("Success");
		}else {
			connStatusTF_RP.setText("Partial");
		}
		
		if(!isLive) {
			cameraCapture = new CameraCapture(camIndex, 60);
			boolean check = cameraCapture.initializeCamera();
			if(!check) {
				return;
			}
			System.out.println("Camera capture Up and active....");
			isLive = true;
			startFeedBtn_RP.setText("Stop Live Feed");
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
			startFeedBtn_RP.setText("Start Live Feed");
			connStatusTF_RP.setText("");
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
			if(selectedPortIndex == 20001) {
				consoleLog("Kindly select the COM PORT.");
				JOptionPane.showMessageDialog(frame, "Connect connect to the System.", "Connection Manager", JOptionPane.ERROR_MESSAGE);
				return;
			}
			System.out.println("Starting Recording....");
			if(karpanTF_RP.getText().equals("")) {
				karpanTF_RP.setBorder(new InputManager().getErrorOuterBorder());
				JOptionPane.showMessageDialog(frame, "Karpan number cannot be Empty!", "Input Manager", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			if(dCodeTF_RP.getText().equals("")) {
				dCodeTF_RP.setBorder(new InputManager().getErrorOuterBorder());
				JOptionPane.showMessageDialog(frame, "Diamond number cannot be Empty!", "Input Manager", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			if(weightTF_RP.getText().equals("")) {
				weightTF_RP.setBorder(
						new CompoundBorder(
								(MatteBorder) new InputManager().getCustomMatteBorder(1, 1, 1, 0, BorderType.ERROR),
								(EmptyBorder) new InputManager().getInnerBorder()
						));
				caratsLabel_RP.setBorder(
						new CompoundBorder(
								(MatteBorder) new InputManager().getCustomMatteBorder(1, 0, 1, 1, BorderType.ERROR),
								(EmptyBorder) new InputManager().getInnerBorder()
						));
				JOptionPane.showMessageDialog(frame, "Weight of Diamond cannot be Empty!", "Input Manager", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			saveToPathOfVideo = filesManager.createPath(karpanTF_RP.getText(), dCodeTF_RP.getText(), karpanYesRB_RP.isSelected(), dCodeYesRB_RP.isSelected());
			
			File testFile = new File(saveToPathOfVideo + File.separator + weightTF_RP.getText() +" cts.avi");
			if(testFile.exists()) {
				weightTF_RP.setBorder(
						new CompoundBorder(
								(MatteBorder) new InputManager().getCustomMatteBorder(1, 1, 1, 0, BorderType.ERROR),
								(EmptyBorder) new InputManager().getInnerBorder()
						));
				caratsLabel_RP.setBorder(
						new CompoundBorder(
								(MatteBorder) new InputManager().getCustomMatteBorder(1, 0, 1, 1, BorderType.ERROR),
								(EmptyBorder) new InputManager().getInnerBorder()
						));
				JOptionPane.showMessageDialog(frame, "Video of diamond with same weigth already Exist!", "Input Manager", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			RecordingTask recordingTask = new RecordingTask();
			isRecording = true;
			recordingFuture = service.submit(recordingTask);
			consoleLog("Recording Started....");
			recordingBtn_RP.setText("Recording...");
			recordingBtn_RP.setEnabled(false);
			ESP32Con();
		}else {
			consoleLog("Kindly set the connection, in order to capture the video.");
			JOptionPane.showMessageDialog(frame, "No Connection Found!", "Connection Manager", JOptionPane.ERROR_MESSAGE);
		}
	}
	private void startRecording() {
		if(cameraCapture.isCaptureOpen()) {
			if(cameraCapture.recordFrames(saveToPathOfVideo, weightTF_RP.getText())) {
				System.out.println(recordingFuture.cancel(true));
				System.out.println("========= Recording Performed Successfully ============");
				VideoConvertingTask videoConvertingTask = new VideoConvertingTask();
				videoConvertingFuture = service.submit(videoConvertingTask);
				recordingBtn_RP.setText("Converting Video");
			}
		}
	}
	
	private void getPortList() {
		LinkedList<String> portNameList = esp32.getPortNameList();
		if(portNameList == null) {
			JOptionPane.showMessageDialog(frame, "No Com Ports Available.","COM PORTS", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(!portNameList.isEmpty()) {
			portList = null;
			portList = new JMenuItem[portNameList.size()];
			comPortMenu.removeAll();
			int az;
			for(az=0; az<portNameList.size(); az++) {
				portList[az] = new JMenuItem(portNameList.get(az));
				int aq = az;
				portList[az].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.out.println(portNameList.get(aq)+ " is selected");
						selectedPortIndex = aq;
						portStatusTF_RP.setText(portNameList.get(aq));
						consoleLog("Com Port : "+ portNameList.get(aq) + " is SELECTED.");
						isPortSet = true;
					}
				});
				comPortMenu.add(portList[az]);
			}
		}
	}
	
	
	private void getCams() {
		cameraMenu.removeAll();
		List<Webcam> webcams = Webcam.getWebcams();
		if(webcams == null) {
			JOptionPane.showMessageDialog(frame, "No Webcams Available.", "Web Camera", JOptionPane.ERROR_MESSAGE);
		}
		
		for(int qq = 0; qq < webcams.size(); qq ++) {
			Webcam webcam = webcams.get(qq);
			JMenuItem webcamItem = new JMenuItem(webcam.getName());
			
			int index = qq;
			webcamItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					camIndex = index;
					cameraStatusTF_RP.setText(webcam.getName());
					System.out.println(webcam.getName() +" is selected.");
					consoleLog("Webcam : " + webcam.getName() + " is SELECTED.");
					isCamSet = true;
				}
			});
			cameraMenu.add(webcamItem);
		}
	}
	
	private void consoleLog(String text) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		console_RP.append(timestamp + " -> " + text + "\n");
	}
	
	private void ESP32Con() {
		System.out.println("ESPcon called");
		esp32.sendMessage(selectedPortIndex);
	}
	
	private void avi4mp4() {
		VideoFormatConvert converter = new VideoFormatConvert(console_RP);
		if(converter != null) {
			if(converter.convertingMethod(saveToPathOfVideo, weightTF_RP.getText())) {
				consoleLog("Video Converted Successfully.");
				videoConvertingFuture.cancel(true);
			}else {
				consoleLog("Failed to convert the Video.");
				videoConvertingFuture.cancel(true);
			}
			isRecording = false;
			recordingBtn_RP.setText("Start Recording");
			recordingBtn_RP.setEnabled(true);
		}
	}
	
	private void getResourcesReleased() {
		if(cameraCapture != null) {
			cameraCapture.releaseResources();
		}
		service.shutdownNow();
	}
	
	private void setApplicationDataSession() {
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
					consoleLog("Video saved at path : " + cameraCapture.getVideoPath());
					throw new InterruptedException("Thread interrupted");
				}
			}
		}
	}
	
	private class VideoConvertingTask implements Callable<Void>{

		@Override
		public Void call() throws Exception {
			while(true) {
				avi4mp4();
				if(Thread.currentThread().isInterrupted()) {
					consoleLog("Converted Video Saved to same path.");
					throw new InterruptedException("Thread interrupted");
				}
			}
		}
		
	}
}
