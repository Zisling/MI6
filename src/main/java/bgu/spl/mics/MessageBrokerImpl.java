package bgu.spl.mics;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The {@link MessageBrokerImpl class is the implementation of the MessageBroker interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBrokerImpl implements MessageBroker {
	private static MessageBroker instance;
	private ConcurrentHashMap<Class<? extends Event<?>>, ConcurrentLinkedQueue<Subscriber>> eventMap;
	private ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<Subscriber>> broadcastMap;
	private ConcurrentHashMap<Subscriber,ConcurrentLinkedQueue<Message>> subMap;


//	constructor
	private MessageBrokerImpl(){
		eventMap = new ConcurrentHashMap<>();
		broadcastMap = new ConcurrentHashMap<>();
		subMap = new ConcurrentHashMap<>();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static MessageBroker getInstance() {
		if(instance==null){
			synchronized (MessageBrokerImpl.class){
		if (instance==null){instance=new MessageBrokerImpl();}}}
		return instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, Subscriber m) {
		eventMap.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {
		broadcastMap.get(type).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		Subscriber temp = broadcastMap.get(b.getClass()).poll();
		if (temp!=null){
		subMap.get(temp).add(b);}
		broadcastMap.get(b.getClass()).add(temp);

		// TODO Auto-generated method stub

	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		Future<T> futureOut=null;
		ConcurrentLinkedQueue<Subscriber> roundRobinQ=eventMap.get(e.getClass());
		Subscriber currSub=roundRobinQ.poll();

		if(currSub!=null) {
			if (subMap.containsKey(currSub)){
				subMap.get(currSub).add(e);
				currSub.notify();
				roundRobinQ.add(currSub);
				futureOut=new Future<>();
			}
		}
		else{
//			*temp line
			System.out.println("currSub is null");
		}

		return futureOut;
	}

	@Override
	public void register(Subscriber m) {
		if(!subMap.containsKey(m))
		subMap.put(m, new ConcurrentLinkedQueue<>());
	}

	@Override
	public void unregister(Subscriber m) {
		subMap.remove(m);
	}

	@Override
	public Message awaitMessage(Subscriber m) throws InterruptedException {
		// TODO Auto-generated method stub
		ConcurrentLinkedQueue<Message> subMessagelist=subMap.get(m);
		if(subMessagelist.isEmpty())
		{
			synchronized (m)
			{
				m.wait();
			}
		}

		return subMessagelist.poll();
	}

	

}
