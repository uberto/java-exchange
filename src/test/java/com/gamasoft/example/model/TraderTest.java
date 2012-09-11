package com.gamasoft.example.model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class TraderTest {
    @Test
    public void testEquals() throws Exception {
        Trader t1 = new Trader("Ben");
        Trader t2 = new Trader("Ben");
        Trader t3 = new Trader("Ken");

        assertThat(t1.getName(), is("Ben"));
        assertThat(t1, is(t2));
        assertThat(t2, not(t3));
    }
}
