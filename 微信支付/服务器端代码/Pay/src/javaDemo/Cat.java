package javaDemo;

public class Cat implements Comparable<Cat>{
	String name;
	int weight;
	int age;
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "Cat [age=" + age + ", name=" + name + ", weight=" + weight
				+ "]\n";
	}

	public Cat(String name, int weight, int age) {
		super();
		this.name = name;
		this.weight = weight;
		this.age = age;
	}

	public int compareTo(Cat o) {
		int we = this.weight - o.weight;
		if(we==0){
			we = this.age - o.age;
			if (we==0) {
				return this.name.compareTo(o.name);
			}
			return we;
		}
		return we;
	}
	
	public int compare(Cat c1,Cat c2){
		return age;		
	}

}
