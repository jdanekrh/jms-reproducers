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
package org.apache.activemq.artemis.tests.integration.jms.jms2client;

import org.apache.activemq.JMSTestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.jms.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(value = Parameterized.class)
public class BodyTest extends JMSTestBase {

   private static final String Q_NAME = "SomeQueue";
   private javax.jms.Queue queue;

   @Test
   public void testBodyConversion() throws Throwable {
      try (
         Connection conn = cf.createConnection();
      ) {
         Session sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
         queue = sess.createQueue(Q_NAME);
         MessageProducer producer = sess.createProducer(queue);

         MessageConsumer cons = sess.createConsumer(queue);
         conn.start();

         BytesMessage bytesMessage = sess.createBytesMessage();
         System.out.println("message type is: " + bytesMessage.getJMSType());
         bytesMessage.getBody(String.class);
         producer.send(bytesMessage);

         Message msg = cons.receive(500);
         assertNotNull(msg);

         try {
            System.out.println("message type is: " + msg.getJMSType());
            msg.getBody(String.class);
            fail("Exception expected");
         } catch (MessageFormatException e) {
         }
      }

   }
}
