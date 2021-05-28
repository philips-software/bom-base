FROM philipssoftware/python:java

RUN python -m pip install --upgrade pip
COPY docker/requirements.txt /app/requirements.txt

RUN pip install -r /app/requirements.txt

COPY target/BOM-base*.jar /app/service.jar
COPY docker/start.sh /app/start.sh

EXPOSE 8080

RUN scancode --version

ENTRYPOINT ["/app/start.sh"]
