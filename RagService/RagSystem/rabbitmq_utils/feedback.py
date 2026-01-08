import pika
from .exchanges import DOC_TOPIC_EXCHANGE_FEEDBACK, RAG_TOPIC_EXCHANGE_FEEDBACK, EXCHANGE_TYPE_TOPIC_FEEDBACK
from .message_models import DocFeedback, RAGFeedback
from .rabbitmq_mangment import get_connection,create_channel

def send_doc_feedback(feedback: DocFeedback, host_url: str):
    connection = get_connection()
    channel = create_channel(connection)

    # Declare exchange
    channel.exchange_declare(exchange=DOC_TOPIC_EXCHANGE_FEEDBACK, exchange_type=EXCHANGE_TYPE_TOPIC_FEEDBACK, durable=True)

    routing_key = f"doc.{feedback.action}.{feedback.backend_id}.{DocFeedback.status}"  # e.g., doc.add.backend_1
    channel.basic_publish(
        exchange=DOC_TOPIC_EXCHANGE_FEEDBACK,
        routing_key=routing_key,
        body=feedback.model_dump_json(),
        properties=pika.BasicProperties(delivery_mode=2)
    )
    connection.close()


def send_rag_feedback(feedback: RAGFeedback, host_url: str):
    connection = get_connection()
    channel = create_channel(connection)

    # Declare exchange
    channel.exchange_declare(exchange=RAG_TOPIC_EXCHANGE_FEEDBACK, exchange_type=EXCHANGE_TYPE_TOPIC_FEEDBACK, durable=True)

    routing_key = f"rag.query.{feedback.backend_id}.{feedback.status}"  # e.g., rag.query.backend_1
    channel.basic_publish(
        exchange=RAG_TOPIC_EXCHANGE_FEEDBACK,
        routing_key=routing_key,
        body=feedback.model_dump_json(),
        properties=pika.BasicProperties(delivery_mode=2)
    )
    connection.close()
