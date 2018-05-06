import javax.swing.*;
import java.awt.*;

public class TabletEnhancerMain implements TabletEvent, KeyEvent {

	/** If the stylus was moved to a new location that is farther form the last location than the specified amount, it will be marked */
	private static final int DETECTION_RADIUS_IN_PIXELS = 200;
	/** If the stylus hasn't been used for the specified amount, it will be marked */
	private static final int STYLUS_MARKING_TIME_THRESHOLD_IN_MILLISECONDS = 5000;

	private Point lastStylusLocations[] = new Point[10];
	private long lastTimeStylusWasSeen = 0;
	private Marker marker;
	private TabletListener tabletListener;

	public static void main(String[] args) {
		new TabletEnhancerMain();
	}

	private TabletEnhancerMain(){
		marker = new Marker();

		for(int i = 0; i < lastStylusLocations.length; i++){
			lastStylusLocations[i] = new Point(0, 0);
		}

		tabletListener = new TabletListener();
		tabletListener.registerListener();
		tabletListener.addStylusListener(this);
		tabletListener.addKeyListener(this);

		//To show that the program has actually started successfully
		Point mouse = MouseInfo.getPointerInfo().getLocation();
		marker.setMarker(mouse.x, mouse.y);

	}

	@Override
	public void stylusDetected(int x, int y) {
		long time = System.currentTimeMillis();

		int index = lastStylusLocations.length-1;
		boolean hasTheStylusBeenInactive = time - lastTimeStylusWasSeen > STYLUS_MARKING_TIME_THRESHOLD_IN_MILLISECONDS;
		boolean hasTheStylusMovedALot = (lastStylusLocations[index].x - x)*(lastStylusLocations[index].x - x) +
										(lastStylusLocations[index].y - y)*(lastStylusLocations[index].y - y) >
										DETECTION_RADIUS_IN_PIXELS * DETECTION_RADIUS_IN_PIXELS;
		if(hasTheStylusBeenInactive || hasTheStylusMovedALot){
			marker.setMarker(x, y);
		}
		lastTimeStylusWasSeen = time;
	}

	@Override
	public void newPositionDetected(int x, int y) {
		System.arraycopy(lastStylusLocations, 0, lastStylusLocations, 1, lastStylusLocations.length - 1);
		lastStylusLocations[0] = new Point(x, y);
		marker.updateMarkerLocation(x, y);
	}

	@Override
	public void F12Pressed() {
		tabletListener.unregisterListener();
		marker.close();
		System.runFinalization();
		JOptionPane.showMessageDialog(null, "Tablet Enhancer has been stopped!", "Tablet Enhancer", JOptionPane.INFORMATION_MESSAGE);
		System.exit(0);
	}
}
