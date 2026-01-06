from .OllamaLLM import OllamaLLM


class Phi3LLM(OllamaLLM):
    def __init__(self, ollama_options=None, ollama_url: str = None):
        super().__init__('phi3', ollama_options, ollama_url)
