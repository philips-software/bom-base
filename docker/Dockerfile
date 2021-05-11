FROM philipssoftware/python:java

COPY target/BOM-base*.jar /app/service.jar
COPY docker/start.sh /app/start.sh

RUN pip install scancode-toolkit==21.3.31

EXPOSE 8080

ENTRYPOINT ["/app/start.sh"]
