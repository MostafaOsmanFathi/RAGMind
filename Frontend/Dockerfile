FROM node:lts-alpine3.23 AS ngbuild

LABEL org.opencontainers.image.authors="Mostafa Osman <mostafa.osman.fathi@gmail.com>" \
      org.opencontainers.image.source="https://github.com/MostafaOsmanFathi/RAGMind" \
      org.opencontainers.image.version="v1.10" \
      org.opencontainers.image.licenses="MIT"

LABEL org.opencontainers.image.title="frontend" \
      org.opencontainers.image.description="Web frontend application providing user interface for the RAG system"

WORKDIR /app

COPY package*.json ./

RUN npm install --legacy-peer-deps --no-audit --no-fund

COPY . .

RUN npm run build --prod

FROM node:lts-alpine3.23

RUN npm install --global serve

WORKDIR /app

COPY --from=ngbuild /app/dist/Frontend/browser /app


EXPOSE 5000

CMD ["serve", "-s", "/app" ,"-p", "5000"]