package shivshank.engine;

public interface GameState {

	public void render(EventManager e, double alpha);

	/**
	 * Called for every logic update.
	 * 
	 * @param e
	 *            the calling EventManager
	 */
	public void step(EventManager e);

	/**
	 * Called when the GameState is loaded onto an EventManagers gamestate
	 * stack.
	 * 
	 * @param e
	 *            the calling EventManager
	 * @param exitedState
	 *            the previous GameState
	 */
	public void create(EventManager e, GameState exitedState);

	/**
	 * Called when EventManager.exitState is called and this state becomes the
	 * head of the stack.
	 * 
	 * @param e
	 *            the calling EventManager
	 * @param exitedState
	 *            the state popped off the stack
	 */
	public void resume(EventManager e, GameState exitedState);

	/**
	 * Called when a new GameState is loaded onto the gamestate stack with
	 * EventManager.pauseState.
	 * 
	 * @param e
	 *            the calling EventManager
	 * @param enteredState
	 *            the state pushed onto the stack
	 */
	public void pause(EventManager e, GameState enteredState);

	/**
	 * Called when EventManager.exitState or .switchState pops this GameState
	 * off the stack.
	 * 
	 * @param e
	 *            the calling EventManager
	 * @param enteredState
	 *            the state pushed onto the stack
	 */
	public void destroy(EventManager e, GameState enteredState);
}
