
package com.camel.cmlboot;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.microsoft.azure.servicebus.ExceptionPhase;
import com.microsoft.azure.servicebus.IMessage;
import com.microsoft.azure.servicebus.IMessageHandler;
import com.microsoft.azure.servicebus.Message;
import com.microsoft.azure.servicebus.MessageHandlerOptions;
import com.microsoft.azure.servicebus.QueueClient;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;

@SpringBootApplication
public class ServiceBusSampleApplication implements CommandLineRunner {

   @Autowired
   private QueueClient queueClient;
    
    public static void main(String[] args) {
        SpringApplication.run(ServiceBusSampleApplication.class);
    }
//
    public void run(String... var1){
        try {
			sendQueueMessage();
			receiveQueueMessage();
		} catch (ServiceBusException | InterruptedException e) {
			e.printStackTrace();
		}
        
    }

    private void sendQueueMessage() throws ServiceBusException, InterruptedException {
        final String messageBody = "queue message";
        System.out.println("Sending message: " + messageBody);
        final Message message = new Message(messageBody.getBytes(StandardCharsets.UTF_8));
        try {
        	queueClient.send(message);
        }catch (Exception e) {
        	e.printStackTrace();
        }
        
    }

    private void receiveQueueMessage() throws ServiceBusException, InterruptedException {
    	queueClient.registerMessageHandler(new MessageHandler(), new MessageHandlerOptions());
        TimeUnit.SECONDS.sleep(5);
//        quClient.close();
    }

    
    static class MessageHandler implements IMessageHandler {
        public CompletableFuture<Void> onMessageAsync(IMessage message) {
            final String messageString = new String(message.getBody(), StandardCharsets.UTF_8);
            System.out.println("Received message: " + messageString);
            return CompletableFuture.completedFuture(null);
        }

        public void notifyException(Throwable exception, ExceptionPhase phase) {
            System.out.println(phase + " encountered exception:" + exception.getMessage());
        }
    }
}
