FROM nginx:stable-alpine3.23
LABEL authors="MostafaOsman"

RUN mkdir -p /var/cache/nginx \
    && chown -R nginx:nginx /var/cache/nginx

COPY nginx.conf /etc/nginx/nginx.conf

COPY cert/ /etc/nginx/nginx-certs
RUN chown -R nginx:nginx /etc/nginx/nginx-certs

VOLUME ["/var/cache/nginx"]

EXPOSE 80 433