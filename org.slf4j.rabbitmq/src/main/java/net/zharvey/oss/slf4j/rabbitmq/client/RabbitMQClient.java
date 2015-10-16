package net.zharvey.oss.slf4j.rabbitmq.client;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.impl.AMQConnection;

public class RabbitMQClient {
	private RabbitMQConfig config;
	
	private AMQP.BasicProperties rabbitMQProperties;
	
	public RabbitMQClient() {
		super();
		
		AMQP.BasicProperties.Builder bob = new AMQP.BasicProperties.Builder();
		AMQP.BasicProperties persistentTextPlain = bob.contentType("text/plain").build();
		setRabbitMQProperties(persistentTextPlain);
	}
	
	public void send(String level, String message) {i
		// TODO Refactor - ConnectionFactory should be initialized in the constructor
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername(config.getUsername());
		factory.setPassword(config.getPassword());
		factory.setVirtualHost(config.getVirtualHost());
		
		// TODO Improve - Thread pool should be in static
		ExecutorService executors = Executors.newCachedThreadPool();
		
		// TODO Refactor - Address should be initiliazed in the constructor
		Address address = new Address(config.getHostName(), Integer.valueOf(config.getPortNumber()));
		Address[] addresses = new Address[1];
		addresses[0] = address;
		
		AMQConnection conn = null;
		Channel channel = null;
		try {
			// TODO Improve - Very costly to open a new connection for every line of log.
			conn = (AMQConnection)factory.newConnection(executors, addresses);
			channel = conn.createChannel();
			//channel.basicPublish(config.getExchange() + "-" + level, config.getRoutingKey(), rabbitMQProperties, message.getBytes());
			channel.basicPublish(config.getExchange(), config.getRoutingKey(), null, (level + "-" + message).getBytes(StandardCharsets.UTF_8));
		} catch (IOException ioExc) {
			throw new RuntimeException(ioExc);
		} finally {
			// Close connections
			try {
				if (channel != null) {
					channel.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public RabbitMQConfig getConfig() {
		return config;
	}

	public void setConfig(RabbitMQConfig config) {
		this.config = config;
	}

	public AMQP.BasicProperties getRabbitMQProperties() {
		return rabbitMQProperties;
	}

	public void setRabbitMQProperties(AMQP.BasicProperties rabbitMQProperties) {
		this.rabbitMQProperties = rabbitMQProperties;
	}
}
