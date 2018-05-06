import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.IOException;

public class Marker extends JDialog{

	private float alpha = 0;
	private int size = 200;
	private boolean running = true;

	Marker(){
		setUndecorated(true);
		getRootPane().setOpaque(false);
		getContentPane ().setBackground (new Color (0, 0, 0, 0));
		setBackground (new Color (0, 0, 0, 0));

		getContentPane().add(new MarkerPanel(size), java.awt.BorderLayout.CENTER);

		setSize(size, size);
		setLocationRelativeTo(null);
		setVisible(true);
		setAlwaysOnTop(true);

		new Thread(){
			@Override
			public void run() {
				super.run();
				while(running){
					try {
						sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(alpha > 0){
						setVisible(true);
						repaint();
						alpha -= 0.01;
					}

					if(alpha <= 0){
						alpha = 0;
						setVisible(false);
					}
				}
			}
		}.start();
	}

	void updateMarkerLocation(int x, int y){
		if(alpha != 0) {
			setLocation(x - size / 2, y - size / 2);
		}
	}

	void setMarker(int x, int y){
		setLocation(x - size/2, y - size/2);
		alpha = 1;
	}

	public void close() {
		running = false;
	}

	public class MarkerPanel extends JPanel {
		private int MARKER_WIDTH;
		private int MARKER_HEIGHT;
		private Image markerImage;

		MarkerPanel(int size){
			MARKER_WIDTH = MARKER_HEIGHT = size;
			setBackground (new Color (0, 0, 0, 0));
			try {
				markerImage = ImageIO.read(new File("assets/marker.png"));
				markerImage = markerImage.getScaledInstance(MARKER_WIDTH, MARKER_HEIGHT, Image.SCALE_DEFAULT);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void paintComponent( Graphics g ) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D)g;
			int rule = AlphaComposite.SRC_OVER;
			Composite comp = AlphaComposite.getInstance(rule, alpha);
			g2.setComposite(comp );
			g2.drawImage(markerImage, 0, 0, this);
		}
	}
}