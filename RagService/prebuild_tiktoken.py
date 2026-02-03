"""
Tiktoken initializer that uses LangChain's TokenTextSplitter to preload
tokenizer data at build time. This ensures the RAG service, which has no
internet access at runtime, can use tokenization without downloading files.
"""

import tiktoken
from langchain_text_splitters import TokenTextSplitter

# Encodings your app may need
encodings = ["cl100k_base", "gpt2"]  # add any others your RAG workflow may call

for e in encodings:
    enc = tiktoken.get_encoding(e)
    enc.encode("prebuild test")


text = "Hello! This is a test of the TokenTextSplitter using tiktoken."

splitter = TokenTextSplitter(chunk_size=10, chunk_overlap=2)

chunks = splitter.split_text(text)

print("Chunks:")
for i, chunk in enumerate(chunks):
    print(f"{i}: {chunk}")
