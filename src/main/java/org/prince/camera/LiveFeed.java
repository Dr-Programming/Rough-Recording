package org.prince.camera;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

class LiveFeed {

	private Mat frame;
	
	public LiveFeed() {
		nu.pattern.OpenCV.loadLocally();
	}
	
	protected void startFeed(VideoCapture videoCapture, JLabel videolabel) {
		while(videoCapture.isOpened()) {
			videoCapture.read(frame);
			if(!frame.empty()) {
				Image image = matToBufferedImage(frame);
				videolabel.setIcon(new ImageIcon(image));
			}
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
}
