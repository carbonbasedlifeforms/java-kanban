package com.yandex.kanbanboard.service;

import com.yandex.kanbanboard.model.Task;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();

    private Node<Task> head;
    private Node<Task> tail;
    private int size = 0;

    private List<Task> getTasks() {
        return historyMap.values()
                .stream()
                .map(Node::getValue)
                .collect(Collectors.toList());
    }

    private Node<Task> linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(task, tail, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
        size++;
        return newNode;
    }

    private void removeNode(Node<Task> node) {
        if (node.getPrev() == null) {
            head = node.getNext();
        } else {
            node.getPrev().setNext(node.getNext());
        }
        if (node.getNext() == null) {
            tail = node.getPrev();
        } else {
            node.getNext().setPrev(node.getPrev());
        }
        node.setValue(null);
        node.setNext(null);
        node.setPrev(null);
        size--;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (historyMap.containsKey(task.getId())) {
            removeNode(historyMap.get(task.getId()));
        }
        linkLast(task);
        historyMap.put(task.getId(), linkLast(task));
    }

    @Override
    public void remove(int id) {
        historyMap.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private static class Node<T> {
        private T value;
        private Node<T> prev;
        private Node<T> next;

        public Node(T value, Node<T> prev, Node<T> next) {
            this.value = value;
            this.prev = prev;
            this.next = next;
        }

        public T getValue() {
            return value;
        }

        public Node<T> getPrev() {
            return prev;
        }

        public Node<T> getNext() {
            return next;
        }

        public void setNext(Node<T> next) {
            this.next = next;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public void setPrev(Node<T> prev) {
            this.prev = prev;
        }
    }
}
