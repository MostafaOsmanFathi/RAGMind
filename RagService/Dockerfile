FROM python:3.12.12

COPY requirements.txt .

RUN pip install -f requirements.txt

COPY . .

ENTRYPOINT[ "python3" , "app.py"]