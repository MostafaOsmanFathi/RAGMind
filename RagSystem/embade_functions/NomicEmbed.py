from RagSystem.embade_functions.OllamaEmbed import OllamaEmbed


class NomicEmbed(OllamaEmbed):
    def __init__(self,ollama_url):
        super().__init__('nomic-embed-text',ollama_url)
