package org.minitestlang.interpreter;

public class IntValue implements Value{
    private final int number;

    public IntValue(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "IntValue{" +
                "number=" + number +
                '}';
    }
}
