package javaDemo;

public class GenericDemo {
	public static <T> void print(T[] t){
		for (int i = t.length-1; i >=0; i--) {
			System.out.println(t[i]);
		}
		return;
	}
	public static void main(String[] args) {
		String[] i =new String[]{"ei","kkk"};
		print(i);
	}
}
