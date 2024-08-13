package com.yandex.kanbanboard.service;

import com.yandex.kanbanboard.model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> historyMap = new HashMap<>();

    private Node head;
    private Node tail;
    private int size = 0;

    private List<Task> getTasks() {
        return historyMap.values()
                .stream()
                .map(Node::getValue)
                .collect(Collectors.toList());
    }

    private Node linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(task, tail, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
        size++;
        return newNode;
    }

    private void removeNode(Node node) {
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
}



