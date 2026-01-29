FROM node:lts-alpine3.23 AS ngbuild

LABEL authors="MostafaOsman"

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