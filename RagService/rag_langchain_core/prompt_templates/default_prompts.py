from langchain_core.prompts import  ChatPromptTemplate


query_expansion_prompt = ChatPromptTemplate.from_messages([
    (
        "system",
        "You are an AI assistant that rewrites user queries to improve document retrieval. "
        "You must follow the rules strictly and output only the rewritten queries."
    ),
    (
        "human",
        """
Rewrite the following query into exactly {expand_by} variants.

Rules:
1. Preserve the original intent.
2. Add relevant keywords, synonyms, or clarifying hints.
3. Keep each variant concise.
4. Output exactly {expand_by} queries, one per line.
5. Do NOT add explanations or extra text.

User Query:
{question}
"""
    )
])



hallucination_answers_prompt = ChatPromptTemplate.from_messages([
    (
        "system",
        "You generate hypothetical document content to help retrieval. "
        "Do NOT provide factual or definitive answers."
    ),
    (
        "human",
        """
Generate plausible ways the following topics or queries could appear in documents.

Instructions:
1. The input contains multiple queries, one per line.
2. For each query, generate 1–2 hypothetical answers.
3. Use synonyms, paraphrases, or related concepts.
4. Output each query followed immediately by its hypothetical answers.
5. Separate each query block with a single newline.
6. Do NOT add explanations, commentary, or extra text.
7. Use bullet points or separate lines for multiple answers.

Queries:
{questions}
"""
    )
])


answer_context_prompt = ChatPromptTemplate.from_messages([
    (
        "system",
        "You are an AI assistant with access to private documents. "
        "You must answer using ONLY the provided context. "
        "Do not infer, guess, or use outside knowledge."
    ),
    (
        "human",
        """
Context:
{context}

Question:
{question}

Rules:
- If the answer is not explicitly stated in the context, respond exactly:
  "I could not find relevant information in the documents."
- Keep the answer concise and accurate.
"""
    )
])