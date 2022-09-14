package ru.itmo.lab1.model;

public enum MIC {
    //TODO add more codes
    XSTO("XSTO"),
    XNAS("XNAS"),
    MISX("MISX");

    private final String name;

    MIC(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
