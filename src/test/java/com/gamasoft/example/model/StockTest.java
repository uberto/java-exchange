package com.gamasoft.example.model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class StockTest {
    @Test
    public void testEquals() throws Exception {
        Stock s1 = new Stock("B", "B&C");
        Stock s2 = new Stock("B", "B&C");
        Stock s3 = new Stock("C", "C&C");

        assertThat(s1.getName(), is("B&C"));
        assertThat(s1.getTicker(), is("B"));
        assertThat(s1, is(s2));
        assertThat(s2, not(s3));
    }
}
