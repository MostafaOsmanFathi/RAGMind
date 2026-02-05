FROM nginx:stable-alpine3.23

LABEL org.opencontainers.image.authors="Mostafa Osman <mostafa.osman.fathi@gmail.com>" \
      org.opencontainers.image.source="https://github.com/MostafaOsmanFathi/RAGMind" \
      org.opencontainers.image.version="v1.10" \
      org.opencontainers.image.licenses="MIT"

LABEL org.opencontainers.image.title="reverse-proxy" \
      org.opencontainers.image.description="NGINX reverse proxy handling TLS termination and routing for frontend and backend services"


RUN mkdir -p /var/cache/nginx \
    && chown -R nginx:nginx /var/cache/nginx

COPY nginx.conf /etc/nginx/nginx.conf

COPY cert/ /etc/nginx/nginx-certs
RUN chown -R nginx:nginx /etc/nginx/nginx-certs

VOLUME ["/var/cache/nginx"]

EXPOSE 80 433