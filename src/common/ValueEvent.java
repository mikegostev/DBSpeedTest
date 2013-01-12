package common;

import com.lmax.disruptor.EventFactory;

public final class ValueEvent {
	private byte[] value;

	public byte[] getValue() {
		return value;
	}

	public void setValue(final byte[] value) {
		this.value = value;
	}

	public final static EventFactory<ValueEvent> EVENT_FACTORY = new EventFactory<ValueEvent>() {
		public ValueEvent newInstance() {
			return new ValueEvent();
		}
	};
}
