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
import java.util.Random;

@RunWith(value = Parameterized.class)
public class JmsContextTest extends JMSTestBase {

   private JMSContext context;
   private final Random random = new Random();
   private Queue queue1;

   @Override
   @Before
   public void setUp() throws Exception {
      super.setUp();
      context = createContext();
      queue1 = createQueue(JmsContextTest.class.getSimpleName() + "Queue");
   }

   @Test(expected = JMSRuntimeException.class)
   public void testInvalidSessionModesValueMinusOne() {
      context.createContext(-1);
   }

   @Test(expected = JMSRuntimeException.class)
   public void testInvalidSessionModesValue4() {
      context.createContext(4);
   }
}
