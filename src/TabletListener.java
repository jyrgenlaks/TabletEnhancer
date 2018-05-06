import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

interface TabletEvent {
	void stylusDetected(int x, int y);
	void newPositionDetected(int x, int y);
}

interface KeyEvent {
	void F12Pressed();
}

public class TabletListener implements NativeMouseInputListener, NativeKeyListener {

	private List<TabletEvent> stylusListeners = new ArrayList<>();
	private List<KeyEvent> keyListeners = new ArrayList<>();

	private int lastX = 0, lastY = 0;
	private int lastTriggeredX = 0, lastTriggeredY = 0;
	private int lastTriggeredX2 = 0, lastTriggeredY2 = 0;

	void addStylusListener(TabletEvent listener) {
		stylusListeners.add(listener);
	}

	void addKeyListener(KeyEvent listener) {
		keyListeners.add(listener);
	}

	void registerListener() {
		//disable JNativeHook logger spam
		LogManager.getLogManager().reset();
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);

		try {
			GlobalScreen.registerNativeHook();
			GlobalScreen.addNativeMouseMotionListener(this);
			GlobalScreen.addNativeKeyListener(this);
		} catch (NativeHookException ex) {
			ex.printStackTrace();
		}
	}

	void unregisterListener() {
		try {
			GlobalScreen.removeNativeKeyListener(this);
			GlobalScreen.removeNativeMouseMotionListener(this);
			GlobalScreen.unregisterNativeHook();
		} catch (NativeHookException ex) {
			ex.printStackTrace();
		}
	}


	@Override
	public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {

	}

	@Override
	public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {

	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {

	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {
		somethingMovedEvent(nativeMouseEvent);
	}

	@Override
	public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {
		somethingMovedEvent(nativeMouseEvent);
	}

	private void somethingMovedEvent(NativeMouseEvent nativeMouseEvent){
		int x = nativeMouseEvent.getX();
		int y = nativeMouseEvent.getY();

		if (x == lastX && y == lastY) {
			stylusDetected(x, y);
		}else{
			newPositionDetected(x, y);
		}

		lastX = x;
		lastY = y;
	}

	private void stylusDetected(int x, int y) {
		if (lastTriggeredX != x || lastTriggeredY != y) {
			//see on uue positsiooniga event
			lastTriggeredX = x;
			lastTriggeredY = y;
			for (TabletEvent listener : stylusListeners) {
				listener.stylusDetected(x, y);
			}
		}
	}

	private void newPositionDetected(int x, int y) {
		if (lastTriggeredX2 != x || lastTriggeredY2 != y) {
			lastTriggeredX2 = x;
			lastTriggeredY2 = y;
			for (TabletEvent listener : stylusListeners) {
				listener.newPositionDetected(x, y);
			}
		}
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {

	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
		if(nativeKeyEvent.getKeyCode() == 88){
			for (KeyEvent listener : keyListeners) {
				listener.F12Pressed();
			}
		}
	}
}
