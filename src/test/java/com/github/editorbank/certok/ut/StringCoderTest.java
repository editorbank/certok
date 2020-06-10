package com.github.editorbank.certok.ut;

import org.junit.Test;
import com.github.editorbank.certok.StringCoder;

public class StringCoderTest {
    @Test
    public void testEncodeDecode() {
        StringCoder stringCoder = new StringCoder();
        String text = "secret";
        String code = stringCoder.encode(text);
        System.out.println(text + " -> " + code);
        assert( stringCoder.decode(code).equals(text+"") );

    }
}
