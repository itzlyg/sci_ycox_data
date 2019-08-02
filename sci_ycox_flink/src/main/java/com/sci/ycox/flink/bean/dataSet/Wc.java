package com.sci.ycox.flink.bean.dataSet;

public class Wc {

	private String word;

	private int salary;
	public Wc(){

	}

	public Wc(String word, int salary){
		this.word = word;
		this.salary = salary;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}

    @Override
    public String toString() {
        return "Wc{" +
                "word='" + word + '\'' +
                ", salary=" + salary +
                '}';
    }
}
