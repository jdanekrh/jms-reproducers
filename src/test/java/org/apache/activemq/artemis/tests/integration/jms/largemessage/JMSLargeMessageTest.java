/*
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
package org.apache.activemq.artemis.tests.integration.jms.largemessage;

import org.apache.activemq.JMSTestBase;
import org.apache.activemq.artemis.utils.UUIDGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.jms.*;
import java.math.BigInteger;
import java.util.Random;

@RunWith(value = Parameterized.class)
public class JMSLargeMessageTest extends JMSTestBase {
   // Constants -----------------------------------------------------

   // Attributes ----------------------------------------------------

   Queue queue1;
   Random random = new Random();

   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------

   // Public --------------------------------------------------------

   @Override
   @Before
   public void setUp() throws Exception {
      super.setUp();
      String randomSuffix = new BigInteger(130, random).toString(32);
      queue1 = createQueue("queue1_" + randomSuffix);
//      queue1 = createQueue("queue1_");
   }

   @Test(timeout = 30000)
   public void testSmallString() throws Exception {
      sendStringOfSize(1024);
   }

   @Test(timeout = 30000)
   public void testHugeString() throws Exception {
      sendStringOfSize(1024 * 1024);
   }

   private void sendStringOfSize(int msgSize) throws JMSException {
      Connection conn = cf.createConnection();

      Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

      MessageProducer prod = session.createProducer(queue1);

      TextMessage m = session.createTextMessage();

      // TODO: this is extra compared to original test, hoping maybe it gives me the JMSException I crave
      m.setJMSDeliveryMode(DeliveryMode.PERSISTENT);

      StringBuffer buffer = new StringBuffer();
      while (buffer.length() < msgSize) {
         buffer.append(UUIDGenerator.getInstance().generateStringUUID());
      }

      final String originalString = buffer.toString();

      m.setText(originalString);

      buffer = null;

      prod.send(m);

      conn.close();

//      validateNoFilesOnLargeDir(server.getConfiguration().getLargeMessagesDirectory(), 1);

      conn = cf.createConnection();

      session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

      MessageConsumer cons = session.createConsumer(queue1);

      conn.start();

      TextMessage rm = (TextMessage) cons.receive(10000);
      Assert.assertNotNull(rm);

      String str = rm.getText();
      Assert.assertEquals(originalString, str);
      conn.close();
//      validateNoFilesOnLargeDir(server.getConfiguration().getLargeMessagesDirectory(), 0);

   }

}
