from click import prompt

from llm_models.MistralLLM import MistralLLM
from prompts.DefaultPrompt import DefaultPrompt
from  vectordb.ChromadbManger import ChromadbManger
from embade_functions.NomicEmbed import NomicEmbed


class RagSystem:
    db_collection_root = 'chromadb_root'

    def __init__(self,
                 db_collection_name: str,
                 embedding_fun = NomicEmbed().embedding_fn,
                 llm_model=MistralLLM(),
                prompt_manger=DefaultPrompt(),
                 n_results_query:int=5):

        self.embedding_fun = embedding_fun
        self.vectordb=ChromadbManger(RagSystem.db_collection_root,db_collection_name,n_results_query,self.embedding_fun)
        self.prompt_manger=prompt_manger
        self.llm_model=llm_model


    def add_document(self,document_path:str):
        self.vectordb.add_document(document_path)


    def ask_question(self, question: str) -> str:
        try:

            relevant_chunks = self.vectordb.query_document(question)

            llm_response = self.llm_model.chat_query(message=self.prompt_manger.answer_context(question=question,context=relevant_chunks))
            return llm_response

        except Exception as e:
            raise e
