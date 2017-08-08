package org.apache.activemq.artemis.tests.integration.jms.client;/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.jms.*;

import static junit.framework.TestCase.assertNotNull;
import static org.jgroups.util.Util.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.activemq.JMSTestBase;

@RunWith(Parameterized.class)
public class NoLocalSubscriberTest extends JMSTestBase {
   // Constants -----------------------------------------------------

   @Test
   public void testNoLocalReconnect() throws Exception {
      ConnectionFactory connectionFactory = cf;
      String uniqueID = Long.toString(System.currentTimeMillis());
      String topicName = "exampleTopic";
      String clientID = "myClientID_" + uniqueID;
      String subscriptionName = "mySub_" + uniqueID;

      boolean noLocal = true;
      String messageSelector = "";
      Topic topic = createTopic(topicName);

      {
         // Create durable subscription
         Connection connection = connectionFactory.createConnection("guest", "guest");
         connection.setClientID(clientID);
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         TopicSubscriber topicSubscriber = session.createDurableSubscriber(topic, subscriptionName, messageSelector, noLocal);
         topicSubscriber.close();
         connection.close();
      }

      {
         // create a connection using the same client ID and send a message
         // to the topic
         // this will not be added to the durable subscription
         Connection connection = connectionFactory.createConnection("guest", "guest");
         connection.setClientID(clientID);
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         MessageProducer messageProducer = session.createProducer(topic);
         messageProducer.send(session.createTextMessage("M3"));
         connection.close();
      }

      {
         // create a connection using a different client ID and send a
         // message to the topic
         // this will be added to the durable subscription
         Connection connection = connectionFactory.createConnection("guest", "guest");
         connection.setClientID(clientID + "_different");
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         MessageProducer messageProducer = session.createProducer(topic);
         messageProducer.send(session.createTextMessage("M4"));
         connection.close();
      }

      {
         Connection connection = connectionFactory.createConnection("guest", "guest");
         connection.setClientID(clientID);
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         TopicSubscriber topicSubscriber = session.createDurableSubscriber(topic, subscriptionName, messageSelector, noLocal);
         connection.start();

         // now drain the subscription
         // we should not receive message M3, but we should receive message M4
         // However for some reason Artemis doesn't receive either
         TextMessage textMessage = (TextMessage) topicSubscriber.receive(1000);
         assertNotNull(textMessage);

         assertEquals("M4", textMessage.getText());

         assertNull(topicSubscriber.receiveNoWait());

         connection.close();
      }
   }
}
