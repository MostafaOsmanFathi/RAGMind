import os

import chromadb


import os
import chromadb


class ChromadbClient:
    _instance = None

    def __new__(cls, chromadb_persistent_client_path: str = None):
        if cls._instance is None:
            instance = super().__new__(cls)

            # ---- one-time initialization SingleTone----
            if chromadb_persistent_client_path is None:
                chromadb_persistent_client_path = os.getenv(
                    "CHROMADB_PERSISTENT_CLIENT_PATH",
                    "chromadb_data"
                )

            instance.chromadb_persistent_client_path = chromadb_persistent_client_path
            instance.chromadb_client = chromadb.PersistentClient(
                path=chromadb_persistent_client_path
            )

            cls._instance = instance

        return cls._instance

    def load_client(self):
        return self.chromadb_client