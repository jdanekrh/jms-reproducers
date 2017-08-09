/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.artemis.tests.integration.amqp;

import org.apache.activemq.JMSClientTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.Enumeration;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class JMSMessageConsumerTest extends JMSClientTestSupport {

   protected static final Logger LOG = LoggerFactory.getLogger(JMSMessageConsumerTest.class);

   @SuppressWarnings("rawtypes")
   @Test(timeout = 30000)
   public void testSelectorsWithJMSPriority() throws Exception {
      Connection connection = createConnection();

      try {
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         javax.jms.Queue queue = session.createQueue("testSelectorsWithJMSPriority_" + randomSuffix);
         MessageProducer p = session.createProducer(queue);

         TextMessage message = session.createTextMessage();
         message.setText("hello");
         p.send(message, DeliveryMode.PERSISTENT, 5, 0);

         message = session.createTextMessage();
         message.setText("hello + 9");
         p.send(message, DeliveryMode.PERSISTENT, 9, 0);

         QueueBrowser browser = session.createBrowser(queue);
         Enumeration enumeration = browser.getEnumeration();
         int count = 0;
         while (enumeration.hasMoreElements()) {
            Message m = (Message) enumeration.nextElement();
            assertTrue(m instanceof TextMessage);
            count++;
         }

         assertEquals(2, count);

         MessageConsumer consumer = session.createConsumer(queue, "JMSPriority > 8");
         Message msg = consumer.receive(2000);
         assertNotNull(msg);
         assertTrue(msg instanceof TextMessage);
         assertEquals("hello + 9", ((TextMessage) msg).getText());
      } finally {
         connection.close();
      }
   }

   @Test(timeout = 60000)
   public void testJMSSelectorFiltersJMSMessageID() throws Exception {
      Connection connection = createConnection();

      try {
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         javax.jms.Queue queue = session.createQueue("testJMSSelectorFiltersJMSMessageID_" + randomSuffix);
         MessageProducer producer = session.createProducer(queue);

         // Send one to receive
         TextMessage message = session.createTextMessage();
         producer.send(message);

         // Send another to filter
         producer.send(session.createTextMessage());

         connection.start();

         // First one should make it through
         MessageConsumer messageConsumer = session.createConsumer(queue, "JMSMessageID = '" + message.getJMSMessageID() + "'");
         TextMessage m = (TextMessage) messageConsumer.receive(5000);
         assertNotNull(m);
         assertEquals(message.getJMSMessageID(), m.getJMSMessageID());

         // The second one should not be received.
         assertNull(messageConsumer.receive(1000));
      } finally {
         connection.close();
      }
   }
}
