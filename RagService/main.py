from dotenv import load_dotenv
import os

load_dotenv(verbose=True)
OLLAMA_URL = os.getenv("OLLAMA_URL")

from RagService.RagSystem import RagSystem

def main():
    print("DEBUG OLLAMA_URL =", os.getenv("OLLAMA_URL"))
    reg = RagSystem('first_collection')

    for doc in os.listdir('docs'):
        if doc:
            reg.add_document(f'docs/{str(doc)}')

    res = reg.ask_question("people centered approaches?")
    print('=============')
    print(res)

if __name__ == '__main__':
    main()
