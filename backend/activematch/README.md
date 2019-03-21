# Build
mvn clean package && docker build -t ch.hec.eda/activematch .

# RUN

docker rm -f activematch || true && docker run -d -p 8080:8080 -p 4848:4848 --name activematch ch.hec.eda/activematch 