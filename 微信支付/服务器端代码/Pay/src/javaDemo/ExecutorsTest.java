package javaDemo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorsTest {
	
	
	
	public static void main(String[] args) {
		final ExecutorService es = Executors.newFixedThreadPool(4);
//		final ExecutorService es = Executors.newSingleThreadExecutor();
		Executors.newCachedThreadPool();
		es.execute(new Runnable() {
			
			public void run() {
				for (int i = 0; i < 10; i++) {
//					if (i==5) {
//						i/=0;
//					}
					System.out.println(Thread.currentThread().getName()+":"+i);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				es.shutdown();
			}
		});
	}
}
