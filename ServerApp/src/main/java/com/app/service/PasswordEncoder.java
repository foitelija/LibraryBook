package com.app.service;


import org.apache.commons.codec.digest.DigestUtils;

/**
 * кодирует пароль в md5, что бы не хранить в базе открытом виде
 */
public class PasswordEncoder {
    public static String encodePassword(String pass) {
        return DigestUtils
                .md5Hex(pass).toUpperCase();

    }
}
