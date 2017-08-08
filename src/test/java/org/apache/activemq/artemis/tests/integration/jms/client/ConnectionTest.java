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
package org.apache.activemq.artemis.tests.integration.jms.client;

import org.junit.After;
import org.junit.Test;

import javax.jms.*;

import org.apache.activemq.JMSTestBase;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.jgroups.util.Util.assertEquals;

@RunWith(Parameterized.class)
public class ConnectionTest extends JMSTestBase {

   private Connection conn;

   @Test
   public void testTXTypeInvalid() throws Exception {
      conn = cf.createConnection();

      Session sess = conn.createSession(false, Session.SESSION_TRANSACTED);

      assertEquals(Session.AUTO_ACKNOWLEDGE, sess.getAcknowledgeMode());

      sess.close();

      TopicSession tpSess = ((TopicConnection) conn).createTopicSession(false, Session.SESSION_TRANSACTED);

      assertEquals(Session.AUTO_ACKNOWLEDGE, tpSess.getAcknowledgeMode());

      tpSess.close();

      QueueSession qSess = ((QueueConnection) conn).createQueueSession(false, Session.SESSION_TRANSACTED);

      assertEquals(Session.AUTO_ACKNOWLEDGE, qSess.getAcknowledgeMode());

      qSess.close();

   }

   @After
   public void tearDown() throws Exception {
      if (conn != null) {
         conn.close();
      }
   }
}
