package bgu.spl.mics;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */

import java.util.*;
import java.util.concurrent.*;

public class MessageBusImpl implements MessageBus {
	private final Map<MicroService, BlockingQueue<Message>> queues;
	private final Map<Class<? extends Event<?>>, List<MicroService>> eventSubscriptions;
	private final Map<Class<? extends Broadcast>, List<MicroService>> broadcastSubscriptions;
	private final Map<Event<?>, Future<?>> futures;

	private static final MessageBusImpl instance = new MessageBusImpl();

	private MessageBusImpl() {
		this.queues = new ConcurrentHashMap<>();
		this.eventSubscriptions = new ConcurrentHashMap<>();
		this.broadcastSubscriptions = new ConcurrentHashMap<>();
		this.futures = new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance() {
		return instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {

		List<MicroService> subscribers = eventSubscriptions.computeIfAbsent(type, k -> Collections.synchronizedList(new ArrayList<>()));
		synchronized (subscribers) {
			if (!subscribers.contains(m)) {
				subscribers.add(m);
			}
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {

		List<MicroService> subscribers = broadcastSubscriptions.computeIfAbsent(type, k -> Collections.synchronizedList(new ArrayList<>()));
		synchronized (subscribers) {
			if (!subscribers.contains(m)) {
				subscribers.add(m);
			}
		}
	}


	@Override
	public <T> void complete(Event<T> e, T result) {

		synchronized (futures) {
			Future f = futures.get(e);
			if (f != null) {
				f.resolve(result);
			}

		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {

		List<MicroService> subscribers = broadcastSubscriptions.get(b.getClass());
		if (subscribers != null) {
			synchronized (subscribers) {
				for (MicroService m : subscribers) {

					if (m == null) {
						System.out.println("Null MicroService found in subscribers");
				}
					if (queues.get(m) == null) {
						System.out.println("Queue for MicroService " + m.getName() + " is null");
					}
					queues.get(m).offer(b);
				}
			}
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {

		List<MicroService> subscriptions = eventSubscriptions.get(e.getClass());
		if (subscriptions == null || subscriptions.isEmpty()) {
			return null;
		}
		MicroService TempMS = subscriptions.remove(0);
		System.out.println( TempMS.getName() + " received to his queue event " + e);
		subscriptions.add(TempMS);

		if (TempMS == null) {
			System.out.println("Null MicroService found in subscriptions");
		}
		if (queues.get(TempMS) == null) {
			System.out.println("Queue for MicroService " + TempMS.getName() + " is null");
		}

		queues.get(TempMS).offer(e);
		Future<T> future = new Future<>();
		futures.put(e, future);
		return future;
	}


	@Override
	public void register(MicroService m) {

		queues.putIfAbsent(m, new LinkedBlockingQueue<>());

	}

	@Override
	public void unregister(MicroService m) {
		if (queues.remove(m) != null) {

			for (List<MicroService> subscribers : eventSubscriptions.values()) {
				synchronized (subscribers) {
					subscribers.remove(m);
				}
			}

			for (List<MicroService> subscribers : broadcastSubscriptions.values()) {
				synchronized (subscribers) {
					subscribers.remove(m);
				}
			}
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (!queues.containsKey(m))
			throw new IllegalStateException("Microservice is not registered ");
		BlockingQueue<Message> mQ = queues.get(m);
			try {
				Message output = mQ.take();
				return output;
			} catch (InterruptedException e) {
				throw new InterruptedException();
			}
		}
	}
