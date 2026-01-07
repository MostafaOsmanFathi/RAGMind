FROM ollama/ollama:rocm

COPY install.sh .
RUN chmode +x install.sh && bash install.sh


EXPOSE 11434

ENTRYPOINT [ "/bin/ollama" ]
CMD [ "serve" ]
