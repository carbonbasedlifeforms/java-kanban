package com.yandex.kanbanboard.service;

import com.yandex.kanbanboard.model.Task;

public class Node {
    private Task value;
    private Node prev;
    private Node next;

    public Node(Task value, Node prev, Node next) {
        this.value = value;
        this.prev = prev;
        this.next = next;
    }

    public Task getValue() {
        return value;
    }

    public Node getPrev() {
        return prev;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void setValue(Task value) {
        this.value = value;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }
}
