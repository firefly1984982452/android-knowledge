package javaDemo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CatTest {
	public static void main(String[] args) {
		List<Cat> cats = new ArrayList<Cat>();
		cats.add(new Cat("andy", 3, 1));
		cats.add(new Cat("july", 2, 1));
		cats.add(new Cat("bob", 4, 3));
		cats.add(new Cat("aib", 4, 3));
		Collections.sort(cats);
		System.out.println(cats);
	}
}
