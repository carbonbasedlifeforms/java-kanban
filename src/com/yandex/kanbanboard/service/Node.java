package com.yandex.kanbanboard.service;

public class Node<Task> {
    private Task value;
    private Node<Task> prev;
    private Node<Task> next;

    public Node(Task value, Node<Task> prev, Node<Task> next) {
        this.value = value;
        this.prev = prev;
        this.next = next;
    }

    public Task getValue() {
        return value;
    }

    public Node<Task> getPrev() {
        return prev;
    }

    public Node<Task> getNext() {
        return next;
    }

    public void setNext(Node<Task> next) {
        this.next = next;
    }

    public void setValue(Task value) {
        this.value = value;
    }

    public void setPrev(Node<Task> prev) {
        this.prev = prev;
    }
}
