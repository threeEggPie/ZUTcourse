package xyz.kingsword.course;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

public class test {
    public static void main(String[] args) {
         final byte[] key = new byte[]{49, 50, 25, 52, 63, 54, 55, 32, 57, 48, 49, 75, 51, 52, 20, 2};

        final SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
        String s1 = aes.decryptStr("1bdca0a107fba49b54fd3b90ce33c8069db30fa7d76d87a895e4e054a5c6ad096262cfc3535b5cb5ee738c329424840f");
        System.out.println(s1);


    }
}
