from RagSystem.prompts.AbstractPrompts import AbstractPrompts


class DefaultPrompt(AbstractPrompts):
    def __init__(self):
        super().__init__()


    def _query_hypothetical_answers(self) -> str:
        query_hypothetical_answers_prompt = f"""
                You ar_ an AI assistant tasked with generating multiple plausible ways topics or concepts could be described in documents. 
                Do not provide a definitive answer. Instead, imagine **all possible contexts, variations, or phrasings** in which the information might appear.

                You will be given **a list of questions or topics**. For each one, generate 2 hypothetical answers in the same order.

                Rules:
                1. For each topic or question, provide **exactly 2 different hypothetical answers** that could plausibly exist in documents.
                2. Use synonyms, alternate phrasings, and related concepts when appropriate.
                3. Return only the hypothetical answers as separate bullet points or lines, **grouped by question in order**.
                """
        return query_hypothetical_answers_prompt

    def _multiple_query_answers(self,expand_by=3) -> str:
        query_expansion_prompt = f"""
              You are an assistant that rewrites user queries to improve retrieval from a document collection. 
              Your task is to expand the query with context cues so that the search system retrieves the most relevant documents, including small or overlooked documents.

              Rules:
              1. Preserve the original intent of the query.
              2. Add context keywords, document types, or hints to make it clear which document is most relevant.
              3. Include synonyms, related terms, or clarifying details that help match the right document.
              4. Keep the expanded query concise and relevant — no extra unrelated words.
              5. Output only the expanded query, nothing else.
              6. You will make exactly {expand_by} extra queries.

              """
        return query_expansion_prompt

    def _answer_context(context: str) -> str:
        prompt = f"""
            You are an AI assistant with access to private documents. Answer user questions **using ONLY the content provided below**.
            Follow these rules:
            1. Use only the information provided in the documents. Do not make assumptions or use external knowledge.
            2. If the answer is not found in the documents, say: "I could not find relevant information in the documents."
            3. Provide concise, clear, and accurate answers.
            4. You may reference the source or file name if useful.

            Context / Relevant document chunks:
            {context}    
            """
        return prompt
