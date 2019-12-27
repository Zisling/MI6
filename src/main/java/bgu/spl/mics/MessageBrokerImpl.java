package bgu.spl.mics;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import bgu.spl.mics.application.Events.*;
import bgu.spl.mics.application.subscribers.Moneypenny;
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
			try {
				EventSem.acquire();
				if(!eventMap.containsKey(type)) {
					eventMap.put(type, new ConcurrentLinkedQueue<>());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				EventSem.release();
				eventMap.get(type).add(m);
			}
		}


	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {
			try {
				BroadSem.acquire();
				if (!broadcastMap.containsKey(type)) {
					broadcastMap.put(type, new ConcurrentLinkedQueue<>());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				BroadSem.release();
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
			for (Class<? extends Event<?>> aClass : eventMap.keySet()) {
				if (eventMap.get(aClass).contains(m)) {
					unSubscribeFromEvents(aClass,m);
				}
			}
			for (Class<? extends Broadcast> aClass : broadcastMap.keySet()) {
				if(broadcastMap.get(aClass).contains(m))
				{
					unSubscriveFromBroadcasts(aClass,m);
				}

			}
			synchronized (m) {
				System.out.println(subMap.get(m).isEmpty());
				System.out.println(m.getName()+" "+m.getClass());
				if(m.getClass()== Moneypenny.class){
					System.out.println("debuge");
				}
				subMap.remove(m);
			}
		}
	}

	private void unSubscribeFromEvents(Class eventClass,Subscriber subToUnregister)
	{
		synchronized (eventMap.get(eventClass)){
			eventMap.get(eventClass).remove(subToUnregister);
		}
	}
	private void unSubscriveFromBroadcasts(Class broadClass,Subscriber subToUnregister)
	{
		synchronized (broadcastMap.get(broadClass))
		{
			broadcastMap.get(broadClass).remove(subToUnregister);
		}
	}

	@Override
	public Message awaitMessage(Subscriber m) throws InterruptedException {
		ConcurrentLinkedQueue<Message> subMessagelist=subMap.get(m);
		while(subMessagelist.isEmpty())
		{
			synchronized (m)
			{
				m.wait();
			}
		}
		return subMessagelist.poll();
	}

	

}
