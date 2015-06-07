package shivshank.engine.events;

public class Event<V extends Enum<?>> {
	protected Object origin;
	protected V action;
	protected long createdOn;

	public Event(Object origin, V action) {
		createdOn = System.currentTimeMillis();
		this.origin = origin;
		this.action = action;
	}

	public Object getOrigin() {
		return origin;
	}

	/**
	 * Checks if this Event has the same origin as Event e.
	 * <p>
	 * Checks identity, not Object.equals.
	 * 
	 * @param e
	 * @return e.origin == this.origin
	 */
	public boolean sameOrigin(Event<?> e) {
		return e.getOrigin() == this.origin;
	}

	/**
	 * Gets whether Event e is newer than this.
	 * 
	 * @return true if this is newer
	 */
	public boolean newerThan(Event<?> e) {
		return e.createdOn < this.createdOn;
	}

	public V getAction() {
		return this.action;
	}

	public boolean equals(Event<?> e) {
		// for future compatibility use equals (types may not be Enums)
		return this.action.equals(e.getAction());
	}
}
