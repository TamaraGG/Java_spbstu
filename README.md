## Step 7: Implement Messaging (RabbitMQ, Kafka, Artemis, Pulsar, ActiveMQ, NATS…)
- Set up RabbitMQ or Kafka or any other message broker. (RabbitMQ is a bit simpler, Kafka is faster)
- Publish a message when a new task is created.
- Remake the Notification service to receive updates !! ONLY !! from the message broker.
- Create a listener to process messages asynchronously.
