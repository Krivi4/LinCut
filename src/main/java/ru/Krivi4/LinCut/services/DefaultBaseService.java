package ru.Krivi4.LinCut.services;

import org.springframework.stereotype.Service;

@Service
public class DefaultBaseService implements BaseService {

    private static final String allowedString =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private char[] allowedCharacters = allowedString.toCharArray();

    private int base = allowedCharacters.length;


    /**Преобразование длинной ссылки в короткую (зашифровка в базу 62 из базы 10) */
    @Override
    public String encode(long input){
        var encodedString = new StringBuilder();

        if(input == 0) {
            return String.valueOf(allowedCharacters[0]);
        }

        while (input > 0) {
            encodedString.append(allowedCharacters[(int) (input % base)]);
            input = input / base;
        }

        return encodedString.reverse().toString();
    }

    /**Преобразование короткой ссылки в длинную (расшифровка из базу 62 в базы 10) */
    @Override
    public long decode(String input) {
        var characters = input.toCharArray();
        var length = characters.length;

        var decoded = 0;


        var counter = 1;
        for (int i = 0; i < length; i++) {
            decoded += allowedString.indexOf(characters[i]) * Math.pow(base, length - counter);
            counter++;
        }
        return decoded;
    }
}