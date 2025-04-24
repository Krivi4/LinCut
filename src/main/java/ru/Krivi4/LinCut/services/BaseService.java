package ru.Krivi4.LinCut.services;

public interface BaseService {
    String encode(long input);
    long decode(String encoded);

}
