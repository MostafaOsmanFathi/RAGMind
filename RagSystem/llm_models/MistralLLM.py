from RagSystem.llm_models.OllamaLLM import OllamaLLM


class MistralLLM(OllamaLLM):
    def __init__(self,ollama_options=None,ollama_url:str=None):
        super().__init__('mistral',ollama_options,ollama_url)
