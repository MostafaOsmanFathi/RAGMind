import threading

from .rabbitmq_utils.rabbitmq_mangment import get_connection, create_channel, declare_exchange, declare_queue, \
    bind_queue, start_consuming

from .rabbitmq_utils.exchanges import *
from . rag_workers import worker_rag_query,worker_add_doc_query




def doc_listener(concurrent_tasks=5):
    connection = get_connection()
    channel = create_channel(connection)
    declare_exchange(channel=channel,
                     exchange_name=DOC_TOPIC_EXCHANGE_WORKER,
                     exchange_type=EXCHANGE_TYPE_TOPIC_WORKER,
                     durable=True)

    declare_queue(channel=channel,
                  queue_name=DOC_QUEUE_WORKER,
                  durable=True)

    bind_queue(channel=channel,
               exchange_name=DOC_TOPIC_EXCHANGE_WORKER,
               queue_name=DOC_QUEUE_WORKER,
               routing_key='doc.*')

    queue_callback_map = {
        DOC_QUEUE_WORKER: worker_add_doc_query
    }

    start_consuming(channel=channel,
                    queue_callback_map=queue_callback_map,
                    prefetch_count=concurrent_tasks)

def rag_listener(concurrent_tasks=2):
    connection = get_connection()
    channel = create_channel(connection)
    declare_exchange(channel=channel,
                     exchange_name=RAG_TOPIC_EXCHANGE_WORKER,
                     exchange_type=EXCHANGE_TYPE_TOPIC_WORKER,
                     durable=True)

    declare_queue(channel=channel,
                  queue_name=RAG_QUEUE_WORKER,
                  durable=True)

    bind_queue(channel=channel,
               exchange_name=RAG_TOPIC_EXCHANGE_WORKER,
               queue_name=RAG_QUEUE_WORKER,
               routing_key='rag.*')

    queue_callback_map = {
        RAG_QUEUE_WORKER: worker_rag_query
    }

    start_consuming(channel=channel,
                    queue_callback_map=queue_callback_map,
                    prefetch_count=concurrent_tasks)

def start_listener(concurrent_raq_workers=2,concurrent_doc_workers=5):
    t1 = threading.Thread(target=rag_listener, args=(concurrent_raq_workers,))
    t2 = threading.Thread(target=doc_listener, args=(concurrent_doc_workers,))

    t1.start()
    t2.start()

    t1.join()
    t2.join()

if __name__ == '__main__':
    start_listener()