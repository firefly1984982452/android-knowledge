package javaDemo;

public class ThreadClass extends Thread {

	final Object object = new Object();
	@Override
	public void run() {
		synchronized (this) {
			
		String args = "extends thread";
		for (int i = 0; i < args.length(); i++) {
			char a = args.charAt(i);
			System.out.println(Thread.currentThread().getName()+a);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		}
	}
}
