package com.redhat.qe;

import org.apache.activemq.JMSTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.jms.*;

@RunWith(value = Parameterized.class)
public class TopicRequestorTest extends JMSTestBase {

    @Test(expected = InvalidDestinationException.class)
    public void testTopicRequestorWithNullDestination() throws Exception {
        Connection connection = cf.createConnection();
        connection.start();

        TopicSession topicSession = ((TopicConnection) connection).createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        TopicRequestor topicRequestor = new TopicRequestor(topicSession, null);
    }
}
