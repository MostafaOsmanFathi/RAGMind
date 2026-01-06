import os

from .AbstractLLM import AbstractLLM
import ollama


class OllamaLLM(AbstractLLM):
    def __init__(self,llm_model_name,ollama_options=None,ollama_url:str=None) :
        super().__init__(llm_model_name)
        self.ollama_url = ollama_url
        if self.ollama_url is None:
            self.ollama_url=os.getenv("OLLAMA_URL",None)

        self.ollama_options=ollama_options
        self.ollama_client=ollama.Client(host=self.ollama_url)


    def chat_query(self,message: list[dict]) -> str:
        response=self.ollama_client.chat(model=self.llm_model_name, messages=message,options= self.ollama_options)
        return response["message"].get("content")

    def generate_query(self, prompt:str) -> str:
        response=self.ollama_client.generate(model=self.llm_model_name, prompt=prompt,options= self.ollama_options)
        return response["message"].get("content")