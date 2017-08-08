package org.apache.activemq;

import org.apache.activemq.artemis.api.core.RoutingType;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQTopic;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.apache.qpid.jms.JmsTopic;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runners.Parameterized;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Topic;
import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.truth.TruthJUnit.assume;

public class JMSTestBase {
   private static String NETTY_CONNECTOR_FACTORY = NettyConnectorFactory.class.getCanonicalName();;

   @Parameterized.Parameters(name = "protocol={0}")
   public static Collection getParameters() {
      Object[] protocols = new Object[]{"core", "openwire", "amqp"};

      ArrayList<Object[]> objects = new ArrayList<>();
      for (Object p : protocols) {
         objects.add(new Object[]{p});
      }
      return objects;
   }

   @Parameterized.Parameter
   public String protocol;

   protected ConnectionFactory cf;

   @Before
   public void setUp() throws Exception {
      cf = newConnectionFactory();
   }

   static ClientSession clientSession;

   @BeforeClass
   public static void setUpClass() throws Exception {
      clientSession = ActiveMQClient.createServerLocatorWithoutHA(new TransportConfiguration(NETTY_CONNECTOR_FACTORY)).createSessionFactory().createSession();
   }

   protected ConnectionFactory newConnectionFactory() throws Exception {
      switch (protocol) {
         case "core": {
            ConnectionFactory factory = new ActiveMQConnectionFactory();
            ((ActiveMQConnectionFactory) factory).setBlockOnAcknowledge(true);
            return factory;
         }
         case "amqp": {
            final JmsConnectionFactory factory = new JmsConnectionFactory("amqp://localhost:61616");
            factory.setForceAsyncAcks(true);
            return factory;
         }
         default:
            org.apache.activemq.ActiveMQConnectionFactory cf = new org.apache.activemq.ActiveMQConnectionFactory("tcp://localhost:61616?wireFormat.cacheEnabled=true");
            cf.setSendAcksAsync(false);
            return cf;
      }
   }

    protected final JMSContext createContext() {
      assume().that(protocol).isNotEqualTo("openwire");

      return cf.createContext();
   }

   protected Topic createTopic(final String topicName) throws Exception {
      SimpleString address = SimpleString.toSimpleString(topicName);
      clientSession.createAddress(address, RoutingType.MULTICAST, false);
      return new ActiveMQTopic("topicName");  // artemis-jms-client insists on its Topic class, others are more accepting
   }
}
