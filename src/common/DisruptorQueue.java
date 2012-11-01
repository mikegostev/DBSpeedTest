package common;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.lmax.disruptor.AlertException;
import com.lmax.disruptor.ClaimStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.SequenceGroup;
import com.lmax.disruptor.Sequencer;
import com.lmax.disruptor.WaitStrategy;

public class DisruptorQueue<T> extends AbstractQueue<T> implements BlockingQueue<T> {

    // Use ThreadLocal to manage consumer sequence group
    private final ThreadLocal<Sequence> threadSequence = new ThreadLocal<Sequence>() {
        protected Sequence initialValue() {
            Sequence sequence = new Sequence(sequenceGroup.get());
            sequenceGroup.add(sequence);
            return sequence;
        };
    };
    private final SequenceGroup sequenceGroup = new SequenceGroup();
    private final Sequence sharedSequence = new Sequence(Sequencer.INITIAL_CURSOR_VALUE);
    private final RingBuffer<ValueEvent> ringBuffer;
    private final SequenceBarrier sequenceBarrier;       

    public DisruptorQueue(
            final int size,
            final ClaimStrategy claimStrategyOption,
            final WaitStrategy waitStrategyOption) {
        ringBuffer = new RingBuffer<ValueEvent>(EVENT_FACTORY,
                claimStrategyOption,
                waitStrategyOption);
        
        ringBuffer.setGatingSequences(sequenceGroup);
        sequenceBarrier = ringBuffer.newBarrier();
        final long cursor = ringBuffer.getCursor();
        sharedSequence.set(cursor);
    }
    
    // TODO jeff Using it as stopper, since wait strategy might not check interruption status
    public void clear() {
        sequenceBarrier.alert();
    }

    public void put(T event) throws InterruptedException {
        // Standard publish
        long sequence = ringBuffer.next();
        ringBuffer.get(sequence).setValue(event);
        ringBuffer.publish(sequence);   
    }

    @SuppressWarnings("unchecked")
    public T take() throws InterruptedException {
        // Get next sequence and set current sequence processed point to the previous 
        // one. This makes sure last processed point is up to date if we block - allowing the 
        // publisher to wrap all the way to the consumer point without blocking it too.
        long nextSequence = sharedSequence.incrementAndGet();
        // TODO jeff retrieve thread local only once
        final Sequence tls = getSequence();
        tls.set(nextSequence-1);
        
        try {
            sequenceBarrier.waitFor(nextSequence);
        } catch (AlertException ex) {
            throw new InterruptedException();
        }
        
        // We now have sequence so get data for it
        T result = (T)ringBuffer.get(nextSequence).getValue();
        
        // Confirm we've processed this point to enable the publisher to take this 
        // slot if required.
        tls.set(nextSequence);
        return result;
    }

    public Sequence getSequence() {
        return threadSequence.get();
    }

    public static final class ValueEvent {
        private Object value;

        public Object getValue()
        {
            return value;
        }

        public void setValue(final Object value)
        {
            this.value = value;
        }
    }

    private static final EventFactory<ValueEvent> EVENT_FACTORY = new EventFactory<ValueEvent>()
    {
        @Override
        public ValueEvent newInstance() {
            return new ValueEvent();
        }
    };

    @Override
    public T poll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public T peek() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean offer(T paramE) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean offer(T paramE, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public T poll(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int remainingCapacity() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int drainTo(Collection<? super T> paramCollection) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int drainTo(Collection<? super T> paramCollection, int paramInt) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Iterator<T> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
        return 0;
    }

}
