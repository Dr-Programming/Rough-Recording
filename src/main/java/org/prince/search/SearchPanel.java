// This sample panel is currently being developed.
// And this is a Stable version which is used in the application.
// 1500 x 988

package org.prince.search;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.sql.Timestamp;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.prince.configuration.ConfigManager;
import org.prince.inputs.InputManager;

public class SearchPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JLabel videoLabel_SP;
	
	private JScrollPane fileTable_sp_SP;
	
	private JTable filesTable_SP;
	
	private JTextArea console_SP;
	
	private JTextField karpanTF_SP;
	private JTextField dCodeTF_SP;
	private JTextField infoTF_1_SP;

	private DefaultTableModel tableModel;
	
	private String directoryPath;
	private String videoPath;
	
	private volatile boolean isPlaying = false;
	private volatile boolean forceStop = false;
	
	private BufferedImage image;
	
	private ExecutorService service = Executors.newCachedThreadPool();
	
	private Future<?> videoFuture;

	private ConfigManager configManager;

	private VideoCapture cap;
	
	

	/**
	 * Create the panel.
	 */
	public SearchPanel(ConfigManager configManager) {
		nu.pattern.OpenCV.loadLocally();
		this.configManager = configManager;
		setBackground(SystemColor.control);
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		JPanel detailsPanel_SP = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, detailsPanel_SP, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, detailsPanel_SP, -699, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.EAST, detailsPanel_SP, -10, SpringLayout.EAST, this);
		detailsPanel_SP.setBackground(Color.WHITE);
		add(detailsPanel_SP);
		
		JPanel searchViewPanel_SP = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, searchViewPanel_SP, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, searchViewPanel_SP, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, searchViewPanel_SP, 0, SpringLayout.SOUTH, detailsPanel_SP);
		springLayout.putConstraint(SpringLayout.EAST, searchViewPanel_SP, -6, SpringLayout.WEST, detailsPanel_SP);
		searchViewPanel_SP.setBackground(Color.WHITE);
		add(searchViewPanel_SP);
		
		JPanel extraPanel_SP = new JPanel();
		springLayout.putConstraint(SpringLayout.SOUTH, detailsPanel_SP, -6, SpringLayout.NORTH, extraPanel_SP);
		springLayout.putConstraint(SpringLayout.NORTH, extraPanel_SP, -209, SpringLayout.SOUTH, this);
		detailsPanel_SP.setLayout(null);
		
		JLabel headingLabel_2_SP = new JLabel("Diamond Details :");
		headingLabel_2_SP.setFont(new Font("Dubai", Font.BOLD, 26));
		headingLabel_2_SP.setBounds(22, 19, 217, 29);
		detailsPanel_SP.add(headingLabel_2_SP);
		
		JLabel inputLabel_1_SP = new JLabel("Karpan No.  :");
		inputLabel_1_SP.setVerifyInputWhenFocusTarget(false);
		inputLabel_1_SP.setFont(new Font("Monospaced", Font.BOLD, 20));
		inputLabel_1_SP.setFocusable(false);
		inputLabel_1_SP.setFocusTraversalKeysEnabled(false);
		inputLabel_1_SP.setBounds(22, 71, 156, 31);
		detailsPanel_SP.add(inputLabel_1_SP);
		
		karpanTF_SP = new JTextField();
		karpanTF_SP.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				karpanTF_SP.setBorder(new InputManager().getFocusOuterBorder());
			}
			@Override
			public void focusLost(FocusEvent e) {
				karpanTF_SP.setBorder(new InputManager().getDefaultOuterBorder());
			}
		});
		karpanTF_SP.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if(!Character.isDigit(c) && c != '.' ) {
					e.consume();
				}
				if( c != KeyEvent.VK_BACK_SPACE && c ==' ') {
					e.consume();
				}
				if(c == '.' && karpanTF_SP.getText().contains(".")) {
					e.consume();
				}
			}
		});
		karpanTF_SP.setToolTipText("Enter Karpan Number");
		karpanTF_SP.setFont(new Font("Monospaced", Font.BOLD, 20));
		karpanTF_SP.setColumns(10);
		karpanTF_SP.setBorder(new InputManager().getDefaultOuterBorder());
		karpanTF_SP.setBounds(188, 75, 156, 26);
		detailsPanel_SP.add(karpanTF_SP);
		
		JLabel inputLabel_2_SP = new JLabel("Diamond No. :");
		inputLabel_2_SP.setFont(new Font("Monospaced", Font.BOLD, 20));
		inputLabel_2_SP.setBounds(22, 130, 156, 26);
		detailsPanel_SP.add(inputLabel_2_SP);
		
		dCodeTF_SP = new JTextField();
		dCodeTF_SP.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				dCodeTF_SP.setBorder(new InputManager().getFocusOuterBorder());
			}
			@Override
			public void focusLost(FocusEvent e) {
				dCodeTF_SP.setBorder(new InputManager().getDefaultOuterBorder());
			}
		});
		dCodeTF_SP.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if(!Character.isDigit(c) && c != '.' ) {
					e.consume();
				}
				if( c != KeyEvent.VK_BACK_SPACE && c ==' ') {
					e.consume();
				}
				if(c == '.' && karpanTF_SP.getText().contains(".")) {
					e.consume();
				}
			}
		});
		dCodeTF_SP.setToolTipText("Enter Diamond Number");
		dCodeTF_SP.setFont(new Font("Monospaced", Font.BOLD, 20));
		dCodeTF_SP.setColumns(10);
		dCodeTF_SP.setBorder(new InputManager().getDefaultOuterBorder());
		dCodeTF_SP.setBounds(188, 132, 156, 26);
		detailsPanel_SP.add(dCodeTF_SP);
		
		JSeparator separator_SP = new JSeparator();
		separator_SP.setBounds(0, 248, 689, 2);
		detailsPanel_SP.add(separator_SP);
		
		JLabel infoLabel_1_SP = new JLabel("Search Results :");
		infoLabel_1_SP.setFont(new Font("Tahoma", Font.PLAIN, 14));
		infoLabel_1_SP.setBounds(22, 265, 107, 17);
		detailsPanel_SP.add(infoLabel_1_SP);
		
		infoTF_1_SP = new JTextField();
		infoTF_1_SP.setBorder(null);
		infoTF_1_SP.setBackground(Color.WHITE);
		infoTF_1_SP.setEditable(false);
		infoTF_1_SP.setFont(new Font("Tahoma", Font.PLAIN, 15));
		infoTF_1_SP.setBounds(139, 265, 290, 20);
		detailsPanel_SP.add(infoTF_1_SP);
		infoTF_1_SP.setColumns(10);
		
		JPanel tablePanel_SP = new JPanel();
		tablePanel_SP.setBounds(32, 311, 629, 423);
		detailsPanel_SP.add(tablePanel_SP);
		tablePanel_SP.setLayout(new BorderLayout(0, 0));
		springLayout.putConstraint(SpringLayout.WEST, extraPanel_SP, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, extraPanel_SP, -10, SpringLayout.EAST, this);
		searchViewPanel_SP.setLayout(new BorderLayout(0, 0));
		
		JPanel headingPanel_1_SP = new JPanel();
		headingPanel_1_SP.setBackground(Color.WHITE);
		searchViewPanel_SP.add(headingPanel_1_SP, BorderLayout.NORTH);
		
		JLabel headingLabel_1_SP = new JLabel("Search View");
		headingLabel_1_SP.setFont(new Font("Tahoma", Font.BOLD, 30));
		headingPanel_1_SP.add(headingLabel_1_SP);
		
		JPanel videoPanel_SP = new JPanel();
		videoPanel_SP.setBackground(Color.WHITE);
		searchViewPanel_SP.add(videoPanel_SP, BorderLayout.CENTER);
		videoPanel_SP.setLayout(new BorderLayout(0, 0));
		
		videoLabel_SP = new JLabel("Kindly Select the Video");
		videoLabel_SP.setHorizontalAlignment(SwingConstants.CENTER);
		videoPanel_SP.add(videoLabel_SP, BorderLayout.CENTER);
		extraPanel_SP.setBackground(Color.WHITE);
		springLayout.putConstraint(SpringLayout.SOUTH, extraPanel_SP, -10, SpringLayout.SOUTH, this);
		add(extraPanel_SP);
		extraPanel_SP.setLayout(new BorderLayout(0, 0));
		
		JPanel headingPanel_2_SP = new JPanel();
		headingPanel_2_SP.setBorder(new MatteBorder(0, 0, 1, 0, (Color) Color.WHITE));
		headingPanel_2_SP.setBackground(Color.BLACK);
		extraPanel_SP.add(headingPanel_2_SP, BorderLayout.NORTH);
		headingPanel_2_SP.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JLabel infoLabel_2_SP = new JLabel(" Console:");
		infoLabel_2_SP.setFont(new Font("Tahoma", Font.PLAIN, 12));
		infoLabel_2_SP.setForeground(Color.WHITE);
		headingPanel_2_SP.add(infoLabel_2_SP);
		
		console_SP = new JTextArea();
		console_SP.setSelectionColor(new Color(192, 192, 192));
		console_SP.setSelectedTextColor(new Color(0, 0, 0));
		console_SP.setFont(new Font("Monospaced", Font.PLAIN, 15));
		console_SP.setForeground(new Color(127, 255, 0));
		console_SP.setBackground(new Color(0, 0, 0));
		console_SP.setLineWrap(true);
		console_SP.setEditable(false);
		JScrollPane consoleSp_SP = new JScrollPane(console_SP);
		consoleSp_SP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		extraPanel_SP.add(consoleSp_SP, BorderLayout.CENTER);
		
		tableModel = new DefaultTableModel(new Object[] {"File Name"}, 0) {
			
			private static final long serialVersionUID = 484862087686999747L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		filesTable_SP = new JTable(tableModel);
		filesTable_SP.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		filesTable_SP.setFont(new Font("Arial", Font.PLAIN, 15));
		filesTable_SP.setRowHeight(25);
		filesTable_SP.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					int row = filesTable_SP.getSelectedRow();
					if(row != -1) {
						String fileName = (String) tableModel.getValueAt(row, 0);
						JOptionPane.showMessageDialog(SearchPanel.this, "File: "+ fileName + " is SELECTED.");
						playVideo(fileName);
					}
				}
			}
		});
		
		JTableHeader header = filesTable_SP.getTableHeader();
		header.setFont(new Font("Arial", Font.BOLD, 18));
		header.setReorderingAllowed(false);
		
		fileTable_sp_SP = new JScrollPane(filesTable_SP);
		tablePanel_SP.add(fileTable_sp_SP, BorderLayout.CENTER);
		
		JButton getVideosBtn_SP = new JButton("Get Videos");
		getVideosBtn_SP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fetchFiles();
			}
		});
		getVideosBtn_SP.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		getVideosBtn_SP.setFont(new Font("Monospaced", Font.BOLD, 16));
		getVideosBtn_SP.setBounds(188, 187, 156, 31);
		detailsPanel_SP.add(getVideosBtn_SP);
		
		ImageIcon icon = new ImageIcon(getClass().getResource("/images/BlueLogo B.png"));
		
		JLabel logoLabel_SP = new JLabel("");
		logoLabel_SP.setBounds(517, 11, 150, 150);
		logoLabel_SP.setIcon(icon);
		detailsPanel_SP.add(logoLabel_SP);
	}
	
	private void fetchFiles() {
		
		if(karpanTF_SP.getText().equals("")) {
			karpanTF_SP.setBorder(new InputManager().getErrorOuterBorder());
			JOptionPane.showMessageDialog(this, "Kindly Enter Karpan Number. ", "Information Manager", JOptionPane.INFORMATION_MESSAGE);
			consoleLog("Enter complete information.");
			return;
		}
		if(dCodeTF_SP.getText().equals("")) {
			dCodeTF_SP.setBorder(new InputManager().getErrorOuterBorder());
			JOptionPane.showMessageDialog(this, "Kindly Enter Diamond Number. ", "Information Manager", JOptionPane.INFORMATION_MESSAGE);
			consoleLog("Enter complete information.");
			return;
		}
		
		directoryPath = configManager.getProperty("savePath") + karpanTF_SP.getText() + File.separator + dCodeTF_SP.getText();
		consoleLog("Fetching files for Article "+karpanTF_SP.getText() + "-" + dCodeTF_SP.getText()+".");
		File folder = new File(directoryPath);
		if(folder.isDirectory()) {
			tableModel.setRowCount(0);
			
			File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp4"));
			if(files != null) {
				infoTF_1_SP.setText(files.length + " Files Found.");
				for(File file : files) {
					consoleLog("File found.");
					tableModel.addRow(new Object[] {file.getName()});
				}
			}else {
				infoTF_1_SP.setText("No Files Found.");
				consoleLog("No File Found.");
			}
			consoleLog("Fetch Complete.");
		}else {
			JOptionPane.showMessageDialog(this, "Invalid input for Karpan No. "+ karpanTF_SP.getText()+" and Diamond No. "+dCodeTF_SP.getText()+".\n\nThat is Article Number: "+
					karpanTF_SP.getText()+"-"+dCodeTF_SP.getText()+" is Not Found!", "Information Manager", JOptionPane.INFORMATION_MESSAGE);
			consoleLog("Fetch Cannot be Completed.");
		}
	}
	
	private void playVideo(String fileName) {
		if(isPlaying) {
			forceStop = true;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		videoPath = directoryPath + File.separator + fileName;
		cap = new VideoCapture(videoPath);
		VideoTask videoTask = new VideoTask();
		videoFuture = service.submit(videoTask);
		isPlaying = true;
		System.out.println("future started at " + fileName);
		consoleLog("Playing " + fileName + " File.");
	}
	
	private void loadVideo() {
		if (!cap.isOpened()) {
            JOptionPane.showMessageDialog(this, "Error opening video file. Please check the file path.", "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("Error opening video file");
            consoleLog("Error Opening the above File.");
            return;
        }
		
		Mat frame = new Mat();
		cap.open(videoPath);
		 while (cap.read(frame)) {
			 if(forceStop) {
				 System.out.println("ForceFully canceling the future at path: "+ videoPath);
				 isPlaying = false;
				 forceStop = false;
				 videoFuture.cancel(true);
				 cap.release();
				 return;
			 }
             int labelWidth = videoLabel_SP.getWidth();
             int labelHeight = videoLabel_SP.getHeight();
             image = matToBufferedImage(frame);
             double aspectRatio = (double) image.getWidth() / image.getHeight();
             
             int newWidth = labelWidth;
             int newHeight = (int) (labelWidth / aspectRatio);
             if (newHeight > labelHeight) {
                 newHeight = labelHeight;
                 newWidth = (int) (labelHeight * aspectRatio);
             }
             
             if (image != null) {
            	 Image sImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            	 videoLabel_SP.setText("");
                 videoLabel_SP.setIcon(new ImageIcon(sImage));
             }
         }
		 System.out.println("stopping the future at video Path : "+ videoPath);
		 videoFuture.cancel(true);
		 isPlaying = false;
		 cap.release();
	}
	
	private BufferedImage matToBufferedImage(Mat mat) {
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
	
	public String releaseResources() {
		if(videoFuture != null) {
			videoFuture.cancel(true);
		}
		if(cap != null && cap.isOpened()){
			cap.release();
			cap = null;
		}
		tableModel.setRowCount(0);
		karpanTF_SP.setText("");
		dCodeTF_SP.setText("");
		infoTF_1_SP.setText("");
		videoLabel_SP.setIcon(null);
		videoLabel_SP.setText("Kindly Select the Video.");
		consoleLog("Search resources released.");
		String data = console_SP.getText();
		console_SP.setText("");
		return data;
	}
	
	public void setConsoleData(String data) {
		console_SP.setText(data);
	}
	
	private void consoleLog(String text) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		console_SP.append(timestamp + " -> " + text + "\n");
	}
	
	private class VideoTask implements Callable<Void>{

		@Override
		public Void call() throws Exception {
			while(true) {
				loadVideo();
				if(Thread.currentThread().isInterrupted()) {
					System.out.println("interrput is called");
					throw new InterruptedException("Thread interrupted");
				}
			}
		}
		
	}
}
