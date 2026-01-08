import os

DOC_TOPIC_EXCHANGE_WORKER = "doc_topic_worker"
RAG_TOPIC_EXCHANGE_WORKER = "rag_topic_worker"
EXCHANGE_TYPE_TOPIC_WORKER = "topic"

DOC_QUEUE_WORKER = "doc_tasks_worker"
RAG_QUEUE_WORKER = "rag_tasks_worker"



DOC_TOPIC_EXCHANGE_FEEDBACK = "doc_topic_feedback"
RAG_TOPIC_EXCHANGE_FEEDBACK = "rag_topic_feedback"
EXCHANGE_TYPE_TOPIC_FEEDBACK = "topic"


RABBITMQ_HOST=os.getenv("RABBITMQ_HOST",'localhost:5672')

RABBITMQ_USER = os.getenv("RABBITMQ_USER", "guest")
RABBITMQ_PASSWORD = os.getenv("RABBITMQ_PASSWORD", "guest")
