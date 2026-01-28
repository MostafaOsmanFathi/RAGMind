FROM node:lts-alpine3.23 AS ngbuild

LABEL authors="MostafaOsman"


WORKDIR /app

COPY package*.json ./

RUN npm install --legacy-peer-deps --no-audit --no-fund

COPY . .

RUN npm run build --prod

FROM node:lts-alpine3.23

WORKDIR /app

COPY --from=ngbuild /app/dist/Frontend /app


ENV PORT=5000
EXPOSE 5000

CMD ["node", "/app/server/server.mjs"]
