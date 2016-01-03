package shivshank.engine.input;

public class MouseInfo {
	private final boolean[] buttons;

	private double xPos;
	private double yPos;
	private double scrollX;
	private double scrollY;
	private boolean focused = false;

	public MouseInfo(int buttons) {
		this.buttons = new boolean[buttons];
	}

	public double getPosX() {
		return xPos;
	}

	public double getPosY() {
		return yPos;
	}

	public double getScrollX() {
		return scrollX;
	}

	public double getScrollY() {
		return scrollY;
	}

	/**
	 * Gets whether or not the mouse is focused on the window.
	 * <p>
	 * Note that this is different from the window being visible/focused. This
	 * only tells you whether or not the mouse is over the screen.
	 * 
	 * @return
	 */
	public boolean isFocused() {
		return focused;
	}

	/**
	 * Get the state of the button
	 * 
	 * @param glfw_mouse_button
	 * @return
	 */
	public boolean getButton(int glfw_mouse_button) {
		return buttons[glfw_mouse_button];
	}

	public boolean wasPressed(int glfw_mouse_button) {
		return buttons[glfw_mouse_button] == true;
	}

	void pressButton(int glfw_mouse_button) {
		buttons[glfw_mouse_button] = true;
	}

	void releaseButton(int glfw_mouse_button) {
		buttons[glfw_mouse_button] = false;
	}
	
	void setFocus(boolean focus) {
		focused = focus;
	}
	
	void setPos(double x, double y) {
		xPos = x;
		yPos = y;
	}
	
	void clear() {
		// Scroll values are relative so they should be cleared, unlike everything else
		scrollX = 0;
		scrollY = 0;
	}

	public void addScroll(double xOffset, double yOffset) {
		scrollX += xOffset;
		scrollY += yOffset;
	}
}
