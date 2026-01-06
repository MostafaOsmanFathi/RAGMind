from .OllamaEmbed import OllamaEmbed


class NomicEmbed(OllamaEmbed):
    def __init__(self,ollama_url=None):
        super().__init__('nomic-embed-text',ollama_url)
