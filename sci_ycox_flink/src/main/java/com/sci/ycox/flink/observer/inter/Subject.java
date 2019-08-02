package com.sci.ycox.flink.observer.inter;

import java.io.Serializable;

public interface Subject extends Serializable {
    /**
     * 添加观察者
     * @param observer
     */
    public void addObserver(Observer observer);

    /**
     * 删除指定观察者
     * @param observer
     */
    public void deleteObserver(Observer observer);

    /**
     * 通知所有观察者
     */
    public void notifyObservers();
}
