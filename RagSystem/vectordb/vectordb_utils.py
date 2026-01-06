import re
import hashlib

def chunk_document(
    doc: str,
    file_id: str,
    max_words: int = 400,
    overlap_words: int = 80
) -> list[dict]:
    """
    RAG-friendly chunking:
    - paragraph-aware
    - sentence-safe
    - overlapping
    """

    # Normalize whitespace
    doc = re.sub(r'\n{2,}', '\n\n', doc.strip())

    paragraphs = doc.split("\n\n")
    chunks = []

    current_chunk = []
    current_len = 0
    chunk_count = 1

    def flush_chunk(overlap=0):
        nonlocal chunk_count
        if not current_chunk:
            return

        text = " ".join(current_chunk)
        chunks.append({
            "id": f"{file_id}_chunk_{chunk_count}",
            "text": text
        })
        chunk_count += 1

        if overlap > 0:
            return current_chunk[-overlap:]
        return []

    for para in paragraphs:
        para = para.strip()
        if not para:
            continue

        words = para.split()

        # If paragraph fits, add it directly
        if current_len + len(words) <= max_words:
            current_chunk.append(para)
            current_len += len(words)
            continue

        # Otherwise split paragraph into sentences
        sentences = re.split(r'(?<=[.!?])\s+', para)

        for sent in sentences:
            sent_words = sent.split()

            if current_len + len(sent_words) > max_words:
                overlap = overlap_words // max(1, len(sent_words))
                current_chunk = flush_chunk(overlap=overlap)
                current_len = sum(len(s.split()) for s in current_chunk)

            current_chunk.append(sent)
            current_len += len(sent_words)

    if current_chunk:
        flush_chunk()

    return chunks


def generate_file_id(file_path: str) -> str:
    """
    Compute a short, deterministic file ID.
    """
    hash_func = hashlib.md5()  # fast, small
    with open(file_path, "rb") as f:
        for chunk in iter(lambda: f.read(4096), b""):
            hash_func.update(chunk)
    return hash_func.hexdigest()  # 32-char hex string


