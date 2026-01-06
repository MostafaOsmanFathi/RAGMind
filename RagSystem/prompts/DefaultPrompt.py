from typing import List
from .AbstractPrompts import AbstractPrompts


class DefaultPrompt(AbstractPrompts):
    def __init__(self):
        super().__init__()

    def query_hypothetical_answers(self, questions: str) -> List[dict]:
        """
        Generate plausible variations (hypothetical answers) for multiple queries at once.
        Each query should produce 1–2 answers using synonyms, paraphrases, or related concepts.
        The output format pairs each query with its answers, separated by a newline.
        """
        prompt = f"""
You are an AI assistant tasked with generating plausible ways topics or concepts could appear in documents.
Do NOT provide definitive answers. Imagine all possible contexts, variations, or phrasings.

Instructions:
1. The input is a list of queries, one per line.
2. For each query, generate 1–2 hypothetical answers that might appear in documents.
3. Use synonyms, paraphrases, or related concepts.
4. Output each query immediately followed by its hypothetical answers.
5. Separate each query block (query + answers) by a single newline.
6. Do NOT include explanations, commentary, or extra text.
7. Use bullet points or separate lines for multiple answers per query.
"""
        return self._parse_prompt(prompt, questions)

    def multiple_query(self, question: str, expand_by: int = 3) -> List[dict]:
        """
        Expand a user query into multiple variants for document retrieval.
        All expansions are returned at once to reduce LLM calls.
        """
        prompt = f"""
You are an AI assistant that rewrites a user query to improve document retrieval.
Expand the query into {expand_by} variants that preserve intent, add relevant keywords, synonyms, or clarifying hints, 
and improve retrieval coverage.

Rules:
1. Keep each expanded query concise and relevant.
2. Include terms that increase matching with documents.
3. Output exactly {expand_by} expanded queries, one per line.
4. Separate each query by a newline.
5. Do NOT add explanations, commentary, or extra text.
"""
        return self._parse_prompt(prompt, question)

    def answer_context(self, question: str, context: str) -> List[dict]:
        """
        Answer a user question using ONLY the provided context.
        Hypothetical answers should NOT be included here.
        """
        prompt = f"""
You are an AI assistant with access to private documents. Answer the user question **using ONLY the content below**.

Rules:
1. Do NOT assume anything not present in the context.
2. If the answer is not found, respond: "I could not find relevant information in the documents."
3. Provide concise, accurate answers.
4. You may reference the source or file name if useful.

Context / Relevant document chunks:
{context}
"""
        return self._parse_prompt(prompt, question)
