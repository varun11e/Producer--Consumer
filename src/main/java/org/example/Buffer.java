package org.example;

import java.util.LinkedList;

public class Buffer {
    public LinkedList<Integer> list = new LinkedList<>();
    public boolean producer1Turn = true;
    public boolean producer2Turn = false;
    public boolean consumerTurn = false;
}