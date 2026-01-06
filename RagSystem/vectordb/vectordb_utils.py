import hashlib

def chunk_document(doc: str, file_id: str, chunk_size: int = 150, chunk_overlap: int = 15) -> list[dict]:
    words = doc.split()
    chunks = []
    step = chunk_size - chunk_overlap
    for cnt, i in enumerate(range(0, len(words), step)):
        chunk_text = " ".join(words[i:i + chunk_size])
        chunks.append({'id': f'{file_id}_chunk_{cnt + 1}', 'text': chunk_text})
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


