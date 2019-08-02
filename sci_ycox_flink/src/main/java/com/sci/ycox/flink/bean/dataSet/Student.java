package com.sci.ycox.flink.bean.dataSet;

public class Student {

    private String name;

    private String addr;

    private double salary;

    public Student() {
    }

    public Student(String name, String addr, double salary) {
        this.name = name;
        this.addr = addr;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", addr='" + addr + '\'' +
                ", salary=" + salary +
                '}';
    }
}
