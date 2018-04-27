package javaDemo;

public class ThreadDemo {
	public static void main(String[] args) {
		final Object object = new Object();
//		new Thread(new Runnable() {
//			public void run() {
//				synchronized (this) {
//					
//					String args = "runnable";
//					for (int i = 0; i < args.length(); i++) {
//						char a = args.charAt(i);
//						System.out.println(Thread.currentThread().getName()+a);
//						try {
//							Thread.sleep(500);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//
//				}
//			}
//		}).start();

		new ThreadClass().start();
		
	}
}
