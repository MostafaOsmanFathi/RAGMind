import os

from langchain_chroma import Chroma
from langchain_core.runnables import RunnablePassthrough, RunnableLambda

from RagService.rag_langchain_core.configs.configBase import ConfigBase


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

    def get_retrival_chain(self,number_of_results:int):
        return self._vector_store.as_retriever(search_kwargs={"k": number_of_results})


