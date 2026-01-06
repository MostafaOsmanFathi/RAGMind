import os
from http.client import responses

import AbstractLLM
import ollama

class OllamaLLM(AbstractLLM):
    def __init__(self,llm_model_name,ollama_url:str=None) :
        super().__init__(llm_model_name)
        self.ollama_url = ollama_url
        if self.ollama_url is None:
            os.environ['OLLAMA_URL'] = ollama_url


        self.ollama_client=ollama.Client(self.ollama_url)


    def chat_query(self,message: list[dict]) -> str:
        response=self.ollama_client.chat(model=self.llm_model_name, messages=message)
        return response["message"].get("content")

    def generate_query(self, prompt:str) -> str:
        response=self.ollama_client.generate(model=self.llm_model_name, prompt=prompt)
        return response["message"].get("content")