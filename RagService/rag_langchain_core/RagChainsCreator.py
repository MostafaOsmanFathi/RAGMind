import os

from langchain_chroma import Chroma
from langchain_core.runnables import RunnablePassthrough, RunnableLambda

from RagService.rag_langchain_core.configs.configBase import ConfigBase
from RagService.rag_langchain_core.prompt_templates import default_prompts

class RagChainsCreator:
    def __init__(self,config:ConfigBase,collection_name:str):
        self._config = config
        self._collection_name = collection_name

        self._vector_store= Chroma(embedding_function=config.embeddings,
                                   collection_name=self._collection_name,
                                   persist_directory=os.getenv('CHROMADB_PERSISTENT_CLIENT_PATH',
                                                               './chroma_langchain_db')
                                   )

    def get_add_doc_chain(self):
        return (
                {
                    "file_path": RunnablePassthrough(),
                    "vector_store": RunnableLambda(lambda temp:self._vector_store)
                }
                | RunnableLambda(self._config.get_file_hash)
                | RunnableLambda(self._config.check_exists)
                | RunnableLambda(self._config.extract_file_name)
                | RunnableLambda(self._config.split_into_chunks)
                | RunnableLambda(self._config.chunks_meta_data)
                | RunnableLambda(self._config.add_documents_to_store)
        )

    def get_retrival_chain(self,num_retrival_results:int):
        return self._vector_store.as_retriever(search_kwargs={"k": num_retrival_results})



    def ask_rag_chain(self,num_retrival_results=2,expand_query_by=3):
        return (
            {
                "question": RunnablePassthrough(),
                "context":
                    RunnablePassthrough()
                    | RunnableLambda(lambda q:{'question':q,'expand_by':expand_query_by})
                    | default_prompts.query_expansion_prompt | self._config.llm
                    | RunnableLambda(lambda result: {'questions':result.content})
                    | default_prompts.hallucination_answers_prompt | self._config.llm
                    | RunnableLambda(lambda result: result.content)
                    | self.get_retrival_chain(num_retrival_results)
            }| default_prompts.answer_context_prompt
        )|self._config.llm

