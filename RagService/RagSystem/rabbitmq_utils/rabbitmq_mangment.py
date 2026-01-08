import time

import pika
from pika.exceptions import AMQPConnectionError

from .exchanges import (
    DOC_TOPIC_EXCHANGE_WORKER,
    RAG_TOPIC_EXCHANGE_WORKER,
    EXCHANGE_TYPE_TOPIC_WORKER,
    DOC_QUEUE_WORKER,
    RAG_QUEUE_WORKER,
    RABBITMQ_HOST,
    RABBITMQ_USER,
    RABBITMQ_PASSWORD,
)


def get_connection(host=RABBITMQ_HOST, user=RABBITMQ_USER, password=RABBITMQ_PASSWORD, heartbeat=600, blocked_timeout=300,max_retries=5,delay=5):
    attempt = 0
    while attempt < max_retries:
        try:
            credentials = pika.PlainCredentials(user, password)
            params = pika.ConnectionParameters(
                host=host,
                credentials=credentials,
                heartbeat=heartbeat,
                blocked_connection_timeout=blocked_timeout
            )
            connection = pika.BlockingConnection(params)
            print(f"[INFO] Connected to RabbitMQ at {host}")
            return connection
        except AMQPConnectionError as e:
            attempt += 1
            print(f"[WARN] Failed to connect to {host} (attempt {attempt}/{max_retries}): {e}")
            time.sleep(delay)

    raise AMQPConnectionError(f"Could not connect to RabbitMQ at {host} after {max_retries} attempts")


def create_channel(connection):
    """Create a channel from a RabbitMQ connection."""
    return connection.channel()


def declare_exchange(channel, exchange_name, exchange_type=EXCHANGE_TYPE_TOPIC_WORKER, durable=True):
    """Declare a topic exchange."""
    channel.exchange_declare(exchange=exchange_name, exchange_type=exchange_type, durable=durable)


def declare_queue(channel, queue_name, durable=True):
    """Declare a queue."""
    channel.queue_declare(queue=queue_name, durable=durable)
    return queue_name


def bind_queue(channel, queue_name, exchange_name, routing_key="#"):
    """Bind a queue to an exchange with a routing key."""
    channel.queue_bind(exchange=exchange_name, queue=queue_name, routing_key=routing_key)


def start_consuming(channel, queue_callback_map, prefetch_count=1):
    """
    Start consuming messages.

    queue_callback_map: dict of {queue_name: callback_function}
    """
    # Limit unacknowledged messages per worker
    channel.basic_qos(prefetch_count=prefetch_count)

    # Register consumers
    for queue_name, callback in queue_callback_map.items():
        channel.basic_consume(queue=queue_name, on_message_callback=callback)

    print("[*] Waiting for messages. To exit press CTRL+C")
    try:
        channel.start_consuming()
    except KeyboardInterrupt:
        print("Interrupted by user, stopping consumer...")
        channel.stop_consuming()

