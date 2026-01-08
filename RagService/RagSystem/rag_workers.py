from hashlib import md5

from .RagSystem import RagSystem
from .embade_functions.NomicEmbed import NomicEmbed
from .llm_models.MistralLLM import MistralLLM
from .llm_models.Phi3LLM import Phi3LLM
from .prompts.DefaultPrompt import DefaultPrompt

from .rabbitmq_utils.exchanges import RABBITMQ_HOST
from .rabbitmq_utils.feedback import send_rag_feedback, send_doc_feedback
from .rabbitmq_utils.message_models import RAGWorkerMessage, RAGFeedback, DocumentWorkerMessage, RagInit, DocFeedback


def get_rag(message:RagInit):
    # select embedding function
    if message.embed_model in (None, "nomic-embed-text"):
        embedding_fun = NomicEmbed().embedding_fn
        embed_model_name = "nomic-embed-text"
    else:
        raise ValueError(f"Unsupported embed model: {message.embed_model}")

    # select LLM
    if message.llm_model == "mistral":
        llm_model = MistralLLM()
    elif message.llm_model == "phi3":
        llm_model = Phi3LLM()
    else:
        raise ValueError(f"Unsupported LLM model: {message.llm_model}")

    combine = (
            message.backend_id
            + message.user_id
            + message.collection_name
            + message.llm_model
            + embed_model_name
    )

    db_collection_name = md5(combine.encode()).hexdigest()

    rag = RagSystem(
        db_collection_name=db_collection_name,
        embedding_fun=embedding_fun,
        llm_model=llm_model,
        prompt_manger=DefaultPrompt(),
    )
    return rag



def worker_rag_query(ch, method, properties, body):
    try:
        message = RAGWorkerMessage.model_validate_json(body)

        rag=get_rag(message)

        response = rag.ask_question(message.question)

        # Feedback Backend Response
        feedback = RAGFeedback(
            backend_id=message.backend_id,
            user_id=message.user_id,
            collection_name=message.collection_name,
            response=response,
            llm_model=message.llm_model
        )

        feedback.status='success'

        send_rag_feedback(feedback, host_url=RABBITMQ_HOST)

        # ACK confirm task success
        ch.basic_ack(delivery_tag=method.delivery_tag)

    except Exception as e:
        print("RAG worker error:", e)
        feedback = RAGFeedback.model_validate_json(body)
        feedback.status='failed'
        send_rag_feedback(feedback, host_url=RABBITMQ_HOST)
        # ACK confirm task failed
        ch.basic_ack(delivery_tag=method.delivery_tag)



def worker_add_doc_query(ch, method, properties, body):
    try:
        message = DocumentWorkerMessage.model_validate_json(body)

        rag=get_rag(message)

        if not rag.add_document(message.file_path):
            raise Exception(f"Failed to add document: {message.file_path}")

        feedback = DocFeedback(
            backend_id=message.backend_id,
            user_id=message.user_id,
            collection_name=message.collection_name,
            llm_model=message.llm_model,
            action=message.action
        )

        feedback.status = 'success'
        send_doc_feedback(feedback, host_url=RABBITMQ_HOST)

        ch.basic_ack(delivery_tag=method.delivery_tag)

    except Exception as e:
        print("RAG worker error:", e)
        feedback = DocFeedback.model_validate_json(body)
        feedback.status = 'failed'
        send_doc_feedback(feedback, host_url=RABBITMQ_HOST)
        ch.basic_ack(delivery_tag=method.delivery_tag)

