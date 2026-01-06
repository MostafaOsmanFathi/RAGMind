from typing import List
from .AbstractPrompts import AbstractPrompts


class DefaultPrompt(AbstractPrompts):
    def __init__(self):
        super().__init__()

    def query_hypothetical_answers(self, question: str) -> List[dict]:
        """
        Generate multiple plausible ways topics or concepts could be described in documents.
        Each topic/question should produce exactly 2 hypothetical answers using synonyms,
        alternate phrasings, or related concepts.
        """
        prompt = f"""
You are an AI assistant tasked with generating multiple plausible ways topics or concepts could appear in documents. 
Do NOT provide definitive answers. Instead, imagine **all possible contexts, variations, or phrasings**.

For each topic or question in the list provided, generate exactly 2 hypothetical answers, in the same order as the questions.

Rules:
1. Provide **exactly 2 different hypothetical answers** per topic/question.
2. Use synonyms, alternate phrasings, or related concepts when appropriate.
3. Return only the hypothetical answers as separate bullet points or lines, **grouped by question in order**.
"""
        return self._parse_prompt(prompt, question)

    def multiple_query(self, question: str, expand_by: int = 3) -> List[dict]:
        """
        Expand the user query with context cues to improve document retrieval.
        The output should be exactly `expand_by` extra queries that preserve intent
        and add relevant keywords or synonyms.
        """
        prompt = f"""
You are an assistant that rewrites user queries to improve retrieval from a document collection. 
Your task is to expand the query so that the search system retrieves the most relevant documents, 
including smaller or less obvious ones.

Rules:
1. Preserve the original intent of the query.
2. Add context keywords, document types, or hints for better relevance.
3. Include synonyms, related terms, or clarifying details.
4. Keep the expanded query concise and relevant — no unrelated words.
5. Output only the expanded queries, nothing else.
6. Generate exactly {expand_by} extra queries.
"""
        return self._parse_prompt(prompt, question)

    def answer_context(self, question: str, context: str) -> List[dict]:
        """
        Answer a question using ONLY the provided context.
        Must reference the source if useful and explicitly state when information is missing.
        """
        prompt = f"""
You are an AI assistant with access to private documents. Answer user questions **using ONLY the content provided below**.
Follow these rules:
1. Use only the information provided in the documents. Do NOT make assumptions or use external knowledge.
2. If the answer is not found in the documents, respond: "I could not find relevant information in the documents."
3. Provide concise, clear, and accurate answers.
4. You may reference the source or file name if it helps clarity.

Context / Relevant document chunks:
{context}
"""
        return self._parse_prompt(prompt, question)
