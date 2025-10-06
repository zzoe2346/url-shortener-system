package com.jeongseonghun.urlshortener.common.util;

import java.util.HashMap;
import java.util.Map;

public class Base62 {
    private static final char[] BASE62_CHAR_ARRAY = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final Map<Character, Integer> BASE62_CHAR_MAP = new HashMap<>();

    private Base62() {
    }

    static {
        for (int i = 0; i < BASE62_CHAR_ARRAY.length; i++) {
            BASE62_CHAR_MAP.put(BASE62_CHAR_ARRAY[i], i);
        }
    }

    public static class Decoder {

        public static long decode(String str) {
            long result = 0;
            long power = 1;

            for (int i = str.length() - 1; i >= 0; i--) {
                char c = str.charAt(i);
                Integer charValue = BASE62_CHAR_MAP.get(c);

                result += charValue * power;
                power *= 62;
            }

            return result;
        }
    }

    public static class Encoder {

        public static String encode(Long value) {
            StringBuilder sb = new StringBuilder();
            while (value > 0) {
                sb.append(BASE62_CHAR_ARRAY[(int) (value % 62)]);
                value /= 62;
            }

            return sb.reverse().toString();
        }
    }
}
