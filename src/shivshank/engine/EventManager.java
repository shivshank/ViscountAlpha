package shivshank.engine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import shivshank.engine.events.Event;
import shivshank.engine.events.InputEvent;
import shivshank.engine.events.ServerAction;
import shivshank.engine.events.WindowAction;
import shivshank.engine.input.InputController;
import shivshank.engine.network.Server;

public class EventManager {

	private long currentTime;
	private long previousTime;

	public final double deltaTime;
	/**
	 * The maximum amount of time the simulation is allowed to see between step
	 * calls.
	 */
	private final double elapsedTimeCap;
	private double remainingTime;
	private double alpha;

	private Server server;
	private InputController userInput;
	private Window window;

	private LinkedList<GameState> gsStack;

	/**
	 * Creates an EventManager to house the logic framework of the engine.
	 * <p>
	 * Receives events from the external frameWork and then makes those events
	 * available for processing to the current GameState.
	 * 
	 * @param initial Initial GameState
	 * @param updateFrequency the delta time, or time between game logic
	 *            updates.
	 */
	public EventManager(GameState initial, double updateFrequency) {
		gsStack = new LinkedList<GameState>();
		this.pauseState(initial);

		currentTime = System.nanoTime();
		previousTime = currentTime;
		remainingTime = 0;
		deltaTime = updateFrequency;
		if (deltaTime == 0) {
			throw new IllegalArgumentException("Cannot have a 0 Delta Time.");
		}
		elapsedTimeCap = 0.25;
	}

	/**
	 * Convenience method if there is no server.
	 * 
	 * @see link
	 * @param window
	 * @param userInput
	 */
	public void link(Window window, InputController userInput) {
		link(window, userInput, null);
	}

	/**
	 * Link this EventManager to EventSources.
	 * <p>
	 * EventSources are used to receive Events from external components, like
	 * the windowing system and the keyboard.
	 * 
	 * @param windowSrc
	 * @param userInputSrc
	 * @param serverSrc
	 */
	public void link(Window windowSrc, InputController userInputSrc,
			Server serverSrc) {
		server = serverSrc;
		window = windowSrc;
		userInput = userInputSrc;
	}

	public boolean isCurrent(GameState query) {
		return query == gsStack.peekFirst();
	}

	public void switchState(GameState newState) {
		if (newState == null)
			throw new IllegalArgumentException("New GameState cannot be null.");

		GameState oldState = null;
		if (!gsStack.isEmpty()) {
			oldState = gsStack.pop();
			oldState.destroy(this, newState);
		}

		newState.create(this, oldState);
		gsStack.push(newState);
	}

	public void pauseState(GameState newState) {
		if (newState == null)
			throw new IllegalArgumentException("New GameState cannot be null.");

		if (!gsStack.isEmpty())
			gsStack.peekFirst().pause(this, newState);

		newState.create(this, gsStack.peekFirst());
		gsStack.push(newState);
	}

	public void exitState() {
		if (gsStack.isEmpty())
			return;

		GameState oldState = gsStack.pop();
		if (!gsStack.isEmpty()) {
			oldState.destroy(this, gsStack.peekFirst());
			gsStack.peekFirst().resume(this, oldState);
		} else {
			oldState.destroy(this, null);
		}
	}

	/**
	 * <p>
	 * Get the updateFrequency as set by the constructor.
	 * </p>
	 * 
	 * <p>
	 * Update frequency, or delta time, is the time interval for logic updates.
	 * </p>
	 * 
	 * @return
	 */
	public double getUpdateFrequency() {
		return deltaTime;
	}

	/**
	 * <p>
	 * Steps the EventManager.
	 * </p>
	 * 
	 * <p>
	 * Every step causes a render.
	 * </p>
	 * 
	 * <p>
	 * If enough time has elapsed, a GameState step is also induced.
	 * </p>
	 * 
	 * <p>
	 * GameStates can only be stepped at the updateFrequency.
	 * </p>
	 * 
	 * @return true if EventManager is still running
	 * 
	 */
	public boolean step() {

		if (gsStack.isEmpty())
			return false;

		currentTime = System.nanoTime();
		double elapsedTime = (currentTime - previousTime) / 1.0e9;
		previousTime = currentTime;

		if (elapsedTime > elapsedTimeCap) {
			// truncate so that too many frames aren't simulated
			// (avoids infinite loop)
			elapsedTime = elapsedTimeCap;
		}

		remainingTime += elapsedTime;

		// logic steps
		while (remainingTime >= deltaTime && !gsStack.isEmpty()) {
			gsStack.peekFirst().step(this);

			remainingTime -= deltaTime;
		}

		alpha = remainingTime / deltaTime;

		// render
		if (!gsStack.isEmpty())
			gsStack.peekFirst().render(this, alpha);
		
		/*
		 	Events should probably not be consumed if missed... the gamestate should manually clear them! 
		// consume leftover events
		if (window != null) {
			window.getEvents().clear();
		}
		if (userInput != null) {
			userInput.getEvents().clear();
		}
		if (server != null) {
			server.getEvents().clear();
		}
		*/
		
		return true;
	}

	public List<Event<WindowAction>> getWindowEvents() {
		List<Event<WindowAction>> w = window.getEvents();
		if (w == null) {
			return new ArrayList<Event<WindowAction>>();
		} else {
			return w;
		}
	}

	public List<InputEvent> getInputEvents() {
		List<InputEvent> u = userInput.getEvents();
		if (u == null) {
			return new ArrayList<InputEvent>();
		} else {
			return u;
		}
	}

	
	public List<Event<ServerAction>> getServerEvents() {
		return server.getEvents();
	}
	
	public Window getWindow() {
		return window;
	}
	
	public InputController getInputController() {
		return userInput;
	}
	
	public GameState getCurrentState() {
		return gsStack.peekFirst();
	}
}
