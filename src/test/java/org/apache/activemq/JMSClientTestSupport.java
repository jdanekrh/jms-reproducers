package org.apache.activemq;

import org.junit.Before;

import javax.jms.Connection;
import javax.jms.JMSException;
import java.math.BigInteger;
import java.util.Random;

public class JMSClientTestSupport extends JMSTestBase {
    protected String randomSuffix;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        // https://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
        Random random = new Random();
        randomSuffix = new BigInteger(130, random).toString(32);
    }
    protected Connection createConnection() throws JMSException {
        return cf.createConnection();
    }
}
