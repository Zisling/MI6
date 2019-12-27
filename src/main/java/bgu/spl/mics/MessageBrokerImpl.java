package bgu.spl.mics;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import bgu.spl.mics.application.Broadcasts.AbortBroadCast;
import bgu.spl.mics.application.Broadcasts.Terminating;
import bgu.spl.mics.application.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Events.*;
import bgu.spl.mics.application.subscribers.Q;

/**
 * The {@link MessageBrokerImpl class is the implementation of the MessageBroker interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBrokerImpl implements MessageBroker {
	private ConcurrentHashMap<Class<? extends Event<?>>, ConcurrentLinkedQueue<Subscriber>> eventMap;
	private ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<Subscriber>> broadcastMap;
	private ConcurrentHashMap<Subscriber,ConcurrentLinkedQueue<Message>> subMap;
	private ConcurrentHashMap<Event,Future> futureMessageMap;
	private Q MyQ;
	private Semaphore EventSem=new Semaphore(1);
	private Semaphore BroadSem=new Semaphore(1);


	//	constructor
	private MessageBrokerImpl(){
		eventMap = new ConcurrentHashMap<>();
		broadcastMap = new ConcurrentHashMap<>();
		subMap = new ConcurrentHashMap<>();
		futureMessageMap=new ConcurrentHashMap<>();

//		broadcastMap.put(TickBroadcast.class,new ConcurrentLinkedQueue<>());
//		broadcastMap.put(Terminating.class,new ConcurrentLinkedQueue<>());
//		eventMap.put(AbortBroadCast.class,new ConcurrentLinkedQueue<>());
//		eventMap.put(AgentAvailableEvent.class,new ConcurrentLinkedQueue<>());
//		eventMap.put(MissionReceviedEvent.class,new ConcurrentLinkedQueue<>());
//		eventMap.put(GadgetAvailableEvent.class,new ConcurrentLinkedQueue<>());
//		eventMap.put(ReadyEvent.class,new ConcurrentLinkedQueue<>());
	}

	private static class MessageBrokerImpHolder{
		private static final MessageBrokerImpl instance =new MessageBrokerImpl();
	}
	/**
	 * Retrieves the single instance of this class.
	 */
	public static MessageBroker getInstance() {
		return MessageBrokerImpHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, Subscriber m){
		if(!eventMap.containsKey(type)) {
			try {
				EventSem.acquire();
				eventMap.put(type, new ConcurrentLinkedQueue<>());
				eventMap.get(type).add(m);
			} catch (Exception e) {
			} finally {
				EventSem.release();
			}
		}
		else
		{
			eventMap.get(type).add(m);
		}

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {
		if(!broadcastMap.containsKey(type)) {
			try {
				BroadSem.acquire();
				broadcastMap.put(type, new ConcurrentLinkedQueue<>());
				broadcastMap.get(type).add(m);
			} catch (Exception e) {
			} finally {
				BroadSem.release();
			}
		}
		else
		{
			broadcastMap.get(type).add(m);
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future futureToResolve=futureMessageMap.get(e);
		if (futureToResolve!=null){
			futureToResolve.resolve(result);
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
				sub.notifyAll();
			}
		}

	}

	
	@Override
	public  <T> Future<T> sendEvent(Event<T> e) {
		Future<T> futureOut=null;
		ConcurrentLinkedQueue<Subscriber> roundRobinQ=eventMap.get(e.getClass());
		Subscriber currSub=null;
		synchronized (eventMap.get(e.getClass())){
		if (!roundRobinQ.isEmpty()){
			currSub=roundRobinQ.poll();
			roundRobinQ.add(currSub);}}
			if(currSub!=null) {
				try {
				if (subMap.containsKey(currSub)){
					subMap.get(currSub).add(e);
					futureOut=new Future<>();
					futureMessageMap.put(e,futureOut);//Adding the future object associated to the event to the hash map
				}
				synchronized (currSub){
				currSub.notifyAll();}
			}catch (Exception m){m.getMessage();}
		}
			else {
				if (e.getClass()==GadgetAvailableEvent.class){
					subMap.get(MyQ).add(e);
					futureOut=new Future<>();
					futureMessageMap.put(e,futureOut);
					synchronized (MyQ){
						MyQ.notifyAll();}
			}
			}
		return futureOut;
	}

	@Override
	public void register(Subscriber m) {
		if(!subMap.containsKey(m))
		subMap.put(m, new ConcurrentLinkedQueue<>());

		if (m.getClass()==Q.class){
			MyQ= ((Q) m);
		}
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
