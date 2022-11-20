FROM gradle:6.5.1-jdk11
RUN apt-get update
RUN apt-get install -y sudo
RUN curl -sL https://deb.nodesource.com/setup_10.x | sudo -E bash -
RUN apt-get install -y nodejs
RUN apt-get install -y postgresql postgresql-contrib
RUN service postgresql start && sudo -u postgres psql -c "ALTER USER postgres PASSWORD 'postgres';" && sudo -u postgres psql -c "CREATE DATABASE brainate;"
WORKDIR /app
CMD service postgresql start && gradle build