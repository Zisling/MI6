package bgu.spl.mics;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import bgu.spl.mics.application.Broadcasts.Terminating;
import bgu.spl.mics.application.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Events.*;

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
	private ConcurrentHashMap<Event,Future> futureMessageMap;


//	constructor
	private MessageBrokerImpl(){
		eventMap = new ConcurrentHashMap<>();
		broadcastMap = new ConcurrentHashMap<>();
		subMap = new ConcurrentHashMap<>();
		futureMessageMap=new ConcurrentHashMap<>();

		broadcastMap.put(TickBroadcast.class,new ConcurrentLinkedQueue<>());
		broadcastMap.put(Terminating.class,new ConcurrentLinkedQueue<>());
		eventMap.put(AgentAvailableEvent.class,new ConcurrentLinkedQueue<>());
		eventMap.put(MissionReceviedEvent.class,new ConcurrentLinkedQueue<>());
		eventMap.put(GadgetAvailableEvent.class,new ConcurrentLinkedQueue<>());
		eventMap.put(ReadyEvent.class,new ConcurrentLinkedQueue<>());
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
		Future futureToResolve=futureMessageMap.get(e);
		System.out.println(result.getClass());
		if (futureToResolve!=null){
			futureToResolve.resolve(result);
			System.out.println(futureToResolve.isDone());
		}else {
			System.out.println("Resolve is null");
		}
		futureMessageMap.remove(e);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		ConcurrentLinkedQueue<Subscriber> bSubscribers = broadcastMap.get(b.getClass());
		for (Subscriber sub:bSubscribers) {
			if(subMap.containsKey(sub))
			{
				synchronized (subMap.get(sub)) {
					subMap.get(sub).add(b);
				}
			}
			synchronized (sub)
			{
				sub.notify();
			}
		}

		// TODO Auto-generated method stub

	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		Future<T> futureOut=null;
		ConcurrentLinkedQueue<Subscriber> roundRobinQ=eventMap.get(e.getClass());
		if (!roundRobinQ.isEmpty()){
			Subscriber currSub=null;
			currSub=roundRobinQ.poll();
			roundRobinQ.add(currSub);
			if(currSub!=null) {
			try {
				if (subMap.containsKey(currSub)){
					subMap.get(currSub).add(e);
					futureOut=new Future<>();
					futureMessageMap.put(e,futureOut);//Adding the future object associated to the event to the hash map
					currSub.notify();
				}
			}catch (Exception m){m.getMessage();}
		}
		else{
//			*temp line
			System.out.println("currSub is null");
		}
		}
		if (futureOut==null)
		System.out.println("send a futureOut null");
		return futureOut;
	}

	@Override
	public void register(Subscriber m) {
		if(!subMap.containsKey(m))
		subMap.put(m, new ConcurrentLinkedQueue<>());
	}

	@Override
	public void unregister(Subscriber m) {
		if(subMap.containsKey(m)) {
			unSubscribeFromEvents(AgentAvailableEvent.class, m);
			unSubscribeFromEvents(GadgetAvailableEvent.class, m);
			unSubscribeFromEvents(MissionReceviedEvent.class, m);
			subMap.remove(m);
		}
	}

	private void unSubscribeFromEvents(Class eventClass,Subscriber subToUnregister)
	{
		synchronized (eventMap.get(eventClass)){
			eventMap.get(eventClass).remove(subToUnregister);
		}
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
