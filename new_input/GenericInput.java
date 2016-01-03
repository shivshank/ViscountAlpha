package shivshank.engine.input;

/**
 * An input trigger describes some input event.
 * 
 * It is a simple container class for grouping together various states that must
 * be simultaneously true for the trigger to fire.
 * 
 * @author shivshank
 */
public interface GenericInput {
	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);
}
