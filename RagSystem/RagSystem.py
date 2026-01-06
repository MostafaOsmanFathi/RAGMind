from click import prompt

from llm_models.MistralLLM import MistralLLM
from prompts.DefaultPrompt import DefaultPrompt
from vectordb.ChromadbManger import ChromadbManger
from embade_functions.NomicEmbed import NomicEmbed


class RagSystem:
    db_collection_root = 'chromadb_root'

    def __init__(self,
                 db_collection_name: str,
                 embedding_fun = NomicEmbed().embedding_fn,
                 llm_model=MistralLLM(),
                prompt_manger=DefaultPrompt(),
                 n_results_query:int=6):

        self.embedding_fun = embedding_fun
        self.vectordb=ChromadbManger(RagSystem.db_collection_root,db_collection_name,n_results_query,self.embedding_fun)
        self.prompt_manger=prompt_manger
        self.llm_model=llm_model


    def add_document(self,document_path:str):
        self.vectordb.add_document(document_path)


    def ask_question(self, question: str) -> str:
        try:
            message = self.prompt_manger.multiple_query(question=question, expand_by=3)
            expanded_queries_text = self.llm_model.chat_query(message=message)
            expanded_queries = [q.strip() for q in expanded_queries_text.split('\n') if q.strip()]

            queries_for_hypothetical = "\n".join(expanded_queries)
            message = self.prompt_manger.query_hypothetical_answers(questions=queries_for_hypothetical)
            hypothetical_response_text = self.llm_model.chat_query(message=message)
            hypothetical_queries = [line.strip() for line in hypothetical_response_text.split('\n') if line.strip()]

            all_queries = expanded_queries + hypothetical_queries
            all_chunks = []
            for q in all_queries:
                chunks = self.vectordb.query_document(q)  # single string per query
                all_chunks.extend(chunks)

            unique_chunks = list(dict.fromkeys(all_chunks))
            context = "\n".join(unique_chunks)

            print(context)
            message = self.prompt_manger.answer_context(question=question, context=context)
            llm_response = self.llm_model.chat_query(message=message)

            return llm_response

        except Exception as e:
            raise e
